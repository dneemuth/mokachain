package mu.mvy.mokachain.common.domain.sharding;

import java.io.Serializable;
import java.util.Arrays;


public abstract class Fragment implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Hashed key store
	 */
	private String hashedKeyStore;

	/**
	 * Keeps track of fragment orders
	 */
	private Integer order;
	
	/**
	 * Keeps SHA-256 hash of partial content
	 */
	private String partialDataHash;
	
	
	/**
	 * Stores the partial content of the fragment
	 */
	private byte[] partialDataContent;
	
	/**
	 * Specifies the root swarm hash  to verify against collection of data fragments.
	 */
	private String rootSwarmHash;
	
	
	public abstract byte[] calculateHash();
	
	
	

	public String getHashedKeyStore() {
		return hashedKeyStore;
	}




	public void setHashedKeyStore(String hashedKeyStore) {
		this.hashedKeyStore = hashedKeyStore;
	}




	public Integer getOrder() {
		return order;
	}


	public void setOrder(Integer order) {
		this.order = order;
	}

	


	public String getPartialDataHash() {
		return partialDataHash;
	}


	public void setPartialDataHash(String partialDataHash) {
		this.partialDataHash = partialDataHash;
	}


	public byte[] getPartialDataContent() {
		return partialDataContent;
	}


	public void setPartialDataContent(byte[] partialDataContent) {
		this.partialDataContent = partialDataContent;
	}


	public String getRootSwarmHash() {
		return rootSwarmHash;
	}


	public void setRootSwarmHash(String rootSwarmHash) {
		this.rootSwarmHash = rootSwarmHash;
	}


	@Override
	public String toString() {
		return "Fragment [order=" + order + ", partialDataHash=" + partialDataHash + ", partialDataContent="
				+ Arrays.toString(partialDataContent) + ", rootSwarmHash=" + rootSwarmHash + "]";
	}


	
	

}
