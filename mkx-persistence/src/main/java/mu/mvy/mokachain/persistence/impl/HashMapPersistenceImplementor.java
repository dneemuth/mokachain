package mu.mvy.mokachain.persistence.impl;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.NavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.common.domain.sharding.Fragment;
import mu.mvy.mokachain.persistence.bridge.PersistenceImplementor;

public class HashMapPersistenceImplementor  implements PersistenceImplementor {
	/**
	 * Internal Hashmap storage
	 */
	private DB	internalDatabase =	null; 
	
	
	/**
	 * Utility method to load internal memory disk.
	 */
	private void loadInternalDiskMemory() {
		
		 File database = new File("file.db");
		 internalDatabase = DBMaker
		            .fileDB(database)
		            .transactionEnable()
		            .fileLockDisable()
		            .make();
	}
	
	
	public Object saveGenericList(String key, List<EncryptedFragment> fragments) {

		loadInternalDiskMemory();
		
		Type listType = new TypeToken<List<EncryptedFragment>>() {}.getType();		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		
		String listToSave = gson.toJson(fragments,listType);
		
		/**
		 * Create hashmap disk database
		 */	
		
		NavigableMap<String, String> fragmentsDB = internalDatabase.<String, String>treeMap("listFragments", Serializer.STRING, Serializer.STRING).createOrOpen();
		
		/**
		 * Insert object in db
		 */		
		Object savedKey = fragmentsDB.put(key, listToSave); 
		
		internalDatabase.commit();
		
		/** close db to prevent data corruption */
		internalDatabase.close();

		return savedKey;
	
	}
	
	public Object getGenericList(String key) {
		
		loadInternalDiskMemory();
		
		/**
		 * Create hashmap disk database
		 */
		NavigableMap<String, String> fragmentsDB = internalDatabase.<String, String>treeMap("listFragments", Serializer.STRING, Serializer.STRING).createOrOpen();
		String fragmentListInString = fragmentsDB.get(key);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		
		Type listType = new TypeToken<List<EncryptedFragment>>() {}.getType();
		List<Fragment> objectList = gson.fromJson(fragmentListInString, listType);
		
				
		return objectList;
	}
	
	

	@Override
	public Object saveObject(String key, Object object) {
		
		loadInternalDiskMemory();
		
		/**
		 * Create hashmap disk database
		 */		
		NavigableMap<String, EncryptedFragment> fragmentsDB = internalDatabase.<String, EncryptedFragment>treeMap("fragments", Serializer.STRING, Serializer.JAVA).createOrOpen();
		
		/**
		 * Insert object in db
		 */		
		Object savedKey = fragmentsDB.put(key,  (EncryptedFragment)object); 
		
		internalDatabase.commit();
		
		/** close db to prevent data corruption */
		internalDatabase.close();

		return savedKey;
	}

	@Override
	public Object getObject(String objectId) {
		
		loadInternalDiskMemory();
		
		/**
		 * Create hashmap disk database
		 */
		NavigableMap<String, EncryptedFragment> fragmentsDB = internalDatabase.<String, EncryptedFragment>treeMap("fragments", Serializer.STRING, Serializer.JAVA).createOrOpen();
					
		return fragmentsDB.get(objectId);
	}

}
