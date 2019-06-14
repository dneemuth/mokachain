package mu.mvy.mokachain.crypto.model;

import java.io.Serializable;

public class DecryptedVO implements Serializable {

	//default serial version 
	private static final long serialVersionUID = 1L;

	//contains decrypted AES content data
	private byte[] decryptedContentData;

	public byte[] getDecryptedContentData() {
		return decryptedContentData;
	}

	public void setDecryptedContentData(byte[] decryptedContentData) {
		this.decryptedContentData = decryptedContentData;
	}
	
}
