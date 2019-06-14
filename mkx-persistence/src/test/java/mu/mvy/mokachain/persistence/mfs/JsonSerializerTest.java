package mu.mvy.mokachain.persistence.mfs;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Test;

import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.common.domain.sharding.Fragment;
import mu.mvy.mokachain.persistence.configs.DefaultConfiguration;
import mu.mvy.mokachain.persistence.mfs.dht.MokachainStorageEntry;
import mu.mvy.mokachain.persistence.mfs.dht.MokachainDHT;
import mu.mvy.mokachain.persistence.mfs.utils.JsonSerializer;

public class JsonSerializerTest {
	
	DataOutputStream dout;

	

	@Test
	public void saveFile_ok() throws Exception {
		 /**
         * @section Store Basic Kad data
         */
		
		EncryptedFragment encryptedFragment = new EncryptedFragment();
		encryptedFragment.setOrder(new Integer(1));
		encryptedFragment.setPartialDataHash("refx10002020202020202020200202002002");
		encryptedFragment.setRootSwarmHash("rootswarmhash");
		encryptedFragment.setPartialDataContent(null);
		
		
        dout = new DataOutputStream(new FileOutputStream("C:\\Javed\\dev\\codelabs\\Kademlia\\store\\kad.kns"));
        new JsonSerializer<Fragment>().write(encryptedFragment, dout);
	}

}
