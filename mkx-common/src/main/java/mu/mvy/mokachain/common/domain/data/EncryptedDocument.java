package mu.mvy.mokachain.common.domain.data;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import mu.mvy.mokachain.common.domain.interfaces.Hashable;
import mu.mvy.mokachain.common.utils.SanityCheck;

public class EncryptedDocument implements Document , Hashable  {

	private static final long serialVersionUID = 1L;
	
	/**
	 * RSA-encrypted secret key for encrypted AES data
	 */
    private byte[] key; 
    
    /**
	 * Attribute to contain all meta data related to stored data
	 */
	private MetaInformation metaDataVO;
    
    /**
	 * AES-encrypted content data converted to equivalent bytes
	 */
	private byte[] sealedContent;

	public byte[] getSealedContent() {
		return sealedContent;
	}

	public void setSealedContent(byte[] sealedContent) {
		this.sealedContent = sealedContent;
	}
       
    //default constructor
    public EncryptedDocument() {}
        
    //overloaded constructor
    public EncryptedDocument(byte[] sealedContent, byte[] secretKey) throws UnsupportedEncodingException {
    	
    	/**
    	 * meta data
    	 */
    	metaDataVO = new MetaInformation();    	
    	metaDataVO.setEncodingFormat("mkx-encrypt");    	
    	this.key = secretKey;
    	
    	Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
    	metaDataVO.setCreatedOn(currentTimestamp);
    	
    	
    	/**
    	 * content data
    	 */    	  	
    	this.setSealedContent(sealedContent);  
    	
    }
    
    public MetaInformation getMetaDataVO() {
		return metaDataVO;
	}

	public void setMetaDataVO(MetaInformation metaDataVO) {
		this.metaDataVO = metaDataVO;
	}
	

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}
	

	@Override
	public byte[] calculateHash() {
		byte[] hashableData = null;
    	
		//06.09.2018 - added hash for encrypted data vault
    	if (SanityCheck.isValid(this.getSealedContent())) {
    		hashableData = ArrayUtils.addAll(this.getSealedContent());
    	}
               
        return DigestUtils.sha256(hashableData);
	}

     
}
