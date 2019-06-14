package mu.mvy.mokachain;

import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import mu.mvy.mokachain.common.domain.Address;
import mu.mvy.mokachain.common.domain.Node;
import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.common.domain.sharding.WrapperEncryptedFragmentList;
import mu.mvy.mokachain.common.utils.Base64Utils;
import mu.mvy.mokachain.crypto.model.DecryptedVO;
import mu.mvy.mokachain.crypto.model.EncryptedVO;
import mu.mvy.mokachain.crypto.utils.Cryptography;
import mu.mvy.mokachain.crypto.utils.RSASignatureUtils;

public class RestClientTests {
	
	//Generate signature
    private KeyPair keyPair = null;
    
    private byte[] base64publicKey = null;
    private  byte[] base64privateKey = null;

	@Before
	public void setUp() throws Exception {		
		keyPair = RSASignatureUtils.generateKeyPair();
		
		base64publicKey = keyPair.getPublic().getEncoded();
	    base64privateKey = keyPair.getPrivate().getEncoded();
	}

	@Test
	public void address_ok() {
		
		RestTemplate restTemplate = new RestTemplate();
		
        Address address = new Address("test", base64publicKey);
        restTemplate.put("http://localhost:8080/address?publish=true", address);
        
        System.out.println("Hash of new address: " + Base64.encodeBase64String(address.getHash()));
	}
	
	
	@Test
	public void publish_transaction_ok() throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();		
		
		Address address = new Address("test", base64publicKey);
        restTemplate.put("http://localhost:8080/address?publish=true", address);
			
		String textToEncrypt = "BLOCK ZERO";
		String secretKey = "secret";
	    
	    //Encrypt content
		EncryptedVO  encryptedVO = Cryptography.encryptData(textToEncrypt, Base64Utils.convertByteToString(base64publicKey), secretKey);
        byte[] encryptedSecretKey = Base64Utils.convertStringToByte(encryptedVO.getEncryptedSecretKey());
        
        
        //display private key
        System.out.println("base64privateKey :" + Base64Utils.convertByteToString(base64privateKey));
	     
         
         //sign data.
	    byte[] dataToSign = encryptedVO.getEncryptedContentData();    
        byte[] signature = RSASignatureUtils.sign(dataToSign, base64privateKey);
        
        byte[] senderHash = address.getHash();
		
		Transaction tx = new Transaction(encryptedSecretKey, encryptedVO.getEncryptedContentData(), senderHash, signature);

        restTemplate.put("http://localhost:8080/transaction?publish=true", tx);
        
