package org.pgist.wfengine.tasks;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pgist.wfengine.ManualTask;
import org.pgist.wfengine.Workflow;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="InputNameTask" table="litwf_inputnametask"
 * @hibernate.joined-subclass-key column="id"
 */
public class InputNameTask extends ManualTask {
    
    
    public InputNameTask() {
    }
    
    
    protected int execute(Workflow workflow, HttpServletRequest request, HttpServletResponse response) {
        
        return 0;
    }//execute()
    
    
}//class InputNameTask
