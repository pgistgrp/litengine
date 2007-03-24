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
    void execute(EnvironmentInOuts inouts) throws Exception;
    
    
}//@interface WorkflowTask
