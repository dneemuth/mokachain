package mu.mvy.mokachain.node.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.common.statistics.LogMetrics;
import mu.mvy.mokachain.common.utils.Base64Utils;
import mu.mvy.mokachain.common.utils.MerkleUtils;

/**
 * This service will verify the integrity of the assembled encrypted information.
 * 
 * @author javed.neemuth
 *
 */
@Service
public class NotaryService {
	
	
	/**
	 * this service will compare the merkle root for the fragment hashes and ensure
	 * data has not been tampered with.
	 * 
	 * @param transaction
	 * @param encryptedFragments
	 * @return
	 */
	@LogMetrics
	public boolean verifyEncryptedDocument(Transaction transaction, List<EncryptedFragment> encryptedFragments) {	
		
		if (!Arrays.equals(Base64Utils.convertStringToByte(transaction.getEncryptedDocument().getMetaDataVO().getRootSwarmHash()) , MerkleUtils.calculateMerkleRoot(encryptedFragments) )) {
            return false;
        }
		
		return true;
	}

}
