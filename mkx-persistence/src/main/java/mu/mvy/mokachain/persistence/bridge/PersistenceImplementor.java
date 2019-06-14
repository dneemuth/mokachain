package mu.mvy.mokachain.persistence.bridge;

import java.util.List;

import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;

public interface PersistenceImplementor {
	
	public Object saveGenericList(String key, List<EncryptedFragment> fragments);
	
	public Object getGenericList(String key);
	
	public Object saveObject(String key, Object object);
	
	public Object getObject(String objectId);
}
