package mu.mvy.mokachain.node.rest;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import mu.mvy.mokachain.common.domain.sharding.EncryptedFragment;
import mu.mvy.mokachain.common.domain.sharding.WrapperEncryptedFragmentList;
import mu.mvy.mokachain.common.utils.SanityCheck;
import mu.mvy.mokachain.node.service.PersistenceService;
import mu.mvy.mokachain.persistence.bridge.Persistence;
import mu.mvy.mokachain.persistence.bridge.PersistenceImplementor;
import mu.mvy.mokachain.persistence.bridge.impl.PersistenceImpl;
import mu.mvy.mokachain.persistence.impl.HashMapPersistenceImplementor;

@RestController
@RequestMapping("swarm")
public class SwarmController {
	 private static final Logger LOG = LogManager.getLogger(SwarmController.class);
	 
	 @Autowired
	 private PersistenceService persistenceService;
	 
	 private PersistenceImplementor implementor  = null; 
	 
	 /**
     * Remove a Node
     * @param node the Node to remove
     */
    @RequestMapping(path = "addFragmentList", method = RequestMethod.POST)
    void addFragmentList(@RequestBody WrapperEncryptedFragmentList wrapperEncryptedFragmentList) {
       
    	LOG.info("Adding list of fragments " + wrapperEncryptedFragmentList.getFragments().toString());
        
        String keyhash = wrapperEncryptedFragmentList.getHashedKeyStore();

		/**
		 * save object by reference
		 */	
        persistenceService.saveListFragments(keyhash, wrapperEncryptedFragmentList.getFragments());
    }
	
	
	 @RequestMapping(method = RequestMethod.PUT)
	 void addFragment(@RequestBody EncryptedFragment encryptedFragment, HttpServletResponse response) {
	        
		 	LOG.info("Add fragment " + encryptedFragment.getPartialDataHash());	  
	        
	        implementor  = new HashMapPersistenceImplementor();		       
    		Persistence persistenceAPI = new PersistenceImpl(implementor);
    		
    		/**
    		 * save object by reference
    		 */		    
    	   	String keyStore = encryptedFragment.getHashedKeyStore();
    		Object savedData  = persistenceAPI.persist(keyStore, encryptedFragment);
	        
	        if (SanityCheck.isValid(savedData)) {
	            response.setStatus(HttpServletResponse.SC_ACCEPTED);
	           
	        } else {
	            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
	        }	        
	    }
	
	 	/**
	     * Get specific fragment 
	     * @return encrypted fragment
	     */
		 @RequestMapping(value = "/getFragment", method = RequestMethod.GET)
		 @ResponseBody
	     EncryptedFragment getFragment(@RequestParam(value = "key") String hashedKey, HttpServletResponse response) {
			 implementor  = new HashMapPersistenceImplementor();		
			
			 Persistence persistenceAPI = new PersistenceImpl(implementor);	       
	         return (EncryptedFragment) persistenceAPI.findById(hashedKey);	     
	    }
		 
		 
		 /**
	     * Get specific fragment 
	     * @return encrypted fragment
	     */
		 @SuppressWarnings("unchecked")
		 @RequestMapping(value = "/getListFragment", method = RequestMethod.GET)
		 @ResponseBody
		 WrapperEncryptedFragmentList getListFragment(@RequestParam(value = "key") String hashedKey, HttpServletResponse response) {
			 
			 WrapperEncryptedFragmentList wrapperEncryptedFragmentList = new WrapperEncryptedFragmentList();
			 
			 List<EncryptedFragment>  encryptedFragments = (List<EncryptedFragment>)persistenceService.getListFragments(hashedKey);
			 wrapperEncryptedFragmentList.setFragments(encryptedFragments);	        
	        
	         return wrapperEncryptedFragmentList;	     
	    }
	 

}
