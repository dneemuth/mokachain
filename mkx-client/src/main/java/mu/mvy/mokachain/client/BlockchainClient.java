package mu.mvy.mokachain.client;


import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.SerializationUtils;
import org.springframework.web.client.RestTemplate;

import mu.mvy.mokachain.common.domain.Address;
import mu.mvy.mokachain.common.domain.Transaction;
import mu.mvy.mokachain.common.utils.Base64Utils;
import mu.mvy.mokachain.crypto.model.EncryptedVO;
import mu.mvy.mokachain.crypto.utils.Cryptography;
import mu.mvy.mokachain.crypto.utils.RSASignatureUtils;


/**
 * Simple class to help building REST calls for jBlockchain.
 * Just run it in command line for instructions on how to use it.
 *
 * Functions include:
 * - Generate Private/Public-Key
 * - Publish a new Address
 * - Publish a new Transaction
 * - Publish a new transaction with encrypted loaded data
 */
public class BlockchainClient {

    public static void main(String args[]) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = getOptions();
        try {
            CommandLine line = parser.parse(options, args);
            executeCommand(line);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("BlockchainClient", options , true);
        }
    }

    private static void executeCommand(CommandLine line) throws Exception {
        if (line.hasOption("keypair")) {
            generateKeyPair();
        } 
        else if (line.hasOption("address")) {
            String node = line.getOptionValue("node");
            String name = line.getOptionValue("name");
            String publickey = line.getOptionValue("publickey");
            
            if (node == null || name == null || publickey == null) {
                throw new ParseException("node, name and publickey is required");
            }
            publishAddress(new URL(node), Paths.get(publickey), name);

        } 
      
        
        else if (line.hasOption("transaction")) {
        	
            String node = line.getOptionValue("node");
            String message = line.getOptionValue("message");
            String sender = line.getOptionValue("sender");
            String privatekey = line.getOptionValue("privatekey");
            
            if (node == null || message == null || sender == null || privatekey == null) {
                throw new ParseException("node, message, sender and privatekey is required");
            }
            
            //java -jar client/target/client-0.0.1-SNAPSHOT.jar --transaction --node "http://localhost:8080" --sender "Tdz0bKDfca3QjFAe5Ccuj9Noy6ah8n+R8DnZznvjic4=" --message "Hello World" --privatekey key.priv
            publishTransaction(new URL(node), Paths.get(privatekey), message, Base64.decodeBase64(sender));
            
        }  else if (line.hasOption("wiredtransaction")) {
        	
            String node = line.getOptionValue("node");
            
            String message = line.getOptionValue("message");
            String sender = line.getOptionValue("sender");
            
            String publicKey = line.getOptionValue("publicKey");
            String secretKey = line.getOptionValue("secretKey");
            String privatekey = line.getOptionValue("privatekey");
            
            if (node == null || secretKey == null || publicKey == null|| message == null || sender == null || privatekey == null) {
                throw new ParseException("node, message, sender, secret key , public key and private key is required");
            }
            
            /**
             * create data to encrypt
             */
           String textToEncrypt = "Block zero - inserted by DN on 09 Sep 2018";
            
                 
            /**
             * Send loaded transaction to blockchain
             */
            publishDataTransaction(new URL(node), Paths.get(publicKey) , Paths.get(privatekey), secretKey, textToEncrypt, Base64.decodeBase64(sender));
            
        }
    }

    private static Options getOptions() {
        OptionGroup actions = new OptionGroup();
        actions.addOption(new Option("k", "keypair", false, "generate private/public key pair"));    
        actions.addOption(new Option("a", "address", false, "publish new address"));
        actions.addOption(new Option("t", "transaction", false, "publish new transaction"));
        actions.addOption(new Option("tx", "wiredtransaction", false, "publish new transaction with loaded data"));
        actions.setRequired(true);

        Options options = new Options();
        options.addOptionGroup(actions);
        options.addOption(Option.builder("o")
                .longOpt("node")
                .hasArg()
                .argName("Node URL")
                .desc("needed for address and transaction publishing")
                .build());
        options.addOption(Option.builder("n")
                .longOpt("name")
                .hasArg()
                .argName("name for new address")
                .desc("needed for address publishing")
                .build());
        options.addOption(Option.builder("p")
                .longOpt("publickey")
                .hasArg()
                .argName("path to key file")
                .desc("needed for address publishing")
                .build());
        options.addOption(Option.builder("v")
                .longOpt("privatekey")
                .hasArg()
                .argName("path to key file")
                .desc("needed for transaction publishing")
                .build());
        options.addOption(Option.builder("m")
                .longOpt("message")
                .hasArg()
                .argName("message to post")
                .desc("needed for transaction publishing")
                .build());
        options.addOption(Option.builder("s")
                .longOpt("sender")
                .hasArg()
                .argName("address hash (Base64)")
                .desc("needed for transaction publishing")
                .build());

        return options;
    }

    private static void generateKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException, IOException {
    	
    	KeyPair keyPair = RSASignatureUtils.generateKeyPair();
        Files.write(Paths.get("key.priv"), keyPair.getPrivate().getEncoded());
        Files.write(Paths.get("key.pub"), keyPair.getPublic().getEncoded());
      
    }


    private static void publishAddress(URL node, Path publicKey, String name) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        Address address = new Address(name, Files.readAllBytes(publicKey));
        restTemplate.put(node.toString() + "/address?publish=true", address);
        System.out.println("Hash of new address: " + Base64.encodeBase64String(address.getHash()));
    }

    private static void publishTransaction(URL node, Path privateKey, String text, byte[] senderHash) throws Exception {
       
    	RestTemplate restTemplate = new RestTemplate();
        byte[] signature = RSASignatureUtils.sign(text.getBytes(), Files.readAllBytes(privateKey));
        Transaction transaction = new Transaction(text, senderHash, signature);
        restTemplate.put(node.toString() + "/transaction?publish=true", transaction);
        
        System.out.println("Hash of new transaction: " + Base64.encodeBase64String(transaction.getHash()));
    	
    }
    
    private static void publishDataTransaction(URL node, Path publicKey, Path privateKey,String secretKey, Object object, byte[] senderHash) throws Exception {
       
    	
    	RestTemplate restTemplate = new RestTemplate();
    	
    	//Encrypt data with private key
    	String publicKeyString = Base64Utils.convertByteToString(Files.readAllBytes(publicKey)) ;
    	EncryptedVO  encryptedVO = Cryptography.encryptData(object, publicKeyString, secretKey);
    	
    	byte[] encryptedSecretKey = Base64Utils.convertStringToByte(encryptedVO.getEncryptedSecretKey());
     
    	
    	//Generate signature
        byte[] serializedTransferObject = SerializationUtils.serialize(object);        
        byte[] signature = RSASignatureUtils.sign(serializedTransferObject, Files.readAllBytes(privateKey));
        Transaction transaction = new Transaction(encryptedSecretKey, encryptedVO.getEncryptedContentData(), senderHash, signature);
        restTemplate.put(node.toString() + "/transaction?publish=true", transaction);
        
        System.out.println("Hash of new transaction: " + Base64.encodeBase64String(transaction.getHash()));
   		
    }
}
