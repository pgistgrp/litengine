package org.pgist.wfengine.activity;

import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


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
    
    
    private static final long serialVersionUID = 6198938783718025831L;

    protected int expression = 0;
    
    protected WhileActivity whilst;
    
    protected Activity prev;
    
    protected Activity next;
    
    
    public LoopActivity() {
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
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        if (task==null) {
            setExpression(1);
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            task.execute(context);
        }
        
        if (getExpression()>0) {//task is finished
            whilst.activate(context);
            stack.push(whilst);
            return true;
        }
        return false;
    }//doExecute()
    
    
    protected void doDeActivate(Workflow workflow) {
        //reset loopCount before leaving the loop
        whilst.setLoopCount(0);
    }//doDeActivate()
    
    
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
