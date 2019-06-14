package mu.mvy.mokachain.common.domain;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class Address {

    /**
     * Unique identifier which can be generated by hashing name and publicKey
     */
    private byte[] hash;

    /**
     * Self-given name for this Address
     */
    private String name;

    /**
     * The public key for this Address to ensure everybody is able to verify signed messages
     */
    private byte[] publicKey;

    public Address() {
    }

    public Address(String name, byte[] publicKey) {
        this.name = name;
        this.publicKey = publicKey;
        this.hash = calculateHash();
    }

    public byte[] getHash() {
        return hash;
    }

    public Address setHash(byte[] hash) {
        this.hash = hash;
        return this;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Calculates the hash using relevant fields of this type
     * @return SHA256-hash as raw bytes
     */
    private byte[] calculateHash() {
        byte[] hashableData = ArrayUtils.addAll(name.getBytes(), publicKey);
        return DigestUtils.sha256(hashableData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        return Arrays.equals(hash, address.hash);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }
}
