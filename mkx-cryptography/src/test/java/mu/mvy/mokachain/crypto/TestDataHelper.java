package mu.mvy.mokachain.crypto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import mu.mvy.mokachain.common.domain.data.EncryptedDocument;
import mu.mvy.mokachain.common.domain.data.MetaInformation;

public class TestDataHelper {
	
	public static String createLargeString() {		
		return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In viverra neque et odio vulputate ultricies in et enim." ;
	}

	
	public static EncryptedDocument createEncryptedDocument() {
		
		EncryptedDocument encryptedDocument = new EncryptedDocument();
			
		//metadata
		MetaInformation metaDataVO = new MetaInformation();
				
		Date date= new Date();
		long time = date. getTime();
		System. out. println("Time in Milliseconds: " + time);
		Timestamp ts = new Timestamp(time);
		metaDataVO.setCreatedOn(ts);
		
		metaDataVO.setEncodingFormat("RSA");	
		metaDataVO.setRootSwarmHash(null);
		metaDataVO.setSwarmNodeLocations(new ArrayList<byte[]>());
		
		encryptedDocument.setMetaDataVO(metaDataVO);
		
		
		//content
		byte[] sealedObject = {'a' ,'b' ,'c' ,'d'};
		encryptedDocument.setSealedContent(sealedObject);		
		
		return encryptedDocument;
	}
}
