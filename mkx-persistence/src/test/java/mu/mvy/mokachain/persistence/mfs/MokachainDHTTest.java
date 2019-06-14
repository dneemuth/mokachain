package mu.mvy.mokachain.persistence.mfs;

import org.junit.Before;
import org.junit.Test;

import mu.mvy.mokachain.persistence.configs.DefaultConfiguration;
import mu.mvy.mokachain.persistence.mfs.dht.MokachainStorageEntry;
import mu.mvy.mokachain.persistence.mfs.dht.MfsStorageEntry;
import mu.mvy.mokachain.persistence.mfs.dht.MokachainDHT;

public class MokachainDHTTest {
	
	@Before
	public void setUp() throws Exception {
	}
	
	
	
	@Test
	public void initialize_mokadht_ok() throws Exception {
		DefaultConfiguration config = new DefaultConfiguration();
		
		MokachainDHT mokachainDHT = new MokachainDHT("javed", config);		
		DHTContentImpl DHTContentImpl = new DHTContentImpl("key12345","JAVED");
		
		MokachainStorageEntry content  = new MokachainStorageEntry(DHTContentImpl);		
		mokachainDHT.store(content);
	}


	@Test
	public void retrieve_mokadht_ok() throws Exception {
		DefaultConfiguration config = new DefaultConfiguration();
		
		MokachainDHT mokachainDHT = new MokachainDHT("javed", config);		
		MfsStorageEntry mfsStorageEntry = mokachainDHT.retrieve("key12345", -2072976188);
		//-2072976188
	}
}
