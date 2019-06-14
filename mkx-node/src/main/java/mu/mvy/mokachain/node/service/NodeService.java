package mu.mvy.mokachain.node.service;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import mu.mvy.mokachain.common.domain.Node;
import mu.mvy.mokachain.node.Config;


@Service
public class NodeService implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {
    private static final Logger LOG = LogManager.getLogger(NodeService.class);

    private final BlockService blockService;
    private final TransactionService transactionService;
    private final AddressService addressService;

    private Node self;
    private Set<Node> knownNodes = new HashSet<>();
    private RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Timer to check up-time for node
     */
    private StopWatch uptimeWatch = new StopWatch();
    
    @Autowired
    public NodeService(BlockService blockService, TransactionService transactionService, AddressService addressService) {
        this.blockService = blockService;
        this.transactionService = transactionService;
        this.addressService = addressService;
        
        uptimeWatch.start();
    }

    /**
     * Initial setup, query master Node for
     *  - Other Nodes
     *  - All Addresses
     *  - Current Blockchain
     *  - Transactions in pool
     *  and publish self on all other Nodes
     *  
     * @param embeddedServletContainerInitializedEvent serverletContainer for port retrieval
     */    
    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent embeddedServletContainerInitializedEvent) {
        Node masterNode = getMasterNode();

        // construct self node
        String host = retrieveSelfExternalHost(masterNode, restTemplate);
        int port = embeddedServletContainerInitializedEvent.getEmbeddedServletContainer().getPort();

        self = getSelfNode(host, port);
        LOG.info("Self address: " + self.getAddress());

        // download data if necessary
        if (self.equals(masterNode)) {
            LOG.info("Running as master node, nothing to init");
        } else {
            knownNodes.add(masterNode);

            // retrieve data
            retrieveKnownNodes(masterNode, restTemplate);
            addressService.retrieveAddresses(masterNode, restTemplate);
            blockService.retrieveBlockchain(masterNode, restTemplate);
            transactionService.retrieveTransactions(masterNode, restTemplate);
            
            // publish self
            broadcastPut("node", self);
        }
    }

    /**
     * Logout from every other Node before shutdown
     */
    @PreDestroy
    public void shutdown() {
        LOG.info("Shutting down");
        broadcastPost("node/remove", self);
        LOG.info(knownNodes.size() + " informed");
    }


    public Set<Node> getKnownNodes() {
        return knownNodes;
    }

    public synchronized void add(Node node) {
        knownNodes.add(node);
    }

    public synchronized void remove(Node node) {
        knownNodes.remove(node);
    }

    /**
     * Invoke a PUT request on all other Nodes
     * @param endpoint the endpoint for this request
     * @param data the data to send
     */
    public void broadcastPut(String endpoint, Object data) {
        knownNodes.parallelStream().forEach(node -> restTemplate.put(node.getAddress() + "/" + endpoint, data));
    }
    
    /**
     * Invoke a PUT request on all other Nodes
     * @param endpoint the endpoint for this request
     * @param data the data to send
     */
    public void broadSpecifiedCastPut(Node node, String endpoint, Object data) {
       restTemplate.put(node.getAddress() + "/" + endpoint, data);
    }

    /**
     * Invoke a POST request on all other Nodes
     * @param endpoint the endpoint for this request
     * @param data the data to send
     */
    public void broadcastPost(String endpoint, Object data) {
        knownNodes.parallelStream().forEach(node -> restTemplate.postForLocation(node.getAddress() + "/" + endpoint, data));
    }
    
    
    /**
     * Invoke a POST request on specific Node
     * @param endpoint the endpoint for this request
     * @param data the data to send
     */
    public void broadcastSpecificPost(Node node, String endpoint, Object data) {
        restTemplate.postForLocation(node.getAddress() + "/" + endpoint, data);
    }

    /**
     * Download Nodes from other Node and them to known Nodes
     * @param node Node to query
     * @param restTemplate RestTemplate to use
     */
    public void retrieveKnownNodes(Node node, RestTemplate restTemplate) {
        Node[] nodes = restTemplate.getForObject(node.getAddress() + "/node", Node[].class);
        Collections.addAll(knownNodes, nodes);
        LOG.info("Retrieved " + nodes.length + " nodes from node " + node.getAddress());
    }
    
    
    /**
     * A scheduled health service to check other nodes availability at a scheduled interval time.
     */
    @Scheduled(fixedDelay = 5000)
    public void triggerNodesHealthCheckup() {
    	
    	 LOG.info("NodeService :: triggerNodesHealthCheckup() -  started ...");
    	 
    	 LOG.info("Found " + knownNodes.size() +" nodes on the network.");
    	
    	/**
    	 * Collect all known nodes on network.
    	 */
	     for (Node node : knownNodes) {
	    	 
	    	 /**
	    	  * Check node health status
	    	  */
	    	 Node nodeTarget = restTemplate.getForObject("http://localhost:8080/node/heartbeat", Node.class , node);
	
	    	 /**
	    	  * Remove node which are not reachable.
	    	  */
	    	 if (nodeTarget == null) {
	    		 knownNodes.remove(node);
	    	 }
	     } 
	     
	     LOG.info("NodeService :: triggerNodesHealthCheckup() - Found " + knownNodes.size() +" nodes still active...");	    
    }

    
    /**
     * 
     * @return
     */
    public Long calculateNodeUptime() {
    	/**
    	 * calculate time elapsed
    	 */
    	long timeElapsed = uptimeWatch.getTime();
    	
    	System.out.println("Uptime interval in milliseconds:" + timeElapsed);
    	
    	return timeElapsed;
    }
    
    
    private String retrieveSelfExternalHost(Node node, RestTemplate restTemplate) {
        return restTemplate.getForObject(node.getAddress() + "/node/ip", String.class);
    }

    private Node getSelfNode(String host, int port) {
        try {
            return new Node(new URL("http", host, port, ""));
        } catch (MalformedURLException e) {
            LOG.error("Invalid self URL", e);
            return new Node();
        }
    }

    public Node getMasterNode() {
        try {
            return new Node(new URL(Config.MASTER_NODE_ADDRESS));
        } catch (MalformedURLException e) {
            LOG.error("Invalid master node URL", e);
            return new Node();
        }
    }

}
