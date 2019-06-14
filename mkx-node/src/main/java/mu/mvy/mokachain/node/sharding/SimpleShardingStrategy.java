package mu.mvy.mokachain.node.sharding;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.common.utils.Base64Utils;
import mu.mvy.mokachain.common.utils.SanityCheck;

@Component
public class SimpleShardingStrategy  implements ShardingStrategy {
	
	/**
	 * Default block size
	 */
	private int blockSize = 1;	
	
	public SimpleShardingStrategy() {
		
	}

	
	/**
	 * Implement logic for data sharding
	 * 
	 */
	@Override
	public List<EncryptedFragment> shardData(Transaction transaction) {
		List<EncryptedFragment> dataFragments = null;
		
		/**
		 * Shard encrypted bytes into blocks
		 */
		List<byte[]>  bytesInBlocks = mu.mvy.mokachain.common.utils.ByteUtils.splitBytes(transaction.getEncryptedDocument().getSealedContent(), getBlockSize()); 
		
		if (SanityCheck.isValid(bytesInBlocks)) {
			dataFragments = new ArrayList<EncryptedFragment>();
			
			int index = 0;
			for (byte[] bytesInBlock : bytesInBlocks) {
				EncryptedFragment fragment = new EncryptedFragment();
				
				fragment.setOrder(index);
				
				String rootSwarmhash = transaction.getEncryptedDocument().getMetaDataVO().getRootSwarmHash();
						
				fragment.setRootSwarmHash(rootSwarmhash);
				fragment.setPartialDataContent(bytesInBlock);
				fragment.setPartialDataHash(Base64Utils.convertByteToString(fragment.calculateHash()));
			
				dataFragments.add(fragment);
				
				++index;
			}
			
		}
		
		return dataFragments;
	}


	public int getBlockSize() {
		return blockSize;
	}


	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	

}
