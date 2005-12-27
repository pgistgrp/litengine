package org.pgist.wfengine;


/**
 * Automatic Task
 * @author kenny
 *
 * @hibernate.joined-subclass name="Task" table="litwf_task_auto"
 * @hibernate.joined-subclass-key column="id"
 */
public abstract class AutoTask extends Task {
    
    
    abstract public int execute(Workflow workflow);
    
    
}//abstract class AutoTask
