package org.pgist.wfengine.web;

import java.util.Map;

import javax.servlet.ServletRequest;

import org.pgist.wfengine.WorkflowEngine;


/**
 * 
 * @author kenny
 *
 */
public class WorkflowUtils {
    
    
    private WorkflowEngine engine;
    
    
    public void setEngine(WorkflowEngine engine) {
        this.engine = engine;
    }
    
    
    /*
     * ------------------------------------------------------------------------
     */


    public Map processWorkflowInfo(ServletRequest request, Long workflowId, Long contextId, Long activityId) throws Exception {
        Map result = engine.getURL(workflowId, contextId, activityId);
        
        request.setAttribute("org.pgist.wfengine.ACTIVITY_RUNNING", result.get("status"));
        
        /*
         * Future and History
         */
        request.setAttribute("org.pgist.wfengine.CURRENT", result.get("activity"));
        request.setAttribute("org.pgist.wfengine.PARALLEL", result.get("parallel"));
        request.setAttribute("org.pgist.wfengine.SERIAL", result.get("serial"));
        request.setAttribute("org.pgist.wfengine.HISTORIES", result.get("histories"));
        request.setAttribute("org.pgist.wfengine.FUTURES", result.get("futures"));
        request.setAttribute("org.pgist.wfengine.CONTEXT", result.get("context"));
        request.setAttribute("org.pgist.wfengine.MEETING", result.get("meeting"));
        request.setAttribute("org.pgist.wfengine.NEXT", result.get("next"));
        
        /*
         * Inject workflow information to request
         */
        request.setAttribute("org.pgist.wfengine.WORKFLOW_ID", workflowId);
        request.setAttribute("org.pgist.wfengine.CONTEXT_ID", contextId);
        request.setAttribute("org.pgist.wfengine.ACTIVITY_ID", activityId);
        request.setAttribute("org.pgist.wfengine.INOUTS", result.get("inouts"));
        
        return result;
    }//processWorkflowInfo()
    
    
}//class WorkflowUtils
