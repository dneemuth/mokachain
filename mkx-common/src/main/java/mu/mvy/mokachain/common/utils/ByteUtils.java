package mu.mvy.mokachain.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteUtils {

	
	 public static List<byte[]>  splitBytes(byte[] sealedContent,int blockSize) {
		 
		 List<byte[]> bytesInBlocks = new ArrayList<byte[]>();
		
		 int blockCount = (sealedContent.length + blockSize - 1) / blockSize;

		 byte[] range = null;

		 for (int i = 1; i < blockCount; i++) {
		 		int idx = (i - 1) * blockSize;
		 		range = Arrays.copyOfRange(sealedContent, idx, idx + blockSize);
		 		System.out.println("Chunk " + i + ": " + java.util.Arrays.toString(range));
		 		
		 		bytesInBlocks.add(range);
		 }

		 // Last chunk
		 int end = -1;
		 if (sealedContent.length % blockSize == 0) {
		 		end = sealedContent.length;
		 } else {
		 		end = sealedContent.length % blockSize + blockSize * (blockCount - 1);
		 }
		 		
		 range = Arrays.copyOfRange(sealedContent, (blockCount - 1) * blockSize, end);
		 bytesInBlocks.add(range);

		 System.out.println("Chunk " + blockCount + ": " + java.util.Arrays.toString(range));
		 
		 return bytesInBlocks;
	 }
}
