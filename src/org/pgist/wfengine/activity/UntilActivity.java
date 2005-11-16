package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * The "until" activity for repeat-until logic.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="UntilActivity" table="litwf_activity_until"
 * @hibernate.joined-subclass-key column="id"
 */
public class UntilActivity extends Activity {
    
    
    private RepeatActivity repeat;
    
    private Activity prev;
    
    private Activity next;
    
    
    /**
     * @return
     * @hibernate.many-to-one column="repeat_id" class="org.pgist.wfengine.activity.RepeatActivity" casecad="all"
     */
    public RepeatActivity getRepeat() {
        return repeat;
    }
    
    
    public void setRepeat(RepeatActivity repeat) {
        this.repeat = repeat;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="prev_id" class="org.pgist.wfengine.Activity" casecad="all"
     */
    public Activity getPrev() {
        return prev;
    }
    
    
    public void setPrev(Activity prev) {
        this.prev = prev;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" casecad="all"
     */
    public Activity getNext() {
        return next;
    }


    public void setNext(Activity next) {
        this.next = next;
    }


    public boolean activate(WorkflowEnvironment env) {
        return false;
    }
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class UntilActivity
