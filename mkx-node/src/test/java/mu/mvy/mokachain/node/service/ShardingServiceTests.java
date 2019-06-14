package mu.mvy.mokachain.node.service;

import java.util.List;

import org.apache.logging.log4j.core.util.Assert;
import org.junit.Before;
import org.junit.Test;

import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.node.sharding.SimpleShardingStrategy;

public class ShardingServiceTests {
	
	 private ShardingService shardingService;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void shardEncryptedInformation_ok() {		
		
		shardingService = new ShardingService();	
		
		SimpleShardingStrategy simpleShardingStrategy = new SimpleShardingStrategy();
		simpleShardingStrategy.setBlockSize(5);
		
		shardingService.setShardingStrategy(simpleShardingStrategy);
		
		Transaction tx = new Transaction();
		//EncryptedDocument encryptedDocument = TestDataHelper.createEncryptedDocument();
		
		//tx.setEncryptedDocument(encryptedDocument);
				
		/**
		 * Actual method
		 */
		List<EncryptedFragment> fragments = shardingService.shardEncryptedInformation(tx);
		
		/**
		 * Asserts
		 */
		Assert.isNonEmpty(fragments);
	}

}
