package mu.mvy.mokachain.persistence.bridge.impl;

import java.util.List;

import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.persistence.bridge.Persistence;
import mu.mvy.mokachain.persistence.bridge.PersistenceImplementor;
import mu.mvy.mokachain.persistence.model.Transaction;

/**
 * Abstraction Imp
 */
public class PersistenceImpl implements Persistence {
	
    private PersistenceImplementor implementor = null;
	
	public PersistenceImpl(PersistenceImplementor imp) {		
		this.implementor = imp;		
	}
	
	

	@Override
	public Object persist(String key, Object object) {		
		return implementor.saveObject(key, object);
	}

	@Override
	public Object findById(String objectId) {
		return implementor.getObject(objectId);
	}



	public Object persistGenericList(String key, List<EncryptedFragment> fragments) {
		return implementor.saveGenericList(key, fragments);
	}



	public List<Transaction> findListById(String objectId) {
		return (List<Transaction>)implementor.getGenericList(objectId);
	}

}
