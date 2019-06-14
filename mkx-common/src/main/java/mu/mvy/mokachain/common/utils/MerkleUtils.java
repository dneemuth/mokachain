package mu.mvy.mokachain.common.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;

public class MerkleUtils {
	
	
	/**
     * Calculates the Hash of all transactions as hash tree.
     * https://en.wikipedia.org/wiki/Merkle_tree
     * @return SHA256-hash as raw bytes
     */
    public static byte[] calculateMerkleRoot(List<EncryptedFragment> encryptedFragments) {
        Queue<byte[]> hashQueue = new LinkedList<>(encryptedFragments.stream().map(EncryptedFragment::calculateHash).collect(Collectors.toList()));
        while (hashQueue.size() > 1) {
            // take 2 hashes from queue
            byte[] hashableData = ArrayUtils.addAll(hashQueue.poll(), hashQueue.poll());
            // put new hash at end of queue
            hashQueue.add(DigestUtils.sha256(hashableData));
        }
        return hashQueue.poll();
    }

}
