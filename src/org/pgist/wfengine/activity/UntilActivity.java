package org.pgist.wfengine.activity;

import java.util.List;
import java.util.Stack;

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
    
    
    public boolean activate(WorkflowEnvironment env) {
        Stack stack = (Stack) env.getExecuteStack();
        List waitingList = (List) env.getWaitingList();
        
        if (performerClass!=null && !"".equals(performerClass)) {
            expression = doPerform(env);;
        }

        if (expression==1 && next!=null) {
            next.reach(this, env);
            stack.push(next);
            return true;
        } else if (expression==0 && repeat!=null) {
            repeat.reach(this, env);
            stack.push(repeat);
            return true;
        } else {
            waitingList.add(this);
        }
        
        return false;
    }//activate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class UntilActivity
