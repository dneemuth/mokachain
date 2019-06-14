package mu.mvy.mokachain.persistence.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mu.mvy.mokachain.persistence.model.Transaction;

@Transactional
@Repository("transactionDao")
public class TransactionDaoImpl  implements TransactionDao {

	@Autowired
	private SessionFactory sessionFactory;

	public mu.mvy.mokachain.persistence.model.Transaction findById(int id) {		
		return null;
	}

	public void saveTransaction(mu.mvy.mokachain.persistence.model.Transaction transaction) {
		 sessionFactory.getCurrentSession().save(transaction);
	}

	public void deleteTransactionByHash(String hash) {
	    Query query =  sessionFactory.getCurrentSession().createSQLQuery("delete from Transaction where transactionHash = :transactionHash");
        query.setString("transactionHash", hash);
        query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<mu.mvy.mokachain.persistence.model.Transaction> findAllTransactions() {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(Transaction.class);		
		return  cr.list();
	}

	@SuppressWarnings("unchecked")
	public List<mu.mvy.mokachain.persistence.model.Transaction> findTransactionByHash(String hash) {			
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(Transaction.class);
		cr.add(Restrictions.eq("transactionHash", hash));
		return cr.list();

	}

}
