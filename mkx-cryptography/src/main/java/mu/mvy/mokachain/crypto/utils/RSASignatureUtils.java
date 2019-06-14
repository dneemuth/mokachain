package mu.mvy.mokachain.crypto.utils;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RSASignatureUtils {

	 private static final Logger LOG = LogManager.getLogger(RSASignatureUtils.class);


	    /**
	     * The keyFactory defines which algorithms are used to generate the private/public keys.
	     */
	    private static KeyFactory keyFactory = null;

	    static {
	        try {
	            keyFactory = KeyFactory.getInstance("RSA");
	        } catch (NoSuchAlgorithmException e) {
	            LOG.error("Failed initializing keyFactory", e);
	        }
	    }

	    
	    /**
	     * Verify if the given signature is valid regarding the data and publicKey.
	     * @param data raw data which was signed
	     * @param signature to proof the validity of the sender
	     * @param publicKey key to verify the data was signed by owner of corresponding private key
	     * @return true if the signature verification succeeds.
	     */
	    public static boolean verify(byte[] data, byte[] signature, byte[] publicKey) throws InvalidKeySpecException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
	        // construct a public key from raw bytes
	        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
	        PublicKey publicKeyObj = keyFactory.generatePublic(keySpec);

	        // do the verification
	        Signature sig = getSignatureObj();
	        sig.initVerify(publicKeyObj);
	        sig.update(data);
	        return sig.verify(signature);
	    }

	    /**
	     * Sign given data with a private key
	     * @param data raw data to sign
	     * @param privateKey to use for the signage process
	     * @return signature of data which can be verified with corresponding public key
	     */
	    public static byte[] sign(byte[] data, byte[] privateKey) throws Exception {
	        // construct a PrivateKey-object from raw bytes
	        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
	        PrivateKey privateKeyObj = keyFactory.generatePrivate(keySpec);

	        // do the signage
	        Signature sig = getSignatureObj();
	        sig.initSign(privateKeyObj);
	        sig.update(data);
	        return sig.sign();
	    }

	    private static Signature getSignatureObj() throws NoSuchProviderException, NoSuchAlgorithmException {
	        return Signature.getInstance("SHA1withRSA");
	    }
	    
	    
	    /**
	     * Generate a random key pair.
	     * @return KeyPair containing private and public key
	     */
	    public static KeyPair generateKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException {
	    	
	    	KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	    	 keyGen.initialize(1024);
	       // SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
	        //keyGen.initialize(1024, random);       
	        return keyGen.generateKeyPair();
	    }
}
