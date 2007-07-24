package org.pgist.wfengine.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.WorkflowEngine;


/**
 * Struts Action for Workflow Running. The url of this action is shown to users, when user clicks
 * the url, this action will forward to the real action. In this way it gives workflow engine
 * an oppotunity to control the access and inject some workflow information to the real actions.
 * 
 * Parameters:
 * <ul>
 *   <li>workflowId - workflow instance id</li>
 *   <li>contextId - workflow context id</li>
 *   <li>activityId - workflow activity id</li>
 *   <li>historyId - workflow history id</li>
 * </ul>
 * 
 * Parameters "workflowId" and "contextId" are required. Parameters "activityId" and "historyId"
 * are mutual exclusive.
 * 
 * Examples:
 * <ul>
 *   <li>
 *      Execute one current activity:
 *      workflow.do?workflowId=1234&contextId=4321&activityId=5678
 *   </li>
 *   <li>
 *      Execute one completed (history) activity:
 *      workflow.do?workflowId=1234&contextId=4321&historyId=5678
 *   </li>
 * </ul>
 * 
 * Before turning to the real action, it puts the following data as the request attributes:
 * <ul>
 *   <li>org.pgist.wfengine.WORKFLOW_ID - worfklowId</li>
 *   <li>org.pgist.wfengine.CONTEXT_ID - conextId</li>
 *   <li>org.pgist.wfengine.ACTIVITY_ID - activityId</li>
 *   <li>org.pgist.wfengine.CURRENT - the current activity objects</li>
 *   <li>org.pgist.wfengine.HISTORIES - a set of RunningHistory objects</li>
 *   <li>org.pgist.wfengine.FUTURES - a set of PManualGameActivity objects</li>
 *   <li>org.pgist.wfengine.ACTIVITY_RUNNING - whether or not the current activity is in running state</li>
 * </ul>
 * 
 * @author kenny
 */
public class WorkflowAction extends Action {
    
    
    WorkflowEngine engine;
    
    
    public void setEngine(WorkflowEngine engine) {
        this.engine = engine;
    }
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    private Long getParameter(HttpServletRequest request, String name) {
        String param = (String) request.getParameter(name);
        if (param==null) return null;
        
        param = param.trim();
        
        if (param==null) return null;
        
        return new Long(param);
    }//getParameter()


    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        /*
         * TODO: Get the current workflow instance, extract the current activitie,
         *       and forward to that activity
         */
        
        try {
            Long workflowId = getParameter(request, "workflowId");
            Long contextId = getParameter(request, "contextId");
            Long activityId = getParameter(request, "activityId");
            
            ActionForward forward = new ActionForward();
            
            Map result = null;
            
            /*
             * Running Activity
             */
            result = engine.getURL(workflowId, contextId, activityId);
            forward.setPath((String) result.get("link"));
            request.setAttribute("org.pgist.wfengine.ACTIVITY_RUNNING", result.get("status"));
            
            /*
             * Future and History
             */
            request.setAttribute("org.pgist.wfengine.CURRENT", result.get("activity"));
            request.setAttribute("org.pgist.wfengine.HISTORIES", result.get("histories"));
            request.setAttribute("org.pgist.wfengine.FUTURES", result.get("futures"));
            
            /*
             * Inject workflow information to request
             */
            request.setAttribute("org.pgist.wfengine.WORKFLOW_ID", workflowId);
            request.setAttribute("org.pgist.wfengine.CONTEXT_ID", contextId);
            request.setAttribute("org.pgist.wfengine.ACTIVITY_ID", activityId);
            
            return forward;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return mapping.findForward("UnknownActivity");
    }//execute()
    
    
}//class WorkflowAction
