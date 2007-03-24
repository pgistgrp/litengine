package org.pgist.wfengine;

import java.util.HashMap;
import java.util.Map;


/**
 * Registry class for WorkflowTask.
 * 
 * @author kenny
 * 
 */
public class WorkflowTaskRegistry {
    
    
    private Map<String, WorkflowTask> tasks = new HashMap<String, WorkflowTask>();
    
    
    public WorkflowTaskRegistry(Map<String, WorkflowTask> tasks) {
        this.tasks = tasks;
    }//WorkflowTaskRegistry()
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    public WorkflowTask getTask(String name) {
        return tasks.get(name);
    }//getTask()
    
    
    public Map<String, WorkflowTask> getTasks() {
        return tasks;
    }//getTasks()


}//class WorkflowTaskRegistry
