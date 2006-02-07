package org.pgist.wfengine.activity;

import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.Task;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="TerminateActivity" table="litwf_activity_terminate"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.TerminateActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class TerminateActivity extends Activity implements SingleIn {
    
    
    private static final long serialVersionUID = 4089087931490531333L;
    
    protected Activity prev = null;
    
    
    public TerminateActivity() {
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="prev_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getPrev() {
        return prev;
    }
    
    
    public void setPrev(Activity prev) {
        this.prev = prev;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        //Terminate Activity have to be handled differently
        if (task==null) {
            setExpression(1);
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            task.execute(context);
        }
        
        return getExpression()>0;
    }//doExecute()
    
    
    public void saveState(Session session) {
        session.save(this);
    }//saveState()


}//class TerminateActivity
