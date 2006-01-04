package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.BackTracable;
import org.pgist.wfengine.PushDownable;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


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
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public Activity clone(Activity prev) {
        try {
            UntilActivity embryo = repeat.embryoUntil;
            embryo.setPrev(prev);
            if (task!=null) embryo.setTask( (Task) task.clone(embryo) );
            
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


    protected Activity[] doExecute(Workflow workflow) throws Exception {
        if (task==null) {//infinite loop
            setExpression(1);
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            task.execute(workflow);
        }
        
        if (getExpression()==0) {//task is not finished
            return new Activity[] { this };
        } else if (getExpression()>0) {
            return new Activity[] { next };
        } else {
            return new Activity[] { repeat };
        }
    }//doExecute()
    
    
    protected void doDeActivate(Workflow workflow) {
        //reset loopCount before leaving the loop
        repeat.setLoopCount(0);
    }//doDeActivate()
    
    
    public void proceed() throws Exception {
        setExpression(1);
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        setExpression(decision);
    }//proceed()


    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class UntilActivity
