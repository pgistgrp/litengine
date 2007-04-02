package org.pgist.wfengine.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.pgist.wfengine.WorkflowEngine;
import org.springframework.web.context.WebApplicationContext;


/**
 * 
 * @author kenny
 *
 */
public class WorkflowListener implements ServletContextListener {
    
    
    private static WorkflowEngine engine;
    
    
    public static WorkflowEngine getEngine() {
        return engine;
    }
    
    
    public static void setEngine(WorkflowEngine wfengine) {
        engine = wfengine;
    }//setEngine()
    
    
    /*
     * ------------------------------------------------------------------------
     */


    public void contextDestroyed(ServletContextEvent event) {
    }//contextDestroyed()
    
    
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        WebApplicationContext context = (WebApplicationContext) servletContext.getAttribute
            (WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        
        engine = (WorkflowEngine) context.getBean("txEngine");
    }//contextInitialized()


}//class WorkflowListener
