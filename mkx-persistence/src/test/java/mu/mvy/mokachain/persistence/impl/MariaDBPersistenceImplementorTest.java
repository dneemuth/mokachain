package mu.mvy.mokachain.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.persistence.bridge.Persistence;
import mu.mvy.mokachain.persistence.bridge.PersistenceImplementor;
import mu.mvy.mokachain.persistence.bridge.impl.PersistenceImpl;
import mu.mvy.mokachain.persistence.configs.HibernateConfiguration;
import mu.mvy.mokachain.persistence.model.Transaction;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HibernateConfiguration.class})
//@ContextConfiguration(classes = HibernateConfiguration.class)
//@Transactional
public class MariaDBPersistenceImplementorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSavePersistGenericList_ok() {		
		
		PersistenceImplementor implementor  = new MariaDBPersistenceImplementor();		
		Persistence persistenceAPI = new PersistenceImpl(implementor);
		
		List<EncryptedFragment> fragments = new ArrayList<EncryptedFragment>();
		
		EncryptedFragment encryptedFragment = new EncryptedFragment();
		encryptedFragment.setOrder(new Integer(1));
		encryptedFragment.setPartialDataHash("refx10002020202020202020200202002002");
		encryptedFragment.setRootSwarmHash("rootswarmhash");
		encryptedFragment.setPartialDataContent(null);
		fragments.add(encryptedFragment);
		
		persistenceAPI.persistGenericList("key1", fragments);
		
	}
	
	
	@Test
	public void testRetrievePersistGenericList_ok() {		
		
		PersistenceImplementor implementor  = new MariaDBPersistenceImplementor();		
		Persistence persistenceAPI = new PersistenceImpl(implementor);
		
		@SuppressWarnings("unchecked")
		List<Transaction> transaction = (List<Transaction>) persistenceAPI.findListById("key1");
		
		Assert.assertTrue(transaction.size() > 0);		
	}

}
