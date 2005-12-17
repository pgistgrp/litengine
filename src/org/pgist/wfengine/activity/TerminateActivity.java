package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.BackTracable;
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


    public boolean activate(WorkflowEnvironment env) {
        int result = UNDEFINED;
        
        if (performerClass!=null && !"".equals(performerClass)) {
            result = doPerform(env);
        }
        
        return (result==1);
    }//activate()
    
    
    public void saveState(Session session) {
        session.save(this);
    }//saveState()
    
    
}//class TerminateActivity
