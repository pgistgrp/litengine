package org.pgist.wfengine.test;

import org.pgist.servlets.IWorkflowContext;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.ManualTask;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.WorkflowEnvironment;
/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="InputNameTask" table="litwf_inputnametask"
 * @hibernate.joined-subclass-key column="id"
 */
public class InputNameTask extends ManualTask {
    
    
    private String userName;

    
    public InputNameTask() {
    }
    
    
    public int execute(IWorkflowContext context) {
        return 0;
    }


    public int execute(Workflow workflow, WorkflowEnvironment env, Activity activity) {
        return 0;
    }


    public Object clone() {
        return null;
    }
    
    
}//class InputNameTask
