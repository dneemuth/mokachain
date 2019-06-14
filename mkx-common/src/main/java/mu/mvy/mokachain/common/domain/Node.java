package mu.mvy.mokachain.common.domain;


import java.net.URL;
import java.sql.Timestamp;

public class Node {

    /**
     * HTTP-Address including port on which the addressed node listens for incoming connections
     */
    private URL address;
    
    /**
     * Flag to indicate that this node is a supernode
     */
    private boolean superNode;
    
    /**
     * Time node has been activated in milliseconds
     */
    private long nodeUptime = -1;    
    
    /**
     * Last time node has signaled its online presence on network
     */
    private Timestamp lastTimeSeenOnNetwork;
    

    public Node() {
    }

    public Node(URL address) {
        this.address = address;
    }    
    
    public long getNodeUptime() {
		return nodeUptime;
	}

	public void setNodeUptime(long nodeUptime) {
		this.nodeUptime = nodeUptime;
	}

	public boolean isSuperNode() {
		return superNode;
	}

	public void setSuperNode(boolean superNode) {
		this.superNode = superNode;
	}
	

	public Timestamp getLastTimeSeenOnNetwork() {
		return lastTimeSeenOnNetwork;
	}

	public void setLastTimeSeenOnNetwork(Timestamp lastTimeSeenOnNetwork) {
		this.lastTimeSeenOnNetwork = lastTimeSeenOnNetwork;
	}

	public URL getAddress() {
        return address;
    }

    public void setAddress(URL address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return address != null ? address.equals(node.address) : node.address == null;
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }
}
