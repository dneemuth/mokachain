package mu.mvy.mokachain.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.SerializationUtils;

import mu.mvy.mokachain.common.domain.data.EncryptedDocument;
import mu.mvy.mokachain.crypto.model.DecryptedVO;
import mu.mvy.mokachain.crypto.model.EncryptedVO;
import mu.mvy.mokachain.crypto.utils.Cryptography;

public class CryptographyTest {

	@Before
	public void setUp() throws Exception {
	}
	
	
	@Test
	public void decrypt_object_data() throws InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException{
		
		/**
		 * Parameters
		 */
	    String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgFGVfrY4jQSoZQWWygZ83roKXWD4YeT2x2p41dGkPixe73rT2IW04glagN2vgoZoHuOPqa5and6kAmK2ujmCHu6D1auJhE2tXP+yLkpSiYMQucDKmCsWMnW9XlC5K7OSL77TXXcfvTvyZcjObEz6LIBRzs6+FqpFbUO9SJEfh6wIDAQAB";
	    String base64PrivateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKAUZV+tjiNBKhlBZbKBnzeugpdYPhh5PbHanjV0aQ+LF7vetPYhbTiCVqA3a+Chmge44+prlqd3qQCYra6OYIe7oPVq4mETa1c/7IuSlKJgxC5wMqYKxYydb1eULkrs5IvvtNddx+9O/JlyM5sTPosgFHOzr4WqkVtQ71IkR+HrAgMBAAECgYAkQLo8kteP0GAyXAcmCAkA2Tql/8wASuTX9ITD4lsws/VqDKO64hMUKyBnJGX/91kkypCDNF5oCsdxZSJgV8owViYWZPnbvEcNqLtqgs7nj1UHuX9S5yYIPGN/mHL6OJJ7sosOd6rqdpg6JRRkAKUV+tmN/7Gh0+GFXM+ug6mgwQJBAO9/+CWpCAVoGxCA+YsTMb82fTOmGYMkZOAfQsvIV2v6DC8eJrSa+c0yCOTa3tirlCkhBfB08f8U2iEPS+Gu3bECQQCrG7O0gYmFL2RX1O+37ovyyHTbst4s4xbLW4jLzbSoimL235lCdIC+fllEEP96wPAiqo6dzmdH8KsGmVozsVRbAkB0ME8AZjp/9Pt8TDXD5LHzo8mlruUdnCBcIo5TMoRG2+3hRe1dHPonNCjgbdZCoyqjsWOiPfnQ2Brigvs7J4xhAkBGRiZUKC92x7QKbqXVgN9xYuq7oIanIM0nz/wq190uq0dh5Qtow7hshC/dSK3kmIEHe8z++tpoLWvQVgM538apAkBoSNfaTkDZhFavuiVl6L8cWCoDcJBItip8wKQhXwHp0O3HLg10OEd14M58ooNfpgt+8D8/8/2OOFaR0HzA+2Dm";
		
	    String secretKey = "secretKey";
		
	    EncryptedDocument encryptedDocument = TestDataHelper.createEncryptedDocument();
		
		
		/**
		 * Encrypt data fully.
		 */
		EncryptedVO encryptedVO = Cryptography.encryptData(encryptedDocument, publicKey ,secretKey);
		
		
		/**
		 * Decrypt data fully.
		 */
		DecryptedVO decryptedVO = Cryptography.decryptData(encryptedVO.getEncryptedContentData(),encryptedVO.getEncryptedSecretKey(), base64PrivateKey);
		
		/**
		 * Asserts
		 */
		System.out.println("Decrypted content:" + decryptedVO.getDecryptedContentData());
		
		
		/**
		 * Deserialize bytes to object
		 */
		EncryptedDocument convertedEncryptedDocument = (EncryptedDocument) SerializationUtils.deserialize(decryptedVO.getDecryptedContentData());
		
		
		/**
		 * Asserts
		 */
		Assert.assertNotNull(convertedEncryptedDocument);
		
		
	}
	
	
	@Test
	public void encrypt_object_data() throws InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException{
		
		/**
		 * Parameters
		 */
	    String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgFGVfrY4jQSoZQWWygZ83roKXWD4YeT2x2p41dGkPixe73rT2IW04glagN2vgoZoHuOPqa5and6kAmK2ujmCHu6D1auJhE2tXP+yLkpSiYMQucDKmCsWMnW9XlC5K7OSL77TXXcfvTvyZcjObEz6LIBRzs6+FqpFbUO9SJEfh6wIDAQAB";
	    String secretKey = "secretKey";
		
	    EncryptedDocument encryptedDocument = TestDataHelper.createEncryptedDocument();
		
		
		/**
		 * Encrypt data fully.
		 */
		EncryptedVO encryptedVO = Cryptography.encryptData(encryptedDocument, publicKey ,secretKey);
		

		/**
		 * Asserts
		 */
		Assert.assertNotNull(encryptedVO);
		
	}
	
	

