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
 * While Activity class for While/Loop.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="WhileActivity" table="litwf_activity_while"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.WhileActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class WhileActivity extends Activity implements SingleIn, SingleOut {
    
    
    private static final long serialVersionUID = 5888916404788773433L;

    protected int loopCount = 0;

    protected int expression = 0;
    
    protected LoopActivity loop;
    
    protected Activity prev;
    
    protected Activity next;

    protected transient LoopActivity embryoLoop;

    
    public WhileActivity() {
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
     * @hibernate.many-to-one column="loop_id" class="org.pgist.wfengine.activity.LoopActivity" cascade="all"
     */
    public LoopActivity getLoop() {
        return loop;
    }
    
    
    public void setLoop(LoopActivity loop) {
        this.loop = loop;
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
    
    
    protected void doActivate(Workflow workflow) {
    }//doActivate()
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        if (task==null) {
            setExpression(1);
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            task.execute(context);
        }
        
        if (getExpression()==0) {
            return false;
        } else if (getExpression()>0) {
            next.activate(context);
            stack.push(next);
        } else {
            loop.activate(context);
            stack.push(loop);
        }
        return true;
    }//doActivate()
    
    
    protected void doDeActivate(Workflow workflow) {
        loopCount++;
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


}//class WhileActivity
