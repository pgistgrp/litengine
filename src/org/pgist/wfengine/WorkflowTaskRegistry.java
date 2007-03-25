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
    
    private Map<String, String> actions = new HashMap<String, String>();
    
    
    public WorkflowTaskRegistry() {
    }//WorkflowTaskRegistry()
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    public void setTasks(Map<String, WorkflowTask> tasks) {
        this.tasks = tasks;
    }


    public void setActions(Map<String, String> actions) {
        this.actions = actions;
    }


    /*
     * ------------------------------------------------------------------------
     */
    
    
    public WorkflowTask getTask(String name) {
        return tasks.get(name);
    }//getTask()
    
    
    public String getAction(String name) {
        return actions.get(name);
    }//getAction()
    
    
}//class WorkflowTaskRegistry
