package org.pgist.wfengine;

import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;


/**
 * The Workflow Engine.
 * 
 * Workflow Engine is the helper class for clients to operate workflow.
 * 
 * @author kenny
 *
 */
public class WorkflowEngine {
    
    
    private static Cache workflowCache = null;
    
    private static WorkflowDAO workflowDAO;
    
    static {
        try {
            //Create a CacheManager using defaults
            CacheManager manager = CacheManager.create();
            //Create a Cache specifying its configuration.
            workflowCache = new Cache("workflow", 50, MemoryStoreEvictionPolicy.LFU, true, false, 2*3600, 20*60, false, 0, null);
            manager.addCache(workflowCache);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//static
    
    
    public static void setWorkflowDAO(WorkflowDAO aWorkflowDAO) {
        workflowDAO = aWorkflowDAO;
    }
    
    
    /**
     * Client program use this method to get the workflow instance.
     * 
     * @param id
     * @return
     */
    public static Workflow getWorkflow(Long id) throws Exception {
        Workflow workflow = null;
        
        //Check if the requested flow is already in the cache
        Element element = workflowCache.get(id);
        if (element!=null) {
            workflow = (Workflow) element.getValue();
        } else {
            workflow = loadWorkflowById(id);
            workflowCache.put(new Element(id, workflow));
        }
        
        return workflow;
    }//getWorkflow()
    
    
    /**
     * Client program use this method to get the running activities in specified workflow instance.
     * 
     * @param id
     * @return
     */
    public static List getRunningActivity(Long id) {
        return null;
    }//getRunningActivity()
    
    
    private static Workflow loadWorkflowById(Long id) {
        return workflowDAO.getWorkflow(id, false, false);
    }//loadWorkflowById()
    
    
}//class WorkflowEngine