        System.out.println("Hash of new transaction: " + Base64.encodeBase64String(tx.getHash()));
	}
	
	@Test
	public void swarm_publish_data_ok() {
		
		RestTemplate restTemplate = new RestTemplate();
		
		EncryptedFragment  encryptedFragment = new EncryptedFragment();
		encryptedFragment.setOrder(new Integer(1));
		encryptedFragment.setPartialDataHash("refx100020202020202020202002020020.02");
		encryptedFragment.setRootSwarmHash("rootswarmencryptedFragmenthash");
		encryptedFragment.setPartialDataContent(null);
		
        restTemplate.put("http://localhost:8080/swarm", encryptedFragment);
        
        System.out.println("");
	}
	
	// restTemplate.postForLocation(node.getAddress() + "/" + endpoint, data);
	
	@Test
	public void swarm_publish_data_list_ok() {
		
		RestTemplate restTemplate = new RestTemplate();
		
		List<EncryptedFragment> listFragment = new ArrayList<EncryptedFragment>();
		
		EncryptedFragment  encryptedFragment = new EncryptedFragment();
		encryptedFragment.setOrder(new Integer(1));
		encryptedFragment.setPartialDataHash("refx100020202020202020202002020020.02");
		encryptedFragment.setRootSwarmHash("rootswarmencryptedFragmenthash");
		encryptedFragment.setPartialDataContent(null);
		
		listFragment.add(encryptedFragment);
		
		WrapperEncryptedFragmentList  wrapperEncryptedFragmentList = new WrapperEncryptedFragmentList();
		wrapperEncryptedFragmentList.setFragments(listFragment);
		
		
        restTemplate.postForLocation("http://localhost:8080/swarm/addFragmentList", wrapperEncryptedFragmentList);
        
        System.out.println("");
	}
	
	
	@Test
	public void swarm_collect_data_list_ok() {
		
		RestTemplate restTemplate = new RestTemplate();
		
		WrapperEncryptedFragmentList response = restTemplate.getForObject(
				  "http://localhost:8080/swarm/getListFragment?key=" + "rootswarmencryptedFragmenthash",
				  WrapperEncryptedFragmentList.class);			
	        
        System.out.println(response.toString());
	}
	
	
	@Test
	public void swarm_collect_data_ok() {
		
		RestTemplate restTemplate = new RestTemplate();
		
		EncryptedFragment response = restTemplate.getForObject(
				  "http://localhost:8080/swarm/getFragment?key=" + "[B@33308786",
				  EncryptedFragment.class);			
	        
        System.out.println(response.toString());
	}
	
	@Test
	public void retrieve_transaction_ok() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		
		RestTemplate restTemplate = new RestTemplate();
		
		Transaction response = restTemplate.getForObject("http://localhost:8080/block/tx?txHash=MFwC92u1hdbdUJPPgG16qybI9V6lDnGO6EaLgWjOszc=",
				  Transaction.class);			
		
		DecryptedVO decryptedVO = Cryptography.decryptData(response.getEncryptedDocument().getSealedContent(), Base64Utils.convertByteToString(response.getEncryptedDocument().getKey()), "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIhzGExrF+oE/JKCz6UsN0Q3PEVoK7cx+H6tJWnwkF2hcXNf/vAaUNO34lm2/CWGNOelFLcW1jeS2++iiBIzL/i5+HCMwvvj03eXt87BHUMPwhl7tnjCvM/cQ6z+lKDjXI020jrt2RlhxMwgH6xt3ZSoX3Jm5fNMLIvovzkhvoOXAgMBAAECgYAP3fp0YHu+begFDYsh9PtURmwO0idLPVddO7s/D4dqK+zqcr5No3HEvPxoXFwD7RBX451+EPzGJ6DFxAu4uhUY1ejqTKrVAqqOQalJY4dKvGsAzz1vP3IoQMZOzIpd6w2DH63LzHzMg5hhQazQfDK1b5HzlqBHK3cJE2JPb6LrKQJBAOdlZ/CaDGSt0fxtWrs5OwFDYesGs/0L8olamxbkEurX3aYf/2qyE4AMXScGRZYNyB7nY2FhWQgk9twEepCzRVUCQQCW9T9x9vZdvPqNTG70+K5pM1SQLK27iF0KZr/p3hbO2bu4CNCmCKgDniLrl9WX6hRpx387ImNi03a8d3eFx2U7AkApfI7kHlp2eouZfGm2A47GrTQTFUKT7d5wLeR8eCvEGyvMKxR6Jq9EV/MjmgZD3viwQcAZp4B061uvGsZ2xw15AkAzK4jJEcSrO67WjK0j3tQIFv04LdzqL6wSJYgyNzUWAtpN5LcNQOvq5gTCRWKCrzciMByhp+N2kWWzNu/N6iwXAkBSCkh4YA/7YeWH2p4uMooqjJ2lTXlx2k10LjGGT8Ab4DPOV5CkQqgx+DpDMFp1W0JlTsULGzNh2bjkX2+dAowT");
	        
		System.out.println(decryptedVO.getDecryptedContentData());
		
	}
	
	
	
	@Test
	public void check_node_health_ok() throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();			
		Node nodeToInspect = new Node(new URL("http://localhost:8080"));
      
		/**
		 * call node heartbeat url
		 */
        Node nodeTarget = restTemplate.getForObject("http://localhost:8080/node/heartbeat",Node.class , nodeToInspect);
        
        System.out.println("Status of pinged node:" + nodeTarget.getNodeUptime());
	}

}
