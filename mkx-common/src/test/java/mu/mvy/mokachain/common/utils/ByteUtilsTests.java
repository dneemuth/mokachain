package mu.mvy.mokachain.common.utils;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class ByteUtilsTests {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void splitBytes_ok() {
		
		 byte[] data = { 2, 3, 5, 7, 8, 9, 11, 12, 13 };
		
		 
		 /**
		  * actual method
		  */
		 List<byte[]> bytesCollected = ByteUtils.splitBytes(data, 3);
		 
		 
		 //assert
		 Assert.assertNotNull(bytesCollected);
	}

}
