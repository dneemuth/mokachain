package mu.mvy.mokachain.node.service;


import java.security.KeyPair;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import mu.mvy.mokachain.common.domain.Address;
import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.utils.Base64Utils;
import mu.mvy.mokachain.crypto.model.EncryptedVO;
import mu.mvy.mokachain.crypto.utils.Cryptography;
import mu.mvy.mokachain.crypto.utils.RSASignatureUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTests {

    @Autowired private TransactionService transactionService;
    @Autowired private AddressService addressService;

    private Address address;
    private KeyPair keyPair;

    @Before
    public void setUp() throws Exception {
    	
        keyPair = RSASignatureUtils.generateKeyPair();
        address = new Address("Max Mustermann", keyPair.getPublic().getEncoded());
        addressService.add(address);
    }

    @Test
    public void addTransaction_valid() throws Exception {
        String text = "Lorem Ipsum";
        byte[] signature = RSASignatureUtils.sign(text.getBytes(), keyPair.getPrivate().getEncoded());
        Transaction transaction = new Transaction(text, address.getHash(), signature);

        boolean success = transactionService.add(transaction);
        Assert.assertTrue(success);
    }
    
    @Test
    public void addTransaction_encrypted_content_valid() throws Exception {    	
    	
    	String textToEncrypt = "BLOCK ZERO";
		String secretKey = "secret";
	    
	    //Encrypt content
		EncryptedVO  encryptedVO = Cryptography.encryptData(textToEncrypt, Base64Utils.convertByteToString(keyPair.getPublic().getEncoded()), secretKey);
        byte[] encryptedSecretKey = Base64Utils.convertStringToByte(encryptedVO.getEncryptedSecretKey());
	             
         //sign data.
	    byte[] dataToSign = encryptedVO.getEncryptedContentData();
        byte[] signature = RSASignatureUtils.sign(dataToSign, keyPair.getPrivate().getEncoded());
      		
		Transaction transaction = new Transaction(encryptedSecretKey, encryptedVO.getEncryptedContentData(), address.getHash(), signature);
    	
		boolean success = transactionService.add(transaction);
	   
		
		Assert.assertTrue(success);
    }

    @Test
    public void addTransaction_invalidText() throws Exception {
        String text = "Lorem Ipsum";
        byte[] signature = RSASignatureUtils.sign(text.getBytes(), keyPair.getPrivate().getEncoded());
        Transaction transaction = new Transaction("Fake text!!!", address.getHash(), signature);

        boolean success = transactionService.add(transaction);
        Assert.assertFalse(success);
    }

    @Test
    public void addTransaction_invalidSender() throws Exception {
        Address addressPresident = new Address("Mr. President", RSASignatureUtils.generateKeyPair().getPublic().getEncoded());
        addressService.add(addressPresident);

        String text = "Lorem Ipsum";
        byte[] signature = RSASignatureUtils.sign(text.getBytes(), keyPair.getPrivate().getEncoded());
        Transaction transaction = new Transaction(text, addressPresident.getHash(), signature);

       boolean success = transactionService.add(transaction);
       Assert.assertFalse(success);
    }
}
