package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.AutoTask;
import org.pgist.wfengine.BackTracable;
import org.pgist.wfengine.ManualTask;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.WorkflowEnvironment;


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


    protected Activity[] doActivate(Workflow workflow, WorkflowEnvironment env) {
        //Terminate Activity have to be handled differently
        count++;
        if (task==null) {
            return null;
        } else if (task instanceof AutoTask) {
            ((AutoTask)task).execute(workflow, env, this);
            return null;
        } else {
            ((ManualTask)task).init(workflow, env, this);
            return new Activity[] { this };
        }
    }//doActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
    }//saveState()
    
    
}//class TerminateActivity
