package mu.mvy.mokachain.persistence.configs;

public interface MfsConfiguration {
	
	/**
     * Creates the folder in which this node data is to be stored.
     *
     * @param ownerId
     *
     * @return The folder path
     */
    public String getNodeDataFolder(String ownerId);

}
