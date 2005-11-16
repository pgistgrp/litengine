package org.pgist.wfengine;

import java.util.HashMap;
import java.util.Map;


/**
 * The Workflow Engine.
 * 
 * @author kenny
 *
 */
public class WorkflowEngine {
    
    
    private static Map flowCache = new HashMap();
    
    
    public Workflow getWorkflow(Long id) {
        
        //Check if the requested flow is already in the cache
        Workflow workflow = (Workflow) flowCache.get(id);
        
        if (workflow==null) {//not in the chache
            workflow = loadWorkflowById(id);
        }
        
        return workflow;
    }//getWorkflow()
    
    
    private Workflow loadWorkflowById(Long id) {
        return null;
    }//loadWorkflowById()
    
    
}//class WorkflowEngine
