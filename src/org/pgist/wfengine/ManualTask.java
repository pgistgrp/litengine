package org.pgist.wfengine;


/**
 * Manually running Task.
 * 
 * @author kenny
 *
 */
public abstract class ManualTask extends Task {

    
    abstract public int execute(Workflow workflow, WorkflowEnvironment env, Activity activity);
    
    
    /**
     * Give Task object an oppotunity to initialize itself
     *
     */
    public void init(Workflow workflow, WorkflowEnvironment env, Activity activity) {
    }
    
    
    /**
     * Give Task object an oppotunity to finalize itself
     *
     */
    public void destroy(Workflow workflow, WorkflowEnvironment env, Activity activity) {
    }
    
    
}//abstract class ManualTask
