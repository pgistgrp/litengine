package org.pgist.wfengine.activity;

import java.util.Stack;

import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.WorkflowTask;


/**
 * The PAutoGameActivity implement the automatic pgame activity.<br>
 * 
 * One PGame is an atomic step or activity in a work flow.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="PAutoGameActivity" table="litwf_activity_pautogame"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.PAutoGameActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class PAutoGameActivity extends PGameActivity implements SingleIn, SingleOut {
    
    
    protected String taskName = null;
    
    
    public PAutoGameActivity() {
    }
    
    
    /**
     * @return
     * @hibernate.property column="task_name"
     */
    public String getTaskName() {
        return taskName;
    }


    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        Object object = context.getTask(getTaskName());
        if (object instanceof WorkflowTask) {
            WorkflowTask task = (WorkflowTask) object;
            return task.execute(this, context, getDeclaration().getProperties());
        }
        
        return false;
//        if (getExpression()>0) {//task is finished
//            next.activate(context);
//            stack.push(next);
//            return true;
//        } else {
//            return false;
//        }
    }//doExecute()
    
    
}//class PAutoGameActivity
