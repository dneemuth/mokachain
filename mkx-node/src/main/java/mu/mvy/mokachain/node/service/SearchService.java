package mu.mvy.mokachain.node.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import mu.mvy.mokachain.common.domain.Block;
import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.utils.Base64Utils;

@Service
public class SearchService {
	
	 private static final Logger LOG = LogManager.getLogger(SearchService.class);	 
	 private List<Block> blockchain = new ArrayList<>();

	 /**
	  * Searches for keywords in blockchain
	  * 
	  * @param txHash
	  * @return list of blocks containing record
	  */
    public synchronized Transaction search(List<Block> blockchain, String txHash) {    	
    	LOG.info("Searching for records ... ");
    	
    	for (Block block : blockchain) {
    		for (Transaction transaction : block.getTransactions()) {
    			
    			if (!Arrays.equals(Base64Utils.convertStringToByte(txHash) , transaction.calculateHash())) {
    	             LOG.warn("Invalid hash");
    	            return null;
    	        }
    			else {
					return transaction;
				}
    		}
    	}
    	
    	return null;
    }

	public List<Block> getBlockchain() {
		return blockchain;
	}

	public void setBlockchain(List<Block> blockchain) {
		this.blockchain = blockchain;
	}		  
	    
}
