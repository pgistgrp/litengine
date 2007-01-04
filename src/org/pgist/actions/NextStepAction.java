package org.pgist.actions;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * NextStep Action.<br>
 * 
 * @author kenny
 *
 */
public class NextStepAction extends Action {
    
    
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
         *       and proceed the current activity
         */
        
        return mapping.findForward("another");
    }//execute()
    
    
}//class NextStepAction
