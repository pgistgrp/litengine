package org.pgist.wfengine.web;

import java.util.HashMap;
import java.util.Map;

import org.pgist.wfengine.WorkflowEngine;


/**
 * DWR Agent class for Workflow Management.
 * 
 * @author kenny
 */
public class WorkflowAgent {
    
    
    private WorkflowEngine engine;
    
    
    public void setEngine(WorkflowEngine engine) {
        this.engine = engine;
    }
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    /**
     * Execute the workflow to go over one specific activity.
     * 
     * @param params A map contains:
     *     <ul>
     *         <li>workflowId - int, id of a Workflow object</li>
     *         <li>contextId - int, id of a RunningContext object</li>
     *         <li>activityId - int, id of an Activity object</li>
     *         <li>nextId - int, id of an Activity object. (Required for some activities.)</li>
     *     </ul>
     * 
     * @return A map contains:
     *     <ul>
     *         <li>successful - boolean, whether the operation succeed</li>
     *         <li>contextId - int, id of a RunningContext object</li>
     *         <li>activityId - int, id of a Activity object</li>
     *     </ul>
     */
    public Map nextStep(Map params) {
        Map results = new HashMap();
        results.put("successful", false);
        
        try {
            Long workflowId = new Long((String) results.get("workflowId"));
            Long contextId = new Long((String) results.get("contextId"));
            Long activityId = new Long((String) results.get("activityId"));
            
            String nextIdStr = (String) results.get("nextId");
            if (nextIdStr==null || nextIdStr.length()==0) {
                engine.executeWorkflow(workflowId, contextId, activityId);
            } else {
                Long nextId = new Long((String) results.get("nextIdStr"));
                //engine.executeWorkflow(workflowId, contextId, activityId, nextId);
            }
        } catch (Exception e) {
            results.put("reason", e.getMessage());
        }
        
        return results;
    }//nextStep()
    
    
}//class WorkflowAgent
