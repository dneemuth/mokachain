package mu.mvy.mokachain.node.sharding;

import java.util.List;

import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;

/**
 * Sharding strategy - Interface for sharding sensitive data
 * 
 * @author javed
 *
 */
public interface ShardingStrategy {

	/**
	 * 
	 * This method will shard the provided sealed object (in bytes) into multiple fragments.
	 * 
	 * @param sealedObject
	 * @return list of sharded objects
	 */
	 public List<EncryptedFragment> shardData(Transaction transaction);
}
