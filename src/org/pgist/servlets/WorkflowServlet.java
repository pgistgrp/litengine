package org.pgist.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.WorkflowEngine;
import org.springframework.web.context.WebApplicationContext;


/**
 * 
 * @author kenny
 *
 */
public class WorkflowServlet extends HttpServlet {

    
    private static final long serialVersionUID = 8750027589724323485L;
    
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException {
        execute(req, resp);
    }//doGet()
    
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException {
        execute(req, resp);
    }//doPost()
    
    
    private void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException {
        WorkflowContext wfContext = new WorkflowContext();
        
        //extract flowId
        try {
            String flowIdStr = (String) req.getParameter("flowId");
            Long flowId = new Long(flowIdStr);
            wfContext.setWorkflowId(flowId);
            WebApplicationContext appContext = (WebApplicationContext) getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            if (appContext==null) {
                throw new ServletException("Can't find spring web application context!");
            }
            WorkflowEngine engine = (WorkflowEngine) appContext.getBean("litengine");
            Workflow workflow = engine.getWorkflow(flowId);
            wfContext.setWorkflow(workflow);
        } catch(Exception e) {
            //error report
            e.printStackTrace();
            return;
        }
        
        //extract taskId
        try {
            String taskIdStr = (String) req.getParameter("taskId");
            Long taskId = new Long(taskIdStr);
            wfContext.setTaskId(taskId);
        } catch(Exception e) {
            e.printStackTrace();
            showFlowTasks(req, resp, wfContext);
            return;
        }
    }//execute()


    private void showFlowTasks(HttpServletRequest req, HttpServletResponse resp, IWorkflowContext context) throws ServletException, java.io.IOException {
        //List list = context.getWorkflow().getWaitingList();
        //req.setAttribute("runningList", list);
        
        req.getRequestDispatcher("/workflow.jsp").forward(req, resp);
    }//showFlowTasks()
    
    
}//class WorkflowServlet
