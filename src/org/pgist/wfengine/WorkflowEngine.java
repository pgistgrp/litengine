package org.pgist.wfengine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The Workflow Engine.
 * 
 * Workflow Engine is the helper class for clients to operate workflow.
 * 
 * @author kenny
 *
 */
public class WorkflowEngine {
    
    
    private static Map workflowCache = new HashMap();
    
    private static WorkflowDAO workflowDAO;
    
    
    /**
     * Client program use this method to get the workflow instance.
     * 
     * @param id
     * @return
     */
    public static Workflow getWorkflow(Long id) {
        
        //Check if the requested flow is already in the cache
        Workflow workflow = (Workflow) workflowCache.get(id);
        
        if (workflow==null) {//not in the chache
            workflow = loadWorkflowById(id);
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
        return workflowDAO.getWorkflow(id);
    }//loadWorkflowById()
    
    
}//class WorkflowEngine
