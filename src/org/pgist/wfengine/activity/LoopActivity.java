package org.pgist.wfengine.activity;

import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;


/**
 * Loop Activity class for While/Loop.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="LoopActivity" table="litwf_activity_loop"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.LoopActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class LoopActivity extends Activity implements SingleIn, SingleOut {
    
    
    private static final long serialVersionUID = -5478838368057460061L;
    

    protected int expression = 0;
    
    protected WhileActivity whilst;
    
    protected Activity prev;
    
    protected Activity next;
    
    
    public LoopActivity() {
        type = TYPE_LOOP;
    }
    
    
    public LoopActivity(WhileActivity whilst) {
        type = TYPE_LOOP;
        this.whilst = whilst;
    }
    
    
    /**
     * @return
     * 
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
     * 
     * @hibernate.many-to-one column="while_id" class="org.pgist.wfengine.activity.WhileActivity" cascade="all"
     */
    public WhileActivity getWhilst() {
        return whilst;
    }
    
    
    public void setWhilst(WhileActivity whilst) {
        this.whilst = whilst;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.many-to-one column="prev_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getPrev() {
        return prev;
    }
    
    
    public void setPrev(Activity prev) {
        this.prev = prev;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" cascade="all"
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
    
    
    public LoopActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        LoopActivity newLoop = (LoopActivity) clonedStop.peek();
        newLoop.setPrev(clonedPrev);
        
        clonedStop.pop();
        stop.pop();
        
        Activity act = getNext();
        if (act!=null) {
            Activity newAct = act.clone(newLoop, clonedStop, stop);
            newLoop.setNext(newAct);
        }
        
        return newLoop;
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
        context.addRunningActivity(getWhilst());
        
        return true;
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


}//class LoopActivity
