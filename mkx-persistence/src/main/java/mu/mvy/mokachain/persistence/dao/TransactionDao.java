package mu.mvy.mokachain.persistence.dao;

import java.util.List;

import mu.mvy.mokachain.persistence.model.Transaction;

public interface TransactionDao {
	
	Transaction findById(int id);
	 
	void saveTransaction(mu.mvy.mokachain.persistence.model.Transaction transaction);
     
    void deleteTransactionByHash(String hash);
     
    List<mu.mvy.mokachain.persistence.model.Transaction> findAllTransactions();
 
    List<mu.mvy.mokachain.persistence.model.Transaction> findTransactionByHash(String hash);

}
