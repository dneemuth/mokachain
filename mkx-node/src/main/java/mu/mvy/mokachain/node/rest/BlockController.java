package mu.mvy.mokachain.node.rest;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import at.favre.lib.bytes.Bytes;
import mu.mvy.mokachain.common.domain.Block;
import mu.mvy.mokachain.common.domain.Node;
import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.domain.data.EncryptedDocument;
import mu.mvy.mokachain.common.domain.data.KeyStoreID;
import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.common.domain.sharding.WrapperEncryptedFragmentList;
import mu.mvy.mokachain.common.utils.Base64Utils;
import mu.mvy.mokachain.common.utils.SanityCheck;
import mu.mvy.mokachain.crypto.utils.AESUtil;
import mu.mvy.mokachain.node.Config;
import mu.mvy.mokachain.node.service.BlockService;
import mu.mvy.mokachain.node.service.MiningService;
import mu.mvy.mokachain.node.service.NodeService;
import mu.mvy.mokachain.node.service.NotaryService;
import mu.mvy.mokachain.node.service.SearchService;
import mu.mvy.mokachain.node.service.SwarmService;


@RestController
@RequestMapping("block")
public class BlockController {

	private static final Logger LOG = LogManager.getLogger(BlockController.class);

    private final BlockService blockService;
    private final NodeService nodeService;
    private final MiningService miningService;
    private final SearchService searchService;
    private final SwarmService swarmService;
    private final NotaryService notaryService;
    

    @Autowired
    public BlockController(BlockService blockService, NodeService nodeService, MiningService miningService, SearchService searchService,SwarmService swarmService, NotaryService notaryService) {
        this.blockService = blockService;
        this.nodeService = nodeService;
        this.miningService = miningService;  
        this.searchService = searchService;
        this.swarmService = swarmService;
        this.notaryService = notaryService;
    }

    /**
     * Retrieve all Blocks in order of mine date, also known as Blockchain
     * @return JSON list of Blocks
     */
    @RequestMapping
    List<Block> getBlockchain() {
        return blockService.getBlockchain();
    }
    
    
    /**
     * Retrieve all Blocks in order of mine date, also known as Blockchain
     * @return JSON list of Blocks
     */
    @RequestMapping(value = "/status", method = RequestMethod.GET)
	 @ResponseBody
    List<Block> status() {
        return blockService.getBlockchain();
    }
    
