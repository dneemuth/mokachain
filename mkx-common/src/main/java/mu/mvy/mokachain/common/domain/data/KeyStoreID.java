package mu.mvy.mokachain.common.domain.data;

import mu.mvy.mokachain.common.domain.Node;

public class KeyStoreID {
	
	private Node node;
	private String hashedContent;
	private String fragmentOrder;
	
	private String hashedStoreKey;
	
	
	public KeyStoreID(String hashedStoreKey, Node node, String hashedContent, String fragmentOrder) {
		this.hashedStoreKey = hashedStoreKey;
		
		this.node = node;
		this.hashedContent = hashedContent;
		this.fragmentOrder = fragmentOrder;
	}
	
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public String getHashedContent() {
		return hashedContent;
	}
	public void setHashedContent(String hashedContent) {
		this.hashedContent = hashedContent;
	}
	public String getFragmentOrder() {
		return fragmentOrder;
	}
	public void setFragmentOrder(String fragmentOrder) {
		this.fragmentOrder = fragmentOrder;
	}

	public String getHashedStoreKey() {
		return hashedStoreKey;
	}

	public void setHashedStoreKey(String hashedStoreKey) {
		this.hashedStoreKey = hashedStoreKey;
	}
	
	


}
