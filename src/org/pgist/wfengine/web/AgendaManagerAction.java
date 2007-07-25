package org.pgist.wfengine.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEngine;


/**
 * 
 * @author kenny
 *
 */
public class AgendaManagerAction extends Action {
    
    
    WorkflowEngine engine;
    
    
    public void setEngine(WorkflowEngine engine) {
        this.engine = engine;
    }
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        Long workflowId = new Long((String) request.getParameter("workflowId"));
        
        String save = (String) request.getParameter("save");
        
        if ("true".equalsIgnoreCase(save)) {
            String[] ids = request.getParameterValues("activity_id");
            
            Map<Long, Date> beginTimes = new HashMap<Long, Date>();
            Map<Long, Date> endTimes = new HashMap<Long, Date>();
            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            
            for (String id : ids) {
                String time = request.getParameter(id+"_begin");
                if (time!=null && time.trim().length()>0) {
                    beginTimes.put(new Long(id), dateFormat.parse(time));
                }
                
                time = request.getParameter(id+"_end");
                if (time!=null && time.trim().length()>0) {
                    endTimes.put(new Long(id), dateFormat.parse(time));
                }
            }//for
            
            engine.updateAgenda(workflowId, beginTimes, endTimes);
            
            List<List<Activity>> activities = engine.getAgenda(workflowId);
            request.setAttribute("activities", activities);
            
            return mapping.findForward("done");
        } else {
            List<List<Activity>> activities = engine.getAgenda(workflowId);
            request.setAttribute("activities", activities);
            return mapping.findForward("view");
        }
    }//execute()
    
    
}//class AgendaManagerAction
