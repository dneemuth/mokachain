package mu.mvy.mokachain.crypto;

import java.security.KeyPair;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import mu.mvy.mokachain.crypto.utils.RSASignatureUtils;

public class RSASignatureUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void sign_data_rsa_ok() throws Exception {
		
		String textToSign = "textToSign";
		
		KeyPair keyPair = RSASignatureUtils.generateKeyPair();
		
		/**
		 * Sign text
		 */
		byte[] signature = RSASignatureUtils.sign(textToSign.getBytes(), keyPair.getPrivate().getEncoded());
		
		
		Assert.assertNotNull(signature);
	}
	
	
	@Test
	public void verify_signed_text_rsa_ok() throws Exception {
		
		String textToSign = "textToSign";
		
		KeyPair keyPair = RSASignatureUtils.generateKeyPair();
		
		/**
		 * Sign text
		 */
		byte[] signature = RSASignatureUtils.sign(textToSign.getBytes(), keyPair.getPrivate().getEncoded());
		
		
		
		boolean signatureStatus = RSASignatureUtils.verify(textToSign.getBytes(), signature, keyPair.getPublic().getEncoded());
		
		Assert.assertTrue(signatureStatus);
	}

}
