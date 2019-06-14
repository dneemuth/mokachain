package mu.mvy.mokachain.common.domain;


import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.google.common.primitives.Longs;

import mu.mvy.mokachain.common.crypto.UTXO;
import mu.mvy.mokachain.common.domain.data.EncryptedDocument;
import mu.mvy.mokachain.common.domain.interfaces.Hashable;
import mu.mvy.mokachain.common.utils.SanityCheck;

public class Transaction implements Hashable , Serializable {

    /**
	 * default serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Unique identifier which can be generated by hashing text, senderHash, signature and timestamp.
     */
    private byte[] hash;

    /**
     * simple description text for this transaction
     */
    private String text;
    
    /**
     * Contains both metadata and content
     */
    private EncryptedDocument encryptedDocument;
    
    /**
     * Tamper seal status - to verify against data integrity
     */
    private boolean tamperProofStatus;
    
  
    /**
     * The hash of the address which is responsible for this transaction
     */
    private byte[] senderHash;

    /**
     * Signature of text which can be verified with publicKey of sender address
     */
    private byte[] signature;

    /**
     * Creation time of this Transaction
     */
    private long timestamp;
        
    
    public class Input {
        /** hash of the Transaction whose output is being used */
        public byte[] prevTxHash;
        /** used output's index in the previous transaction */
        public int outputIndex;
        /** the signature produced to check validity */
        public byte[] signature;

        public Input(byte[] prevHash, int index) {
            if (prevHash == null)
                prevTxHash = null;
            else
                prevTxHash = Arrays.copyOf(prevHash, prevHash.length);
            outputIndex = index;
        }

        public void addSignature(byte[] sig) {
            if (sig == null)
                signature = null;
            else
                signature = Arrays.copyOf(sig, sig.length);
        }
   

        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (getClass() != other.getClass()) {
                return false;
            }

            Input in = (Input) other;

            if (prevTxHash.length != in.prevTxHash.length)
                return false;
            for (int i = 0; i < prevTxHash.length; i++) {
                if (prevTxHash[i] != in.prevTxHash[i])
                    return false;
            }
            if (outputIndex != in.outputIndex)
                return false;
            if (signature.length != in.signature.length)
                return false;
            for (int i = 0; i < signature.length; i++) {
                if (signature[i] != in.signature[i])
                    return false;
            }
            return true;
        }

