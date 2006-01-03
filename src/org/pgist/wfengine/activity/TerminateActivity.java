package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.BackTracable;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="SequenceActivity" table="litwf_activity_terminate"
 * @hibernate.joined-subclass-key column="id"
 */
public class TerminateActivity extends Activity implements BackTracable {
    
    
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
    
    
    /**
     * Clone of terminate activity need not be implemented ?????
     */
    public Activity clone(Activity prev) {
        return null;
    }


    public Activity probe() {
        return this;
    }


    protected Activity[] doExecute(Workflow workflow) throws Exception {
        //Terminate Activity have to be handled differently
        if (task==null) {
            expression = 1;
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            task.execute(workflow);
        }
        
        if (expression>0) {//task is finished
            return new Activity[] {};
        } else {
            return new Activity[] { this };
        }
    }//doExecute()
    
    
    public void proceed() throws Exception {
        expression = 1;
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        //discard the decision
        expression = 1;
    }//proceed()


    public void saveState(Session session) {
        session.save(this);
    }//saveState()
    
    
}//class TerminateActivity
