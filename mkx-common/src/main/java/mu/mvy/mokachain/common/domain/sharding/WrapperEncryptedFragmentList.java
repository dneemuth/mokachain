package mu.mvy.mokachain.common.domain.sharding;

import java.io.Serializable;
import java.util.List;

public class WrapperEncryptedFragmentList   implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String hashedKeyStore;
	
	private List<EncryptedFragment> fragments;

	public List<EncryptedFragment> getFragments() {
		return fragments;
	}

	public void setFragments(List<EncryptedFragment> fragments) {
		this.fragments = fragments;
	}

	public String getHashedKeyStore() {
		return hashedKeyStore;
	}

	public void setHashedKeyStore(String hashedKeyStore) {
		this.hashedKeyStore = hashedKeyStore;
	}

	

}
