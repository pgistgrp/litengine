package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.AutoTask;
import org.pgist.wfengine.BackTracable;
import org.pgist.wfengine.ManualTask;
import org.pgist.wfengine.PushDownable;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * The "until" activity for repeat-until logic.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="UntilActivity" table="litwf_activity_until"
 * @hibernate.joined-subclass-key column="id"
 */
public class UntilActivity extends Activity implements BackTracable, PushDownable {
    
    
    protected int loopCount = 0;
    
    protected int expression;

    private RepeatActivity repeat;
    
    private Activity prev;
    
    protected Activity next;
    
    
    public UntilActivity() {
    }
    
    
    public Activity clone(Activity prev) {
        try {
            UntilActivity embryo = repeat.embryoUntil;
            embryo.setPrev(prev);
            if (task!=null) embryo.setTask( (Task) task.clone() );
            
            if (embryo.next==null && next!=null) {
                Activity embryoNext = next.clone(embryo);
                embryo.setNext(embryoNext);
            }
            
            return embryo;
        } catch(Exception e) {
            return null;
        }
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

    
    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public int getLoopCount() {
        return loopCount;
    }


    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public int getExpression() {
        return expression;
    }


    public void setExpression(int expression) {
        this.expression = expression;
    }


    /**
     * @return
     * @hibernate.many-to-one column="repeat_id" class="org.pgist.wfengine.activity.RepeatActivity" cascade="all"
     */
    public RepeatActivity getRepeat() {
        return repeat;
    }
    
    
    public void setRepeat(RepeatActivity repeat) {
        this.repeat = repeat;
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
        if (task==null) {//infinite loop
            return new Activity[] { repeat };
        } else if (task instanceof AutoTask) {
            int result = ((AutoTask)task).execute(workflow, env, this);
            if (result==0) {
                //reset loopCount before leaving the loop
                loopCount = 0;
                return new Activity[] { next };
            } else {
                return new Activity[] { repeat };
            }
        } else {
            ((ManualTask)task).init(workflow, env, this);
            return new Activity[] { this };
        }
    }//doActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class UntilActivity
