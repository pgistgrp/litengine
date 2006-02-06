package org.pgist.wfengine.tasks;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pgist.wfengine.ManualTask;
import org.pgist.wfengine.RunningContext;


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
    
    
    protected void execute(RunningContext context, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("---> task is executed: InputNameTask");
    }//execute()
    
    
}//class InputNameTask
