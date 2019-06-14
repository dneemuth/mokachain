package mu.mvy.mokachain.common.domain.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class MetaInformation implements Serializable  {
	
	 /**
	 * default serial id
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * Specifies the encoding for the data.
	 */
	private String encodingFormat;
		
	/**
	 * Specifies timestamp at which artifact is deployed on blockchain.
	 */
	private Timestamp createdOn;
	
	
	/**
	 * Specifies the root swarm hash  to verify against collection of data fragments.
	 */
	private String merkleRootSwarmHash;
	
		
	/**
	 * Keeps track of all node where the sharded data has been sent to.
	 */
	private List<byte[]> swarmNodeLocations;
	

	public String getEncodingFormat() {
		return encodingFormat;
	}

	public void setEncodingFormat(String encodingFormat) {
		this.encodingFormat = encodingFormat;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getRootSwarmHash() {
		return merkleRootSwarmHash;
	}

	public void setRootSwarmHash(String rootSwarmHash) {
		this.merkleRootSwarmHash = rootSwarmHash;
	}

	public List<byte[]> getSwarmNodeLocations() {
		return swarmNodeLocations;
	}

	public void setSwarmNodeLocations(List<byte[]> swarmNodeLocations) {
		this.swarmNodeLocations = swarmNodeLocations;
	}

	
	
}
