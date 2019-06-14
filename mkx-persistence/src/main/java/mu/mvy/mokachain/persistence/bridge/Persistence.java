package mu.mvy.mokachain.persistence.bridge;

import java.util.List;

import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.persistence.model.Transaction;

public interface Persistence {
	
	
	/**
	 * @param object
	 * @return returns objectID
	 */
	public Object persistGenericList(String key, List<EncryptedFragment> fragments);
	
	
	/**
	 * 
	 * @param objectId
	 * @return persisted Object
	 */
	public List<Transaction> findListById(String objectId);
	
	
	/**
	 * @param object
	 * @return returns objectID
	 */
	public Object persist(String key, Object object);
	
	/**
	 * 
	 * @param objectId
	 * @return persisted Object
	 */
	public Object findById(String objectId);

}
