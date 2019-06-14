package mu.mvy.mokachain.node.service;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.common.domain.sharding.Fragment;
import mu.mvy.mokachain.common.statistics.LogMetrics;
import mu.mvy.mokachain.persistence.bridge.Persistence;
import mu.mvy.mokachain.persistence.bridge.PersistenceImplementor;
import mu.mvy.mokachain.persistence.bridge.impl.PersistenceImpl;
import mu.mvy.mokachain.persistence.configs.HibernateConfiguration;
import mu.mvy.mokachain.persistence.impl.MariaDBPersistenceImplementor;
import mu.mvy.mokachain.persistence.model.Transaction;


@Configuration
@Import(HibernateConfiguration.class) // <-- AppConfig imports DataSourceConfig
@Service
public class PersistenceService {
	 //private static final Logger LOG = LogManager.getLogger(PersistenceService.class);	
	 
	 PersistenceImplementor implementor  = null;
	 
	 
	 /**
	  * Service method to persist list of fragments.
	  * 
	  * @param encryptedFragments
	  */
	 @LogMetrics
	 public Object saveListFragments(String keyhash, List<EncryptedFragment> encryptedFragments) {

		    implementor  = new MariaDBPersistenceImplementor();		
			Persistence persistenceAPI = new PersistenceImpl(implementor);
			
			return persistenceAPI.persistGenericList(keyhash, encryptedFragments);
	}
	 @LogMetrics
	 public Object getListFragments(String keyhash) {
		 
		  implementor  = new MariaDBPersistenceImplementor();		
		  Persistence persistenceAPI = new PersistenceImpl(implementor);
			
		  List<Transaction> transactions = persistenceAPI.findListById(keyhash);
		   
		  Gson gson = new GsonBuilder().disableHtmlEscaping().create();			
		  Type listType = new TypeToken<List<EncryptedFragment>>() {}.getType();
		  List<Fragment> fragmentList = gson.fromJson(transactions.get(0).getTransactionContent(), listType);		  
		  
		  return fragmentList;
	 }

}

