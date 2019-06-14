package mu.mvy.mokachain.persistence.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.persistence.bridge.PersistenceImplementor;
import mu.mvy.mokachain.persistence.configs.HibernateConfiguration;
import mu.mvy.mokachain.persistence.dao.TransactionDao;
import mu.mvy.mokachain.persistence.model.Transaction;

@Component
public class MariaDBPersistenceImplementor  implements PersistenceImplementor {	

	//@Override
	public Object saveGenericList(String key, List<EncryptedFragment> fragments) {
		
		AnnotationConfigApplicationContext context = 
	            new AnnotationConfigApplicationContext(HibernateConfiguration.class);
		
		TransactionDao transactionDao = context.getBean(TransactionDao.class);		
		Transaction tx = new Transaction();
		tx.setTransactionHash(key);
		
		Type listType = new TypeToken<List<EncryptedFragment>>() {}.getType();		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();		
		String listToSave = gson.toJson(fragments,listType);		
		tx.setTransactionContent(listToSave);
		
		transactionDao.saveTransaction(tx);		
		return null;
	}

	//@Override
	public List<Transaction> getGenericList(String key) {
		
		AnnotationConfigApplicationContext context = 
	            new AnnotationConfigApplicationContext(HibernateConfiguration.class);
		
		TransactionDao transactionDao = context.getBean(TransactionDao.class);		
		return transactionDao.findTransactionByHash(key);
		
	}

	//@Override
	public Object saveObject(String key, Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public Object getObject(String objectId) {
		// TODO Auto-generated method stub
		return null;
	}

}
