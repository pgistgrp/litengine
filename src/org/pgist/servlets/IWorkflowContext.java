package org.pgist.servlets;

import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


/**
 * 
 * @author kenny
 *
 */
public interface IWorkflowContext {

    
    Long getWorkflowId();
    
    Workflow getWorkflow();
    
    Long getTaskId();
    
    Task getTask();
    
    
}//interface IWorkflowContext
