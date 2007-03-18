package org.pgist.wfengine;

import java.util.Map;


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
     * @return if the execution succeed, return true; else return false
     */
    boolean execute(Activity activity, RunningContext context, Map<String, String> properties);
    
    
}//@interface WorkflowTask