    /**
     * Get specified transaction on blockchain
     * @return JSON list of addresses
     * @throws IOException 
     */
	 @RequestMapping(value = "/tx", method = RequestMethod.GET)
	 @ResponseBody
     Transaction getFragment(@RequestParam(required = true) String txHash, HttpServletResponse response) throws IOException {
       
		System.out.println("hash:" + txHash); 
		
        Transaction tx =  searchService.search(getBlockchain(), txHash);		
        String hashedKeyStore  = null;        
       
        /**
         * Collect fragments over network
         */
        List<byte[]> swarmNodeUrls = tx.getEncryptedDocument().getMetaDataVO().getSwarmNodeLocations();
        
        List<KeyStoreID> swarmNodeKeyStores = null;
        
        if (!swarmNodeUrls.isEmpty()) {
        	swarmNodeKeyStores = new ArrayList<KeyStoreID>();
        	for (byte[] encryptedNodeUrl : swarmNodeUrls) {
        		/**
        		 * decrypted urls
        		 */
        		String decryptedSwarmKey = Base64Utils.convertByteToString(AESUtil.decrypt(encryptedNodeUrl, Config.MOKACHAIN_SECRET_PASS));
        		
        		StringTokenizer st = new StringTokenizer(decryptedSwarmKey, " ");
        		List<String> tokens = new ArrayList<>();
        		 while (st.hasMoreTokens()) {  
        			 tokens.add(st.nextToken());
        	     }          		
        	
        		 
        		Node node = new Node(new URL(tokens.get(0)));        		
        		String hashedContent = tokens.get(1);        		
        		String fragmentOrder = tokens.get(2);
        		
        		 hashedKeyStore = DigestUtils.sha256Hex(decryptedSwarmKey);
        		
        		swarmNodeKeyStores.add(new KeyStoreID(hashedKeyStore, node, hashedContent, fragmentOrder));
        	}
        	
        }
        
        
    	RestTemplate restTemplate = new RestTemplate();    	
    	
    	List<EncryptedFragment> encryptedFragments  = null;
    	if (swarmNodeKeyStores != null && swarmNodeKeyStores.size() == 1) {
    		
    		encryptedFragments = new ArrayList<EncryptedFragment>(); 
    	    		
    		WrapperEncryptedFragmentList wrapperEncryptedFragmentList = restTemplate.getForObject(
  				  "http://" +  swarmNodeKeyStores.get(0).getNode().getAddress().getHost() + ":" + swarmNodeKeyStores.get(0).getNode().getAddress().getPort() +  "/swarm/getListFragment?key=" + swarmNodeKeyStores.get(0).getHashedStoreKey(),
  				WrapperEncryptedFragmentList.class);
    		
    		encryptedFragments = wrapperEncryptedFragmentList.getFragments();
    	}
        
    	else 
    	{
    		int keyIndex = 0;
	        for (KeyStoreID swarmKeyStore : swarmNodeKeyStores) {
	        	
	        	encryptedFragments = new ArrayList<EncryptedFragment>();       
	     		
	    		EncryptedFragment fragment = restTemplate.getForObject(
	    				  "http://" +  swarmKeyStore.getNode().getAddress().getHost() + ":" +  swarmKeyStore.getNode().getAddress().getPort() +  "/swarm/getFragment?key=" + swarmKeyStore.getHashedStoreKey(),
	    				  EncryptedFragment.class);	
	    		
	    		encryptedFragments.add(fragment);
	    		
	    		++keyIndex;
	        }  
    	}
        
        
        /**
         * No fragments missing - Ok to proceed
         */
        if (SanityCheck.isValid(encryptedFragments) && encryptedFragments != null&& encryptedFragments.size() == swarmNodeKeyStores.size()) {
	        EncryptedDocument encryptedDocument =  swarmService.collectFragmentsOverNetwork(encryptedFragments);
	        tx.getEncryptedDocument().setSealedContent(encryptedDocument.getSealedContent());
        
        }
        else {
        	//call any supernode to assemble data
        	Node supernode = new Node(new URL(Config.MASTER_NODE_ADDRESS));        	
        	
        	WrapperEncryptedFragmentList fragmentListWrapper = (WrapperEncryptedFragmentList) restTemplate.getForObject(
  				  "http://" +  supernode.getAddress().getHost() + ":" + supernode.getAddress().getPort() +  "/swarm/getListFragment?key=" + hashedKeyStore,
  				WrapperEncryptedFragmentList.class);
        	
        	 EncryptedDocument encryptedDocument =  swarmService.collectFragmentsOverNetwork(fragmentListWrapper.getFragments());
 	         tx.getEncryptedDocument().setSealedContent(encryptedDocument.getSealedContent());
        	
        }
        
        /**
         * 
         * Verify integrity of encrypted data
         */
          
        boolean tamperProofStatus = notaryService.verifyEncryptedDocument(tx, encryptedFragments);
        tx.setTamperProofStatus(tamperProofStatus);
        
        /**
         * If tamper not changed; decompress data bytes
         */
        if (tamperProofStatus) {
    	   
        	/**
        	 * Decompress stuff...
        	 */
        	
        	Bytes decompressedDataBytes = Bytes.wrap(tx.getEncryptedDocument().getSealedContent()).transform(at.favre.lib.bytes.BytesTransformers.decompressGzip());
    		tx.getEncryptedDocument().setSealedContent(decompressedDataBytes.array());
        }
        
        return tx;
    }
    
    
    /**
     * Add a new Block at the end of the Blockchain.
     * It is expected that the Block is valid, see BlockService.verify(Block) for details.
     *
     * @param block the Block to add
     * @param publish if true, this Node is going to inform all other Nodes about the new Block
     * @param response Status Code 202 if Block accepted, 406 if verification fails
     */
    @RequestMapping(method = RequestMethod.PUT)
    void addBlock(@RequestBody Block block, @RequestParam(required = false) Boolean publish, HttpServletResponse response) {
        LOG.info("Add block " + Base64.encodeBase64String(block.getHash()));
        
        boolean success = blockService.append(block);

        if (success) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);

            if (publish != null && publish) {
                nodeService.broadcastPut("block", block);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

    /**
     * Start mining of Blocks on this Node in a Thread
     */
    @RequestMapping(path = "start-miner")
    public void startMiner() {
        miningService.startMiner();
    }

    /**
     * Stop mining of Blocks on this Node
     */
    @RequestMapping(path = "stop-miner")
    public void stopMiner() {
        miningService.stopMiner();
    }

}
