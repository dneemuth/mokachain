package mu.mvy.mokachain.node.service;


import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import mu.mvy.mokachain.common.crypto.UTXO;
import mu.mvy.mokachain.common.crypto.UTXOPool;
import mu.mvy.mokachain.common.domain.Address;
import mu.mvy.mokachain.common.domain.Node;
import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.statistics.LogMetrics;
import mu.mvy.mokachain.common.utils.Crypto;
import mu.mvy.mokachain.crypto.utils.RSASignatureUtils;


@Service
public class TransactionService {

	private static final Logger LOG = LogManager.getLogger(TransactionService.class);

    private final AddressService addressService;


    /**
     * Pool of Transactions which are not included in a Block yet.
     */
    private Set<Transaction> transactionPool = new HashSet<>();
    
    
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */

    // the unspent outputs
	private UTXOPool uPool = null;

    @Autowired
    public TransactionService(AddressService addressService) {
        this.addressService = addressService;
    }
    
    
    /**
     * Exposed utility method to allocate utxo pool to transaction service.
     * 
     * @param utxoPool
     */
    public void allocateUtxPool(UTXOPool utxoPool) {
        uPool = new UTXOPool(utxoPool);
    }



    public Set<Transaction> getTransactionPool() {
        return transactionPool;
    }
    
    
   
    /**
     * Add a new Transaction to the pool
     * @param transaction Transaction to add
     * @return true if verification succeeds and Transaction was added
     */
    @LogMetrics
    public synchronized boolean add(Transaction transaction) {
        if (verify(transaction)) {
            transactionPool.add(transaction);
            return true;
        }
        return false;
    }

    /**
     * Remove Transaction from pool
     * @param transaction Transaction to remove
     */
    public void remove(Transaction transaction) {
        transactionPool.remove(transaction);
    }

    /**
     * Does the pool contain all given Transactions?
     * @param transactions Collection of Transactions to check
     * @return true if all Transactions are member of the pool
     */
    public boolean containsAll(Collection<Transaction> transactions) {
        return transactionPool.containsAll(transactions);
    }

    
    
    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    private boolean verify(Transaction transaction) {
    	
    	 ArrayList<Transaction.Output> outputs = transaction.getOutputs();
         ArrayList<Transaction.Input> inputs = transaction.getInputs();
       
    	/**
    	 * correct signature
    	 */
        Address sender = addressService.getByHash(transaction.getSenderHash());
        if (sender == null) {
            LOG.warn("Unknown address " + Base64.encodeBase64String(transaction.getSenderHash()));
            return false;
        }

        try {
        	
        	
            if (!RSASignatureUtils.verify(transaction.getSignableData(), transaction.getSignature(), sender.getPublicKey())) {
                LOG.warn("Invalid signature");
                return false;
            }
            
            // avoid double spend
            UTXOPool inputPool = new UTXOPool();
            
            // if there is no inputs or outputs
            if (inputs == null || outputs == null) {
                return false;
            }
            
            
            for (int i = 0; i < inputs.size(); i++) {
                Transaction.Input in = inputs.get(i);
                UTXO ut = new UTXO(in.prevTxHash, in.outputIndex);

                /* all outputs claimed by {@code tx} are in the current UTXO pool */
                if (!uPool.contains(ut)) {
                    return false;
                }

                /* no UTXO is claimed multiple times by {@code tx} */
                if (inputPool.contains(ut)) {
                    return false;
                }

                /* the signatures on each input of {@code tx} are valid */
                // get the corresponding publicKey
                Transaction.Output ot = uPool.getTxOutput(ut);
                PublicKey publicKey = ot.address;

                // get signature
                byte[] signature = in.signature;

                if(!Crypto.verifySignature(publicKey, transaction.calculateHash(), signature)) {
                    return false;
                }

                inputPool.addUTXO(ut, ot);
            }
            
            
            /* all of {@code tx}s output values are non-negative */
            for (Transaction.Output ot : outputs) {
                if (ot.value < 0) {
                    return false;
                }
            }
            
            
            /* the sum of {@code tx}s input values is greater than or equal to the sum of its output values */
            Double inputSum = 0.0;
            Double outputSum = 0.0;

            for (Transaction.Input in : inputs) {
                UTXO ut = new UTXO(in.prevTxHash, in.outputIndex);
                Transaction.Output ot = uPool.getTxOutput(ut);
                inputSum = inputSum + ot.value;
            }

            for (Transaction.Output ot : outputs) {
                outputSum = outputSum + ot.value;
            }

            if (inputSum < outputSum) {
                return false;
            }
            
            
            
            
        } catch (Exception e) {
            LOG.error("Error while verification", e);
            return false;
        }

        // correct hash
        if (!Arrays.equals(transaction.getHash(), transaction.calculateHash())) {
             LOG.warn("Invalid hash");
            return false;
        }

        return true;
    }
    
    
    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        ArrayList<Transaction> validTx = new ArrayList<Transaction>();
        int prevTxLen;
        int newTxLen;

        do {
            prevTxLen = validTx.size();
            for (int i = 0; i < possibleTxs.length; i++) {
                Transaction tx = possibleTxs[i];
                if (validTx.contains(tx)) {
                    continue;
                }
                if (verify(tx)) {
                    validTx.add(tx);

                    // update the UTXOPool
                    ArrayList<Transaction.Input> inputs = tx.getInputs();
                    ArrayList<Transaction.Output> outputs = tx.getOutputs();

                    // remove all claimed outputs from UTXOPool
                    for (Transaction.Input in : inputs) {
                        UTXO ut = new UTXO(in.prevTxHash, in.outputIndex);
                        uPool.removeUTXO(ut);
                    }

                    // add all outputs into UTXOPool
                    for (int j = 0; j < outputs.size(); j++) {
                        UTXO ut = new UTXO(tx.getHash(), j);
                        uPool.addUTXO(ut, outputs.get(j));
                    }
                }
            }
            newTxLen = validTx.size();
        } while (prevTxLen != newTxLen);

        Transaction[] validTxOut = new Transaction[validTx.size()];
        int i = 0;
        for (Transaction tx : validTx) {
            validTxOut[i++] = tx;
        }

        return validTxOut;
    }

    /**
     * Download Transactions from other Node and add to the trasaction pool
     * @param node Node to query
     * @param restTemplate RestTemplate to use
     */
    @LogMetrics
    public void retrieveTransactions(Node node, RestTemplate restTemplate) {
        Transaction[] transactions = restTemplate.getForObject(node.getAddress() + "/transaction", Transaction[].class);
        Collections.addAll(transactionPool, transactions);
        LOG.info("Retrieved " + transactions.length + " transactions from node " + node.getAddress());
    }
}