	@Test
	public void encrypt_text_data() throws InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException {
		
		/**
		 * Parameters
		 */
	    String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgFGVfrY4jQSoZQWWygZ83roKXWD4YeT2x2p41dGkPixe73rT2IW04glagN2vgoZoHuOPqa5and6kAmK2ujmCHu6D1auJhE2tXP+yLkpSiYMQucDKmCsWMnW9XlC5K7OSL77TXXcfvTvyZcjObEz6LIBRzs6+FqpFbUO9SJEfh6wIDAQAB";
		String strToEncrypt = TestDataHelper.createLargeString();
		
		String secretKey = "secretKey";
		
		/**
		 * Encrypt data fully.
		 */
		EncryptedVO encryptedVO = Cryptography.encryptData(strToEncrypt, publicKey ,secretKey);
		
		/**
		 * Asserts
		 */
		Assert.assertNotNull(encryptedVO);
	}
	
	@Test
	public void decrypt_text_data() throws InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException {
		
		/**
		 * Parameters
		 */
	    String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgFGVfrY4jQSoZQWWygZ83roKXWD4YeT2x2p41dGkPixe73rT2IW04glagN2vgoZoHuOPqa5and6kAmK2ujmCHu6D1auJhE2tXP+yLkpSiYMQucDKmCsWMnW9XlC5K7OSL77TXXcfvTvyZcjObEz6LIBRzs6+FqpFbUO9SJEfh6wIDAQAB";
		String base64PrivateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKAUZV+tjiNBKhlBZbKBnzeugpdYPhh5PbHanjV0aQ+LF7vetPYhbTiCVqA3a+Chmge44+prlqd3qQCYra6OYIe7oPVq4mETa1c/7IuSlKJgxC5wMqYKxYydb1eULkrs5IvvtNddx+9O/JlyM5sTPosgFHOzr4WqkVtQ71IkR+HrAgMBAAECgYAkQLo8kteP0GAyXAcmCAkA2Tql/8wASuTX9ITD4lsws/VqDKO64hMUKyBnJGX/91kkypCDNF5oCsdxZSJgV8owViYWZPnbvEcNqLtqgs7nj1UHuX9S5yYIPGN/mHL6OJJ7sosOd6rqdpg6JRRkAKUV+tmN/7Gh0+GFXM+ug6mgwQJBAO9/+CWpCAVoGxCA+YsTMb82fTOmGYMkZOAfQsvIV2v6DC8eJrSa+c0yCOTa3tirlCkhBfB08f8U2iEPS+Gu3bECQQCrG7O0gYmFL2RX1O+37ovyyHTbst4s4xbLW4jLzbSoimL235lCdIC+fllEEP96wPAiqo6dzmdH8KsGmVozsVRbAkB0ME8AZjp/9Pt8TDXD5LHzo8mlruUdnCBcIo5TMoRG2+3hRe1dHPonNCjgbdZCoyqjsWOiPfnQ2Brigvs7J4xhAkBGRiZUKC92x7QKbqXVgN9xYuq7oIanIM0nz/wq190uq0dh5Qtow7hshC/dSK3kmIEHe8z++tpoLWvQVgM538apAkBoSNfaTkDZhFavuiVl6L8cWCoDcJBItip8wKQhXwHp0O3HLg10OEd14M58ooNfpgt+8D8/8/2OOFaR0HzA+2Dm";
		
		/**
		 * Encrypt large text
		 */
		String strToEncrypt = TestDataHelper.createLargeString();
		String secretKey = "secretKey";
		EncryptedVO encryptedVO = Cryptography.encryptData(strToEncrypt, publicKey ,secretKey);
		
		/**
		 * Decrypt data fully.
		 */
		DecryptedVO decryptedVO = Cryptography.decryptData(encryptedVO.getEncryptedContentData(),encryptedVO.getEncryptedSecretKey(), base64PrivateKey);
		
		/**
		 * Asserts
		 */
		System.out.println("Decrypted content:" + decryptedVO.getDecryptedContentData());
		Assert.assertNotNull(decryptedVO);
	}

}
