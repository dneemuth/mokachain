package mu.mvy.mokachain.crypto;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.junit.Before;
import org.junit.Test;

import mu.mvy.mokachain.crypto.utils.AESUtil;

public class ASEUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void encrypt_decrypt_text() throws UnsupportedEncodingException {
			
			String secretKey = "secretKey";
			String bigTextString = TestDataHelper.createLargeString();
			
			/**
			 * Encrypt bytes
			 */
			
			byte[] bigTextInBytes = bigTextString.getBytes("UTF-8");
		    byte[] encryptedBytes = AESUtil.encrypt(bigTextInBytes, secretKey) ;
		    
		    System.out.println("encrypted text:"+ Base64.getEncoder().encodeToString(encryptedBytes));
		    
		    
		    /**
		     * Decrypt bytes
		     */
		    byte[] decryptedBytes = AESUtil.decrypt(encryptedBytes, secretKey) ;	   
		    
		    System.out.println("decrypted text:"+ new String(decryptedBytes));
	}

}
