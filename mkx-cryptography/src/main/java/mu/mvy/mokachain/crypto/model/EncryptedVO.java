package mu.mvy.mokachain.crypto.model;

import java.io.Serializable;

public class EncryptedVO  implements Serializable {

	//default serial version 
	private static final long serialVersionUID = 1L;
	
	//RSA encrypted Secret Key 
	private String encryptedSecretKey;
	
	//AES-128 encrypted data
	private byte[] encryptedContentData;

	public String getEncryptedSecretKey() {
		return encryptedSecretKey;
	}

	public void setEncryptedSecretKey(String encryptedSecretKey) {
		this.encryptedSecretKey = encryptedSecretKey;
	}

	public byte[] getEncryptedContentData() {
		return encryptedContentData;
	}

	public void setEncryptedContentData(byte[] encryptedContentData) {
		this.encryptedContentData = encryptedContentData;
	}
	
	

}
