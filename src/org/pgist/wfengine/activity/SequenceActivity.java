package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.AutoTask;
import org.pgist.wfengine.BackTracable;
import org.pgist.wfengine.ManualTask;
import org.pgist.wfengine.PushDownable;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


/**
 * The SequenceActivity implement the sequence flow activity.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="SequenceActivity" table="litwf_activity_sequence"
 * @hibernate.joined-subclass-key column="id"
 */
public class SequenceActivity extends Activity implements BackTracable, PushDownable {
    
    
    protected Activity prev;
    
    protected Activity next;
    
    
    public SequenceActivity() {
    }
    
    
    public Activity clone(Activity prev) {
        try {
            SequenceActivity embryo = new SequenceActivity();
            embryo.setCaption(this.caption);
            if (task!=null) embryo.setTask( (Task) task.clone(embryo) );
            embryo.setUrl(this.url);
            embryo.setPrev(prev);
            
            if (next!=null) {
                Activity embryoNext = next.clone(embryo);
                embryo.setNext(embryoNext);
            }
            
            return embryo;
        } catch(Exception e) {
            return null;
        }
    }//clone()

    
    public Activity probe() {
        if (next==null) return this;
        return next.probe();
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


    /**
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" cascade="all"

     * @return
     */
    public Activity getNext() {
        return next;
    }
    
    
    public void setNext(Activity next) {
        this.next = next;
    }

    
    protected Activity[] doActivate(Workflow workflow) {
        if (task==null) {
            return new Activity[] { next };
        } else if (task instanceof AutoTask) {
            ((AutoTask)task).execute(workflow, this);
            return new Activity[] { next };
        } else {
            ((ManualTask)task).init(workflow, this);
            return new Activity[] { this };
        }
    }//doActivate()


    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class SequenceActivity
