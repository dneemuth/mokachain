package mu.mvy.mokachain.node.rest;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.bytes.Bytes;
import mu.mvy.mokachain.common.domain.Node;
import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.common.domain.sharding.Fragment;
import mu.mvy.mokachain.common.domain.sharding.WrapperEncryptedFragmentList;
import mu.mvy.mokachain.common.statistics.LogMetrics;
import mu.mvy.mokachain.common.utils.Base64Utils;
import mu.mvy.mokachain.common.utils.MerkleUtils;
import mu.mvy.mokachain.crypto.utils.AESUtil;
import mu.mvy.mokachain.node.Config;
import mu.mvy.mokachain.node.service.NodeService;
import mu.mvy.mokachain.node.service.SwarmService;
import mu.mvy.mokachain.node.service.TransactionService;


@RestController()
@RequestMapping("transaction")
public class TransactionController {
	private static final Logger LOG = LogManager.getLogger(TransactionController.class);

    private final TransactionService transactionService;
    private final SwarmService swarmService;
    private final NodeService nodeService;
    
    private Random randomGenerator;

    @Autowired
    public TransactionController(TransactionService transactionService, SwarmService swarmService, NodeService nodeService) {
        this.transactionService = transactionService;
        this.nodeService = nodeService;
        this.swarmService = swarmService;
    }

    /**
     * Retrieve all Transactions, which aren't in a block yet
     * @return JSON list of Transactions
     */
    @RequestMapping
    Set<Transaction> getTransactionPool() {
        return transactionService.getTransactionPool();
    }
    

