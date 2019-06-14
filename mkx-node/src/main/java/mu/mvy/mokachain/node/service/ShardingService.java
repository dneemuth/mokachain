package mu.mvy.mokachain.node.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.common.statistics.LogMetrics;
import mu.mvy.mokachain.node.sharding.ShardingStrategy;

@Service
public class ShardingService {
	 private static final Logger LOG = LogManager.getLogger(ShardingService.class);	 	 

	 private ShardingStrategy shardingStrategy;	 
	 
	  //this can be set at runtime by the application preferences
	  public void setShardingStrategy(ShardingStrategy shardingStrategy) {
	    this.shardingStrategy = shardingStrategy;
	  }
	 
	 
	 /**
	  * The following method will shard the encrypted content into multiple fragments
	  * 
	  * @param sealedObject
	  * @return
	  */
	  @LogMetrics
	 public List<EncryptedFragment> shardEncryptedInformation(Transaction transaction){	
		 LOG.debug("ShardingService ::shardEncryptedInformation - splitting up data...");		 
		 return shardingStrategy.shardData(transaction);
	 }
	
}
