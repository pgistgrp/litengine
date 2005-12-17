package org.pgist.wfengine.test;

import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;
/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="InputNameTask" table="litwf_inputnametask"
 * @hibernate.joined-subclass-key column="id"
 */
public class InputNameTask extends Task {
    
    
    private String userName;

    
    public InputNameTask() {
    }
    
    
    public void execute(Workflow workflow) {
        
    }
    
    
}//class InputNameTask
