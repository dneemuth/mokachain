package mu.mvy.mokachain.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.persistence.bridge.Persistence;
import mu.mvy.mokachain.persistence.bridge.PersistenceImplementor;
import mu.mvy.mokachain.persistence.bridge.impl.PersistenceImpl;

public class HashMapPersistenceImplementorTest {

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * This program needs a persistence framework at runtime  an implementor is chosen between file system implementation and
	 * database implementor , depending on existence of database drivers
	 */
	@Test
	public void persist_fragment_ok() {		
		
		PersistenceImplementor implementor  = new HashMapPersistenceImplementor();
			
		Persistence persistenceAPI = new PersistenceImpl(implementor);
		
		EncryptedFragment encryptedFragment = new EncryptedFragment();
		encryptedFragment.setOrder(new Integer(1));
		encryptedFragment.setPartialDataHash("refx10002020202020202020200202002002");
		encryptedFragment.setRootSwarmHash("rootswarmhash");
		encryptedFragment.setPartialDataContent(null);
		
		/**
		 * Retrieve object by reference
		 */		
		
		Object savedData  = persistenceAPI.persist("rootswarmhash", encryptedFragment);
		
		/**
		 * Asserts
		 */
		Assert.assertNotNull(savedData);
	}
	
	
	/**
	 * This program needs a persistence framework at runtime  an implementor is chosen between file system implementation and
	 * database implementor , depending on existence of database drivers
	 */
	@Test
	public void persist_list_fragment_ok() {		
		
		PersistenceImplementor implementor  = new HashMapPersistenceImplementor();			
		Persistence persistenceAPI = new PersistenceImpl(implementor);
		
		List<EncryptedFragment> fragments = new ArrayList<EncryptedFragment>();
		
		EncryptedFragment encryptedFragment = new EncryptedFragment();
		encryptedFragment.setOrder(new Integer(1));
		encryptedFragment.setPartialDataHash("refx10002020202020202020200202002002");
		encryptedFragment.setRootSwarmHash("rootswarmhash");
		encryptedFragment.setPartialDataContent(null);
		fragments.add(encryptedFragment);
		

		EncryptedFragment encryptedFragment2 = new EncryptedFragment();
		encryptedFragment2.setOrder(new Integer(2));
		encryptedFragment2.setPartialDataHash("refx10002020202020202020200202002002");
		encryptedFragment2.setRootSwarmHash("rootswarmhashList");
		encryptedFragment2.setPartialDataContent(null);
		fragments.add(encryptedFragment2);
		
		
		/**
		 * Retrieve object by reference
		 */		
		
		Object savedData  = persistenceAPI.persistGenericList("xxx", fragments);
		
		/**
		 * Asserts
		 */
		//Assert.assertNotNull(savedData);
	}
	
	
	
	@Test
	public void retrieve_fragment_ok() {		
		
		PersistenceImplementor implementor  = new HashMapPersistenceImplementor();
			
		Persistence persistenceAPI = new PersistenceImpl(implementor);
		
		/**
		 * Retrieve object by reference
		 */		
		
		Object savedData  = persistenceAPI.findById("rootswarmhashList");
		
		/**
		 * Asserts
		 */
		Assert.assertNotNull(savedData);
	}
	
	
	@Test
	public void retrieve_fragment_list_ok() {		
		
		PersistenceImplementor implementor  = new HashMapPersistenceImplementor();
			
		Persistence persistenceAPI = new PersistenceImpl(implementor);
		
		/**
		 * Retrieve object by reference
		 */		
		
		Object savedData  = persistenceAPI.findListById("[B@bcef303");
		
		/**
		 * Asserts
		 */
		Assert.assertNotNull(savedData);
	}

}
