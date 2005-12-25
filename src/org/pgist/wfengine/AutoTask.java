package org.pgist.wfengine;


/**
 * Automatic Task
 * @author kenny
 *
 */
public abstract class AutoTask extends Task {
    
    
    abstract public int execute(Workflow workflow, WorkflowEnvironment env, Activity activity);
    
    
}//abstract class AutoTask
