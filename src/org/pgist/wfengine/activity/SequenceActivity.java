package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.BackTracable;
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

    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
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


    protected Activity[] doExecute(Workflow workflow) throws Exception {
        if (task==null) {
            setExpression(1);
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            task.execute(workflow);
        }
        
        if (getExpression()>0) {//task is finished
            return new Activity[] { next };
        } else {
            return new Activity[] { this };
        }
    }//doExecute()
    
    
    public void proceed() throws Exception {
        setExpression(1);
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        //discard the decision
        setExpression(1);
    }//proceed()


    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class SequenceActivity
