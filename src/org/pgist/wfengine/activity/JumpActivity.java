package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.Task;


/**
 * The JumpActivity class.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="JumpActivity" table="litwf_activity_jump"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.JumpActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class JumpActivity extends Activity implements SingleIn, SingleOut {
    
    
    private static final long serialVersionUID = -5876606936184585374L;

    protected Activity prev = null;

    protected Activity next;
    
    protected int expression = 0;
    
    protected List jumps = new ArrayList();
    
    
    public JumpActivity() {
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

    
    /**
     * @return
     * @hibernate.property
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
     * @hibernate.list table="litwf_activity" lazy="true" cascade="all"
     * @hibernate.collection-key column="jump_id"
     * @hibernate.collection-index column="jump_order"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     * 
     */
    public List getJumps() {
        return jumps;
    }


    public void setJumps(List jumps) {
        this.jumps = jumps;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        if (task==null) {
            setExpression(-1);
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            task.execute(context);
        }
        
        if (getExpression()>jumps.size()) setExpression(jumps.size());
        
        if (getExpression()==0) {
            return false;
        } else if (getExpression()<0) {
            next.activate(context);
            stack.push(next);
            return true;
        } else {
            Activity activity = (Activity) jumps.get(getExpression()-1);
            activity.activate(context);
            stack.push(activity);
            return true;
        }
    }//doExecute()
    
    
    public void proceed() throws Exception {
        setExpression(-1);
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        setExpression(decision);
    }//proceed()


    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class JumpActivity