        public int hashCode() {
            int hash = 1;
            hash = hash * 17 + Arrays.hashCode(prevTxHash);
            hash = hash * 31 + outputIndex;
            hash = hash * 31 + Arrays.hashCode(signature);
            return hash;
        }
    }
    
    
    public class Output {
        /** value in bitcoins of the output */
        public double value;
        /** the address or public key of the recipient */
        public PublicKey address;

        public Output(double v, PublicKey addr) {
            value = v;
            address = addr;
        }
        
        
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (getClass() != other.getClass()) {
                return false;
            }

            Output op = (Output) other;

            if (value != op.value)
                return false;
            if (!((RSAPublicKey) address).getPublicExponent().equals(
                    ((RSAPublicKey) op.address).getPublicExponent()))
                return false;
            if (!((RSAPublicKey) address).getModulus().equals(
                    ((RSAPublicKey) op.address).getModulus()))
                return false;
            return true;
	        }
	
	        public int hashCode() {
	            int hash = 1;
	            hash = hash * 17 + (int) value * 10000;
	            hash = hash * 31 + ((RSAPublicKey) address).getPublicExponent().hashCode();
	            hash = hash * 31 + ((RSAPublicKey) address).getModulus().hashCode();
	            return hash;
	        }
    }
    
    /** hash of the transaction, its unique id */
    private ArrayList<Input> inputs;
    private ArrayList<Output> outputs;
    private boolean coinbase;
    

    public Transaction() {    	
		  inputs = new ArrayList<Input>();
	      outputs = new ArrayList<Output>();
	      
	      coinbase = false;
    }
    
    
    public Transaction(Transaction tx) {    	
    	inputs = new ArrayList<Input>(tx.inputs);
        outputs = new ArrayList<Output>(tx.outputs);
        
        coinbase = false;
    }
    
    /** create a coinbase transaction of value {@code coin} and calls finalize on it */
    public Transaction(double coin, PublicKey address) {
        coinbase = true;
        inputs = new ArrayList<Input>();
        outputs = new ArrayList<Output>();
        addOutput(coin, address);       
    }

    public boolean isCoinbase() {
        return coinbase;
    }

    public Transaction(String text, byte[] senderHash, byte[] signature) {
        this.text = text;
        this.senderHash = senderHash;
        this.signature = signature;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }
    
    
    /**
     * overloaded transaction - creates tx with encrypted content.
     * 
     * @param text
     * @param senderHash
     * @param signature
     */
    public Transaction(byte[] secretKey, byte[] sealedContent, byte[] senderHash, byte[] signature) {
    	    	
    	/**
    	 * Insert logic to pre-populate Encrypted Document - 06.09.2018 ( depends on client processing capacity )
    	 */
    	
    	try {
    		
			this.encryptedDocument = new EncryptedDocument(sealedContent , secretKey);		
    	
	        this.senderHash = senderHash;
	        this.signature = signature;
	        this.timestamp = System.currentTimeMillis();
	        this.hash = calculateHash();
        
    	} catch (UnsupportedEncodingException e) {}
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getSenderHash() {
        return senderHash;
    }

    public void setSenderHash(byte[] senderHash) {
        this.senderHash = senderHash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    

    public boolean isTamperProofStatus() {
		return tamperProofStatus;
	}

	public void setTamperProofStatus(boolean tamperProofStatus) {
		this.tamperProofStatus = tamperProofStatus;
	}

	public byte[] getSignableData() {
    	if (this.getEncryptedDocument() == null) {
    		return this.getText().getBytes();
    	}
    	
        return this.getEncryptedDocument().getSealedContent();
    }
    
    
    public EncryptedDocument getEncryptedDocument() {
		return encryptedDocument;
	}

	public void setEncryptedDocument(EncryptedDocument encryptedDocument) {
		this.encryptedDocument = encryptedDocument;
	}

	/**
     * Utility method to evict content data from transaction before adding transaction to blockchain.
     * 
     */
    public void evictContentData() {
       	this.getEncryptedDocument().setSealedContent(null);    	
    }
	
	/**
     * Calculates the hash using relevant fields of this type
     * @return SHA256-hash as raw bytes
     */
    public byte[] calculateHash() {
    	
    	byte[] hashableData = null;
    	
    	if (SanityCheck.isValid(text)) {
    		hashableData = ArrayUtils.addAll(text.getBytes());
    	}
    	
    	if (SanityCheck.isValid(senderHash)) {    		
    		hashableData = ArrayUtils.addAll(senderHash);
    	}
    	
        hashableData = ArrayUtils.addAll(hashableData, signature);
        
        //06.09.2018 - added encrypted sealed content to hash table       
        if (SanityCheck.isValid(this.getEncryptedDocument()) && SanityCheck.isValid(this.getEncryptedDocument().getSealedContent())) {
        	hashableData = ArrayUtils.addAll(this.getEncryptedDocument().getSealedContent());
        }    
       
        //04.09.2018 - added timestamp to hash
        hashableData = ArrayUtils.addAll(hashableData, Longs.toByteArray(timestamp));
             
        return DigestUtils.sha256(hashableData);
    }
    
    public void addInput(byte[] prevTxHash, int outputIndex) {
        Input in = new Input(prevTxHash, outputIndex);
        inputs.add(in);
    }

    public void addOutput(double value, PublicKey address) {
        Output op = new Output(value, address);
        outputs.add(op);
    }

    public void removeInput(int index) {
        inputs.remove(index);
    }

    public void removeInput(UTXO ut) {
        for (int i = 0; i < inputs.size(); i++) {
            Input in = inputs.get(i);
            UTXO u = new UTXO(in.prevTxHash, in.outputIndex);
            if (u.equals(ut)) {
                inputs.remove(i);
                return;
            }
        }
    }
    
    
    public ArrayList<Input> getInputs() {
        return inputs;
    }

    public ArrayList<Output> getOutputs() {
        return outputs;
    }

    public Input getInput(int index) {
        if (index < inputs.size()) {
            return inputs.get(index);
        }
        return null;
    }

    public Output getOutput(int index) {
        if (index < outputs.size()) {
            return outputs.get(index);
        }
        return null;
    }

    public int numInputs() {
        return inputs.size();
    }

    public int numOutputs() {
        return outputs.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;
        
        // inputs and outputs should be same
        if (that.numInputs() != numInputs())
            return false;

        for (int i = 0; i < numInputs(); i++) {
            if (!getInput(i).equals(that.getInput(i)))
                return false;
        }

        if (that.numOutputs() != numOutputs())
            return false;

        for (int i = 0; i < numOutputs(); i++) {
            if (!getOutput(i).equals(that.getOutput(i)))
                return false;
        }
        
        
        if (Arrays.equals(hash, that.hash)) return true;
        
        
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }
}
