package mu.mvy.mokachain.common.domain.sharding;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import mu.mvy.mokachain.common.utils.SanityCheck;

public class EncryptedFragment extends Fragment {
	private static final long serialVersionUID = 1L;
	
	@Override
	public byte[] calculateHash() {
		byte[] hashableData = null;
    	
    	if (SanityCheck.isValid(this.getPartialDataContent())) {
    		hashableData = ArrayUtils.addAll(this.getPartialDataContent());
    	}
        
        return DigestUtils.sha256(hashableData);
	}

	

}
