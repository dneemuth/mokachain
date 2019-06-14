package mu.mvy.mokachain.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;


public class Base64Utils {
	
	
	/**
	 * This utility method will take as parameter, string and convert into equivalent bytes.
	 * 
	 * @param textToConvert
	 * @return string in bytes
	 */
	 public static byte[] convertStringToByte(String textToConvert) {
		 
		 if (SanityCheck.isValid(textToConvert)) {
			 return Base64.getDecoder().decode(textToConvert);
		 }
		 return null;
	 }
	 
	/**
	 * This utility method will take as parameter, bytes and convert into equivalent string.
	 * 
	 * @param textInBytes
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	 public static String convertByteToString(byte[] textInBytes)  {
		 if (SanityCheck.isValid(textInBytes)) {			 
			 return Base64.getEncoder().encodeToString(textInBytes);
		 }
		 return null;
	 }
	

}
