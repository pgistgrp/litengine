package org.pgist.actions;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * Workflow Action.<br>
 * 
 * @author kenny
 *
 */
public class WorkflowAction extends Action {
    
    
    public static final String ACTION_IN_WORKFLOW = "ACTION_IN_WORKFLOW";
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            javax.servlet.http.HttpServletRequest request,
            javax.servlet.http.HttpServletResponse response
    ) throws Exception {
        /*
         * TODO: Get the current workflow instance, extract the current activitie,
         *       and forward to that activity
         */
        
        return mapping.findForward("another");
    }//execute()
    
    
}//class WorkflowAction
