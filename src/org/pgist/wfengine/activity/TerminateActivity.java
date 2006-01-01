package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.AutoTask;
import org.pgist.wfengine.BackTracable;
import org.pgist.wfengine.ManualTask;
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
     * Clone of terminate activity need not be implemented ?????
     */
    public Activity clone(Activity prev) {
        return null;
    }


    public Activity probe() {
        return this;
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


    protected void doActivate(Workflow workflow) {
    }//doActivate()
    
    
    protected Activity[] doExecute(Workflow workflow) {
        //Terminate Activity have to be handled differently
        if (task==null) {
            return null;
        } else if (task instanceof AutoTask) {
            ((AutoTask)task).execute(workflow);
            return null;
        } else {
            ((ManualTask)task).init(workflow);
            return new Activity[] { this };
        }
    }//doExecute()
    
    
    protected void doDeActivate(Workflow workflow) {
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
    }//saveState()
    
    
}//class TerminateActivity
