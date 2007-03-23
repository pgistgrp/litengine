package org.pgist.wfengine.activity;

import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.Workflow;


/**
 * The "until" activity for repeat-until logic.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="UntilActivity" table="litwf_activity_until"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.UntilActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class UntilActivity extends Activity implements SingleIn, SingleOut {
    
    
    private static final long serialVersionUID = -3847966226618977676L;
    
    
    protected int loopCount = 0;
    
    protected int expression;
    
    private RepeatActivity repeat;
    
    private Activity prev;
    
    protected Activity next;
    
    
    public UntilActivity() {
        type = TYPE_UNTIL;
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
    
    
    public UntilActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        UntilActivity newUntil = (UntilActivity) clonedStop.peek();
        newUntil.setPrev(clonedPrev);
        
        clonedStop.pop();
        stop.pop();
        
        Activity act = getNext();
        if (act!=null) {
            Activity newAct = act.clone(newUntil, clonedStop, stop);
            newUntil.setNext(newAct);
        }
        
        return newUntil;
    }//clone()
    
    
    public Activity getEnd() {
        Activity act = getNext();
        if (act==null) {
            return this;
        } else {
            return act.getEnd();
        }
    }//getEnd()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context) throws Exception {
        if (getExpression()==0) {//task is not finished
        } else if (getExpression()>0) {
            next.activate(context);
            context.getStack().push(next);
        } else {
            repeat.activate(context);
            context.getStack().push(repeat);
        }
        
        return false;
    }//doExecute()
    
    
    protected void doDeActivate(Workflow workflow) {
        //reset loopCount before leaving the loop
        repeat.setLoopCount(0);
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class UntilActivity
