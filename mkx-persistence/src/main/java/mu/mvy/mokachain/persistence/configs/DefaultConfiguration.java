package mu.mvy.mokachain.persistence.configs;

import java.io.File;

/**
 * A set of Kademlia configuration parameters. Default values are
 * supplied and can be changed by the application as necessary.
 *
 */
public class DefaultConfiguration implements MfsConfiguration {
	
   private final static String LOCAL_FOLDER = "kademlia";

	@Override
	public String getNodeDataFolder(String ownerId) {
		 /* Setup the main storage folder if it doesn't exist */
        String path = System.getProperty("user.home") + File.separator + DefaultConfiguration.LOCAL_FOLDER;
        File folder = new File(path);
        if (!folder.isDirectory())
        {
            folder.mkdir();
        }

        /* Setup subfolder for this owner if it doesn't exist */
        File ownerFolder = new File(folder + File.separator + ownerId);
        if (!ownerFolder.isDirectory())
        {
            ownerFolder.mkdir();
        }

        /* Return the path */
        return ownerFolder.toString();
	}

}
