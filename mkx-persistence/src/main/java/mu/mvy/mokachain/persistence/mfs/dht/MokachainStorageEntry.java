package mu.mvy.mokachain.persistence.mfs.dht;

public class MokachainStorageEntry  implements MfsStorageEntry {

	 private String content;
	    private final StorageEntryMetadata metadata;

	    public MokachainStorageEntry(final MfsContent content)
	    {
	        this(content, new StorageEntryMetadata(content));
	    }

	    public MokachainStorageEntry(final MfsContent content, final StorageEntryMetadata metadata)
	    {
	        this.setContent(content.toSerializedForm());
	        this.metadata = metadata;
	    }

	    @Override
	    public final void setContent(final byte[] data)
	    {
	        this.content = new String(data);
	    }

	    @Override
	    public final byte[] getContent()
	    {
	        return this.content.getBytes();
	    }

	    @Override
	    public final MfsStorageEntryMetadata getContentMetadata()
	    {
	        return this.metadata;
	    }

	    @Override
	    public String toString()
	    {
	        StringBuilder sb = new StringBuilder("[StorageEntry: ");

	        sb.append("[Content: ");
	        sb.append(this.getContent());
	        sb.append("]");

	        sb.append(this.getContentMetadata());

	        sb.append("]");

	        return sb.toString();
	    }
}
