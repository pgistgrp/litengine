package org.pgist.wfengine;


/**
 * An interface for Workflow Tasks.
 * 
 * @author kenny
 */
public interface WorkflowTask {
    
    
    /**
     * Execute the task.
     * 
     * @param context the running context for the task
     * @param properties a string to string map
     */
    void execute(WorkflowInfo info, EnvironmentInOuts inouts) throws Exception;
    
    
}//@interface WorkflowTask
