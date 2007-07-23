package org.pgist.wfengine.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.WorkflowEngine;
import org.springframework.web.context.WebApplicationContext;


/**
 * 
 * @author kenny
 * 
 */
public class WorkflowFilter implements Filter {
    
    
    WorkflowEngine engine;
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    protected FilterConfig filterConfig = null;
    
    
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        
        System.out.println("  ---> starting workflow filter");
        
        try {
            WebApplicationContext context = (WebApplicationContext) filterConfig.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            engine = (WorkflowEngine) context.getBean("txEngine");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        
        System.out.println("  ---> workflow filter started");
        
    }//init()
    
    
    private Long getParameter(ServletRequest request, String name) {
        HttpServletRequest req = (HttpServletRequest) request;
        
        String param = (String) request.getParameter(name);
        if (param==null) return null;
        
        param = param.trim();
        
        if (param==null) return null;
        
        return new Long(param);
    }//getParameter()
    
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            Long workflowId = getParameter(request, "workflowId");
            Long contextId = getParameter(request, "contextId");
            Long activityId = getParameter(request, "activityId");
            
            Map result = null;
            
            result = engine.getURL(workflowId, contextId, activityId);
            
            request.setAttribute("org.pgist.wfengine.ACTIVITY_RUNNING", result.get("status"));
            
            /*
             * Future and History
             */
            RunningContext context = (RunningContext) result.get("context");
            request.setAttribute("org.pgist.wfengine.CURRENT", result.get("activity"));
            request.setAttribute("org.pgist.wfengine.HISTORIES", context.getHistories());
            request.setAttribute("org.pgist.wfengine.FUTURES", context.getFutureActivities());
            
            /*
             * Inject workflow information to request
             */
            request.setAttribute("org.pgist.wfengine.WORKFLOW_ID", workflowId);
            request.setAttribute("org.pgist.wfengine.CONTEXT_ID", contextId);
            request.setAttribute("org.pgist.wfengine.ACTIVITY_ID", activityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        chain.doFilter(request, response);
    }//doFilter()
    
    
    public void destroy() {
    }//destroy()
    

}//class PgistFilter