    /**
     * Add a new Transaction to the pool.
     * It is expected that the transaction has a valid signature and the correct hash.
     *
     * @param transaction the Transaction to add
     * @param publish if true, this Node is going to inform all other Nodes about the new Transaction
     * @param response Status Code 202 if Transaction accepted, 406 if verification fails
     */
    @LogMetrics
    @RequestMapping(method = RequestMethod.PUT)
    void addTransaction(@RequestBody Transaction transaction, @RequestParam(required = false) Boolean publish, HttpServletResponse response) {
        LOG.info("Add transaction " + Base64.encodeBase64String(transaction.getHash()));
        boolean success = transactionService.add(transaction);

        if (success) {
        	
            response.setStatus(HttpServletResponse.SC_ACCEPTED);

            if (publish != null && publish) {  
            	
            	List<EncryptedFragment> diffusedFragments = null;
                    	
            	/**
            	 * Upload back-up fragments (in whole) to main node as backup
            	 */
            
            	try {
            		
            		URL swarmUploadUrl = new URL((Config.MASTER_NODE_ADDRESS));
            		Node masterNode = new Node(swarmUploadUrl);
            		
            		/**
            		 * Compress data content
            		 */
            		Bytes compressedDataBytes = Bytes.wrap(transaction.getEncryptedDocument().getSealedContent()).transform(at.favre.lib.bytes.BytesTransformers.compressGzip());
            		transaction.getEncryptedDocument().setSealedContent(compressedDataBytes.array());
            		
            		
            		/**
            		 * Shard data bytes into fragments
            		 */
                    diffusedFragments =  swarmService.shardEncryptedData(transaction, Config.BLOCK_SIZE);          
                    
                    /**
                     * calculate merkle root
                     */
                    String merkleSwarmRootHash = Base64Utils.convertByteToString(MerkleUtils.calculateMerkleRoot(diffusedFragments));
                    diffusedFragments.get(0).setRootSwarmHash(merkleSwarmRootHash);
                    
            	 	
        			WrapperEncryptedFragmentList wrapperEncryptedFragmentList = new WrapperEncryptedFragmentList();
        			wrapperEncryptedFragmentList.setFragments(diffusedFragments);
        		    
        			/**
        			 * Add encrypted URL
        			 */
        			List<byte[]> swarmBackupUploads = new ArrayList<byte[]>();        			
        			String keyStore = Config.MASTER_NODE_ADDRESS + " " + diffusedFragments.get(0).getPartialDataHash() + " " + Config.MASTER_NODE_SWARM_URL;        			
        			        			
        			byte[] encryptedMasterNode = AESUtil.encrypt(Base64Utils.convertStringToByte(keyStore), Config.MOKACHAIN_SECRET_PASS);       					
        			swarmBackupUploads.add(encryptedMasterNode);
	            	
	            	transaction.getEncryptedDocument().getMetaDataVO().setSwarmNodeLocations(swarmBackupUploads);	
	            	
	            	/**
	            	 * Add SHA-256 Hashing to record persistence
	            	 */
	            	String sha256hex = DigestUtils.sha256Hex(keyStore);
	            	wrapperEncryptedFragmentList.setHashedKeyStore(sha256hex);
	            	
	            	/**
	            	 * Send data over network
	            	 */
	            	nodeService.broadcastSpecificPost(masterNode, "swarm/addFragmentList", wrapperEncryptedFragmentList);
	            	
            	
            	} catch (MalformedURLException e) {
				}
            	
            	
            	/**
            	 * Send fragments over p2p network
            	 */
            	
            	Set<Node> nodesCollected = nodeService.getKnownNodes();
            	
             	
            	if (nodesCollected.size() > 1) {
            		
            		// Creating a List of HashSet elements
                    List<Node> convertedNodes = new ArrayList<Node>(nodesCollected);
            		List<EncryptedFragment> fragments =  swarmService.shardEncryptedData(transaction, nodesCollected.size());
            
            		/**
            		 * split encrypted data into fragments
            		 */
                	diffusedFragments =  swarmService.shardEncryptedData(transaction, nodesCollected.size());
                	
                	/**
                	 * calculate root merkle for file swarming.       
                	 */
                	String merkleSwarmRootHash = Base64.encodeBase64String(MerkleUtils.calculateMerkleRoot(diffusedFragments));
            		
            		/**
                	 * Scatter fragments over network
                	 */            	
                	List<byte[]> swarmNodeUrls = new ArrayList<byte[]>();
                	for (Fragment fragment : diffusedFragments) {
                		 
                		 //randomize node selection
                		 int index = randomGenerator.nextInt(fragments.size());  
                		 
                		 /**
                		  * Encrypt Swarm Urls
                		  */
                		 String keyStore = convertedNodes.get(index).getAddress() + " " + fragment.getPartialDataHash() + " " + fragment.getOrder(); 
                	     byte[] encryptedSwarmUrl = AESUtil.encrypt(Base64Utils.convertStringToByte(keyStore), Config.MOKACHAIN_SECRET_PASS);                 	
                		 swarmNodeUrls.add(encryptedSwarmUrl);
                		 
                		 
                		 /**
     	            	 * Add SHA-256 Hashing to record persistence
     	            	 */
     	            	 String sha256hex = DigestUtils.sha256Hex(keyStore);
     	            	 fragment.setHashedKeyStore(sha256hex);                		 
                		 
                		 fragment.setRootSwarmHash(merkleSwarmRootHash);                		 
                		 nodeService.broadSpecifiedCastPut(convertedNodes.get(index), "swarm", fragment);
                	}   
                	
                	transaction.getEncryptedDocument().getMetaDataVO().setSwarmNodeLocations(swarmNodeUrls);
            		
            	}
            	
            	
            	/**
            	 * calculate merkle swarm root
            	 */
            	String merkleSwarmRootHash = Base64Utils.convertByteToString(MerkleUtils.calculateMerkleRoot(diffusedFragments));
            	transaction.getEncryptedDocument().getMetaDataVO().setRootSwarmHash(merkleSwarmRootHash);
            	
            	            	
            	/**
            	 * broadcast transactions
            	 */
                nodeService.broadcastPut("transaction", transaction);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }
    
    

}
