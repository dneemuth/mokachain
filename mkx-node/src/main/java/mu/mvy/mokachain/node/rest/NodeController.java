package mu.mvy.mokachain.node.rest;


import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mu.mvy.mokachain.common.domain.Node;
import mu.mvy.mokachain.node.service.NodeService;

@RestController
@RequestMapping("node")
public class NodeController {
	
	private static final Logger LOG = LogManager.getLogger(NodeController.class);

    private final NodeService nodeService;
  
	@Autowired
    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /**
     * Get all Nodes this node knows
     * @return JSON list of addresses
     */
    @RequestMapping()
    Set<Node> getNodes() {
        return nodeService.getKnownNodes();
    }

    /**
     * Add a new Node
     * @param node the Node to add
     */
    @RequestMapping(method = RequestMethod.PUT)
    void addNode(@RequestBody Node node) {
        LOG.info("Add node " + node.getAddress());
        nodeService.add(node);
    }

    /**
     * Remove a Node
     * @param node the Node to remove
     */
    @RequestMapping(path = "remove", method = RequestMethod.POST)
    void removeNode(@RequestBody Node node) {
        LOG.info("Remove node " + node.getAddress());
        nodeService.remove(node);
    }

    /**
     * Helper to determine the external address for new Nodes.
     * @param request HttpServletRequest
     * @return the remote address
     */
    @RequestMapping(path = "ip")
    String getIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
    
    
    /**
     * Helper to check health status for Nodes.
     * @param request HttpServletRequest
     * @return the remote address
     * @throws Exception 
     */
    @RequestMapping(path = "heartbeat")
    Node checkNodeHeartbeat(HttpServletRequest request, @RequestBody Node nodeToInspect) throws Exception {
    	
    	/**
    	 * calculate node uptime
    	 */
    	nodeToInspect.setNodeUptime(nodeService.calculateNodeUptime());
    	
    	/**
    	 * Last time seen
    	 */
    	Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
    	nodeToInspect.setLastTimeSeenOnNetwork(currentTimestamp);
    	
        return nodeToInspect;
    }

}
