package mu.mvy.mokachain.persistence.mfs.dht;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import mu.mvy.mokachain.persistence.configs.MfsConfiguration;
import mu.mvy.mokachain.persistence.exceptions.ContentNotFoundException;
import mu.mvy.mokachain.persistence.mfs.utils.KadSerializer;

/**
 * The main Distributed Hash Table interface that manages the entire DHT
 *
 * @author Joshua Kissoon
 * @since 20140523
 */
public interface DHT {
	
	  /**
     * Initialize this DHT to it's default state
     */
    public void initialize();

    /**
     * Set a new configuration. Mainly used when we restore the DHT state from a file
     *
     * @param con The new configuration file
     */
     public void setConfiguration(MfsConfiguration con);

    /**
     * Creates a new Serializer or returns an existing serializer
     *
     * @return The new ContentSerializer
     */
    public KadSerializer<MfsStorageEntry> getSerializer();

    /**
     * Handle storing content locally
     *
     * @param content The DHT content to store
     *
     * @return boolean true if we stored the content, false if the content already exists and is up to date
     *
     * @throws java.io.IOException
     */
    public boolean store(MfsStorageEntry content) throws IOException;

    public boolean store(MfsContent content) throws IOException;

    /**
     * Retrieves a Content from local storage
     *
     * @param key      The Key of the content to retrieve
     * @param hashCode The hash code of the content to retrieve
     *
     * @return A KadContent object
     *
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     */
    public MfsStorageEntry retrieve(String key, int hashCode) throws FileNotFoundException, IOException, ClassNotFoundException;

    /**
     * Check if any content for the given criteria exists in this DHT
     *
     * @param param The content search criteria
     *
     * @return boolean Whether any content exist that satisfy the criteria
     */
    public boolean contains(GetParameter param);

    /**
     * Retrieve and create a KadContent object given the StorageEntry object
     *
     * @param entry The StorageEntry used to retrieve this content
     *
     * @return KadContent The content object
     *
     * @throws java.io.IOException
     */
    public MfsStorageEntry get(MfsStorageEntryMetadata entry) throws IOException, NoSuchElementException;

    /**
     * Get the StorageEntry for the content if any exist.
     *
     * @param param The parameters used to filter the content needed
     *
     * @return KadContent A KadContent found on the DHT satisfying the given criteria
     *
     * @throws java.io.IOException
     */
    public MfsStorageEntry get(GetParameter param) throws NoSuchElementException, IOException;

    /**
     * Delete a content from local storage
     *
     * @param content The Content to Remove
     *
     *
     * @throws kademlia.exceptions.ContentNotFoundException
     */
    public void remove(MfsContent content) throws ContentNotFoundException;

    public void remove(MfsStorageEntryMetadata entry) throws ContentNotFoundException;

    /**
     * @return A List of all StorageEntries for this node
     */
    public List<MfsStorageEntryMetadata> getStorageEntries();

    /**
     * Used to add a list of storage entries for existing content to the DHT.
     * Mainly used when retrieving StorageEntries from a saved state file.
     *
     * @param ientries The entries to add
     */
    public void putStorageEntries(List<MfsStorageEntryMetadata> ientries);

}
