package mu.mvy.mokachain.node.service;


import java.security.KeyPair;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import mu.mvy.mokachain.common.domain.Address;
import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.crypto.utils.RSASignatureUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MiningServiceTests {

    @Autowired private MiningService miningService;
    @Autowired private TransactionService transactionService;
    @Autowired private AddressService addressService;

    private Address address;
    private KeyPair keyPair;

    @Before
    public void setUp() throws Exception {
    	
        keyPair = RSASignatureUtils.generateKeyPair();
        address = new Address("Max Mustermann", keyPair.getPublic().getEncoded());
        addressService.add(address);
    }

    @Test
    public void startStopMiner() throws Exception {
        final int initalTransactions = 100;
        addTransactions(initalTransactions);

        miningService.startMiner();

        while (transactionService.getTransactionPool().size() == initalTransactions) {
            Thread.sleep(1000);
        }

        miningService.stopMiner();
    }

    private void addTransactions(int count) throws Exception {
        for (int i = 0; i < count; i++) {
            String text = "Demo Transaction " + i;
            byte[] signature = RSASignatureUtils.sign(text.getBytes(), keyPair.getPrivate().getEncoded());
            Transaction transaction = new Transaction(text, address.getHash(), signature);

            transactionService.add(transaction);
        }
    }

}
