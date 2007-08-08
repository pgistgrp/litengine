package org.pgist.wfengine.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;


/**
 * 
 * @author kenny
 * 
 */
public class WorkflowFilter implements Filter {
    
    
    WorkflowUtils workflowUtils;
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    protected FilterConfig filterConfig = null;
    
    
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        
        System.out.println("  ---> starting workflow filter");
        
        try {
            WebApplicationContext context = (WebApplicationContext) filterConfig.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            workflowUtils = (WorkflowUtils) context.getBean("workflowUtils");
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
            
            workflowUtils.processWorkflowInfo(request, workflowId, contextId, activityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        chain.doFilter(request, response);
    }//doFilter()
    
    
    public void destroy() {
    }//destroy()
    

}//class PgistFilter
