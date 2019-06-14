package mu.mvy.mokachain.node.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.domain.data.EncryptedDocument;
import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.common.domain.sharding.SortFragmentByOrder;
import mu.mvy.mokachain.common.statistics.LogMetrics;
import mu.mvy.mokachain.common.utils.SanityCheck;
import mu.mvy.mokachain.node.sharding.SimpleShardingStrategy;

@Service
public class SwarmService {	
	private static final Logger LOG = LogManager.getLogger(SwarmService.class);	

	private final ShardingService shardingService;	
		
	 @Autowired
    public SwarmService(ShardingService shardingService) {	    	
        this.shardingService = shardingService;         
    }
	
	 /**
	  * Shard critical data from transactions
	  * 
	  * @param keywords
	  * @return list of blocks containing record
	  */
	 @LogMetrics
   public synchronized List<EncryptedFragment> shardEncryptedData(Transaction transaction, Integer blockSize) {    	
    	LOG.info("Sharding transaction data ... " );
    	
    	List<EncryptedFragment> fragments = null;
   	    if (SanityCheck.isValid(transaction)) {
   	    	/**
        	 * Sharding service - Shard encrypted data and send to different nodes.
        	 */
   	    	SimpleShardingStrategy simpleShardingStrategy = new SimpleShardingStrategy();
   	    	simpleShardingStrategy.setBlockSize(blockSize);   	    	
   	    	
        	shardingService.setShardingStrategy(simpleShardingStrategy);
        	fragments = shardingService.shardEncryptedInformation(transaction);  
   	    }   	    
   	    return fragments;
   }
   
   
   

	 /**
	  * Collect all fragments from network
	  * 
	  * @param keywords
	  * @return list of blocks containing record
	 * @throws IOException 
	  */
	 @LogMetrics
   public synchronized EncryptedDocument collectFragmentsOverNetwork(List<EncryptedFragment> encryptedFragments) throws IOException {    	
	   LOG.info("collecting swarm transaction data ... " ); 
	   
	   /**
	    * Sort fragments by order
	    */
	   Collections.sort(encryptedFragments, new SortFragmentByOrder()); 
	   
	   EncryptedDocument encryptedDocument = new EncryptedDocument();
	     
	   ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
	   for (EncryptedFragment encryptedFragment : encryptedFragments) {		   
		   outputStream.write( encryptedFragment.getPartialDataContent());
	   }
	   
	   byte assembledEncryptedContent[] = outputStream.toByteArray( );
	   encryptedDocument.setSealedContent(assembledEncryptedContent);
 	   	    
 	   return encryptedDocument;
   }
 
 

}
