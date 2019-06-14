package mu.mvy.mokachain.node.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import mu.mvy.mokachain.common.domain.Block;
import mu.mvy.mokachain.common.domain.Node;
import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.statistics.LogMetrics;
import mu.mvy.mokachain.common.utils.SanityCheck;
import mu.mvy.mokachain.node.Config;


@Service
public class BlockService {

    private static final Logger LOG = LogManager.getLogger(BlockService.class);
    private final TransactionService transactionService; 
    
    private List<Block> blockchain = new ArrayList<>(); 
    
    {
    	/**
    	 * create first genesis block ( block zero )
    	 */
    	blockchain.add(new Block().createGenesisBlock());
    }
  
    @Autowired
    public BlockService(TransactionService transactionService) {    	
        this.transactionService = transactionService;           
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    /**
     * Determine the last added Block
     * @return Last Block in chain
     */
    public Block getLastBlock() {
        if (blockchain.isEmpty()) {
            return null;
        }
        return blockchain.get(blockchain.size() - 1);
    }

    /**
     * Append a new Block at the end of chain
     * @param block Block to append
     * @return true if verification succeeds and Block was appended
     */
    @LogMetrics
    public synchronized boolean append(Block block) {
        if (verify(block)) {
        	
        	/**
        	 * Allocate UTXO pool
        	 */
        	transactionService.allocateUtxPool(block.getUtxoPool());        	
        	
        	        	
        	/**
        	 * Re-verify inputs and outputs
        	 */
        	Transaction[] rTxs =  transactionService.handleTxs((Transaction[])block.getTransactions().toArray());
        	List<Transaction> convertedRtxs = Arrays.asList(rTxs);
        	if (SanityCheck.isValid(convertedRtxs)) {
        		block.setTransactions(convertedRtxs);
        	}
        	        	
        	// add block to blockchain
            blockchain.add(block);

            // remove transactions from pool
            block.getTransactions().forEach(transactionService::remove);
            return true;
        }
        return false;
    }
  
    /**
     * Download Blocks from other Node and them to the blockchain
     * @param node Node to query
     * @param restTemplate RestTemplate to use
     */
    @LogMetrics
    public void retrieveBlockchain(Node node, RestTemplate restTemplate) {
        Block[] blocks = restTemplate.getForObject(node.getAddress() + "/block", Block[].class);
        Collections.addAll(blockchain, blocks);
        LOG.info("Retrieved " + blocks.length + " blocks from node " + node.getAddress());
    }


    private boolean verify(Block block) {
        // references last block in chain
        if (blockchain.size() > 0) {
            byte[] lastBlockInChainHash = getLastBlock().getHash();
            if (!Arrays.equals(block.getPreviousBlockHash(), lastBlockInChainHash)) {
                return false;
            }
        } else {
            if (block.getPreviousBlockHash() != null) {
                return false;
            }
        }

        // correct hashes
        if (!Arrays.equals(block.getMerkleRoot(), block.calculateMerkleRoot())) {
            return false;
        }
        if (!Arrays.equals(block.getHash(), block.calculateHash())) {
            return false;
        }

        // transaction limit
        if (block.getTransactions().size() > Config.MAX_TRANSACTIONS_PER_BLOCK) {
            return false;
        }

        // all transactions in pool
        if (!transactionService.containsAll(block.getTransactions())) {
            return false;
        }

        // considered difficulty
        return block.getLeadingZerosCount() >= Config.DIFFICULTY;
    }
}
