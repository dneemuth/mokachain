package mu.mvy.mokachain.crypto.utils;


/**
Consider this:

    Using an asymmetric encryption (say RSA), the server generates a key pair consisting of a public key and a private key.
    Server saves these keys in a secure location.
    We take public key and ship it in our app (client).
    When we want to transfer some sensitive data to server (at runtime), we generate a passcode (aka secret key) using a symmetric encryption (say AES).
    Using this secret key we encrypt our large texts of data quickly.
    Now we use the public key to encrypt our secret key.
    We send this encrypted data and encrypted secret key combination to server (using any commonly used way to send combination of data, like JSON)
    Server receives this combination, extracts encrypted data and encrypted secret key from it.
    Server uses private key to decrypt the encrypted secret key.
    Server uses decrypted secret key (or simply called secret key) to decrypt the encrypted data. Hence it gets the large texts of data which was sent by the client securely.

This technique is called Hybrid Cryptography.

**/

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.SerializationUtils;

import mu.mvy.mokachain.common.statistics.LogMetrics;
import mu.mvy.mokachain.crypto.model.DecryptedVO;
import mu.mvy.mokachain.crypto.model.EncryptedVO;

public class Cryptography {	
		
	private static final Logger LOG = LogManager.getLogger(Cryptography.class);
	
	/**
	 * This method uses both symmetric and asymmetric method to encrypt large text data.
	 * Uses AES-128 bit to encrypt clear text.
	 * Uses RSA to encrypt AES-encrypted text ( public/private key )
	 * 
	 * @param strToEncrypt
	 * @param secret
	 * @return
	 */
	  @LogMetrics
	public static EncryptedVO encryptData(Object objectToEncrypt, String publicKey, String secretKey) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
		
		EncryptedVO encryptedVO = null;
		String encryptedSecretKey  = null;
		
		try {
		/**
		 * Encrypt big text data using AES Encryption
		 */
	
		 byte[] serializedObject = SerializationUtils.serialize(objectToEncrypt);			
		 byte[] aesEncryptedObject = AESUtil.encrypt(serializedObject, secretKey) ;
		 
		 encryptedVO = new EncryptedVO();
		 encryptedVO.setEncryptedContentData(aesEncryptedObject);
		 
		 /**
		  * Fully encrypted data : RSA & AES protected
		  */
		 encryptedSecretKey = Base64.getEncoder().encodeToString(RSAUtil.encrypt(secretKey, publicKey));
		 encryptedVO.setEncryptedSecretKey(encryptedSecretKey);
		 
		 System.out.println(encryptedSecretKey);
			
		 } catch (NoSuchAlgorithmException e) {
	         LOG.error("Error encountered in encryptData():"  + e.getMessage());
	     }        
	
		return encryptedVO;
	}
	
	
	/**
	 * 
	 * This method uses both symmetric and asymmetric method to decrypt large text data.
	 * Uses RSA to decrypt AES-encrypted text ( public/private key )
	 * Uses AES-128 bit to decrypt clear text.
	 * 
	 * @param encryptedString
	 * @param base64PrivateKey
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	 public static DecryptedVO decryptData(byte[] encryptedDataContent, String encryptedSecretKey, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

		 byte[] decryptedContentData = null;
		 DecryptedVO decryptedVO = null;
		 
		 /**
		  * Decrypt from RSA encrypted string
		  */
		 String decryptedSecretKey = new String(RSAUtil.decrypt(encryptedSecretKey, base64PrivateKey));
			 
		 /**
		  * Decrypt from AES encrypted string
		  */
		 if (decryptedSecretKey != null) {
			 decryptedVO = new DecryptedVO();
			 /**
			  * Decrypt data by using AES algorithm
			  */
			 decryptedContentData = AESUtil.decrypt(encryptedDataContent, decryptedSecretKey);
			 /**
			  * Decrypted data in bytes
			  */
			 decryptedVO.setDecryptedContentData(decryptedContentData);
		 }
		 
		 return decryptedVO;
	 }

}
