package org.pgist.wfengine.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.pgist.wfengine.RunningHistory;
import org.pgist.wfengine.WorkflowEngine;


/**
 * Struts Action for Workflow Running.
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
            Long historyId = getParameter(request, "historyId");
            
            ActionForward forward = new ActionForward();
            
            if (activityId!=null) {
                /*
                 * Running Activity
                 */
                forward.setPath(engine.getURL(workflowId, contextId, activityId));
            } else {
                /*
                 * Running History
                 */
                RunningHistory history = engine.getHistoryURL(workflowId, contextId, historyId);
                forward.setPath(history.getLink());
            }
            
            return forward;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return mapping.findForward("UnknownActivity");
    }//execute()
    
    
}//class WorkflowAction
