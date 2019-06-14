package mu.mvy.mokachain.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name="transaction")
public class Transaction {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;
 
    @Size(min=0, max=100)
    @Column(name = "transactionHash", nullable = false)
    private String transactionHash;
    
    
    @Size(min=0, max=10000)
    @Column(name = "transactionContent", nullable = false)
    private String transactionContent;


	public int getTransactionId() {
		return transactionId;
	}


	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}


	public String getTransactionHash() {
		return transactionHash;
	}


	public void setTransactionHash(String transactionHash) {
		this.transactionHash = transactionHash;
	}


	public String getTransactionContent() {
		return transactionContent;
	}


	public void setTransactionContent(String transactionContent) {
		this.transactionContent = transactionContent;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transactionContent == null) ? 0 : transactionContent.hashCode());
		result = prime * result + ((transactionHash == null) ? 0 : transactionHash.hashCode());
		result = prime * result + transactionId;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		if (transactionContent == null) {
			if (other.transactionContent != null)
				return false;
		} else if (!transactionContent.equals(other.transactionContent))
			return false;
		if (transactionHash == null) {
			if (other.transactionHash != null)
				return false;
		} else if (!transactionHash.equals(other.transactionHash))
			return false;
		if (transactionId != other.transactionId)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", transactionHash=" + transactionHash
				+ ", transactionContent=" + transactionContent + "]";
	}
 

}
