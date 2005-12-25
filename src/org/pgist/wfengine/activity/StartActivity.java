package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.AutoTask;
import org.pgist.wfengine.ManualTask;
import org.pgist.wfengine.PushDownable;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="StartActivity" table="litwf_activity_start"
 * @hibernate.joined-subclass-key column="id"
 */
public class StartActivity extends Activity implements PushDownable {
    
    
    protected Activity next;

    
    public StartActivity() {
    }
    
    
    /**
     * Clone of start activity need not be implemented
     */
    public Activity clone(Activity prev) {
        return null;
    }
    
    
    public Activity probe() {
        if (next==null) return this;
        return next.probe();
    }


    /**
     * @return
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getNext() {
        return next;
    }
    
    
    public void setNext(Activity next) {
        this.next = next;
    }

    
    protected Activity[] doActivate(Workflow workflow, WorkflowEnvironment env) {
        if (task==null) {
            return new Activity[] { next };
        } else if (task instanceof AutoTask) {
            ((AutoTask)task).execute(workflow, env, this);
            return new Activity[] { next };
        } else {
            ((ManualTask)task).init(workflow, env, this);
            return new Activity[] { this };
        }
    }//doActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class StartActivity
