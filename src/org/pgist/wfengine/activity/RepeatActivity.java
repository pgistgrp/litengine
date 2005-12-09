package org.pgist.wfengine.activity;

import java.util.List;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="RepeatActivity" table="litwf_activity_repeat"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepeatActivity extends Activity implements BackTracable, PushDownable {
    
    
    protected int loopCount = 0;
    
    protected int expression;

    protected UntilActivity until;
    
    protected Activity prev;
    
    protected Activity next;
    
    
    public RepeatActivity() {
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
    
    
    /**
     * @return
     * @hibernate.many-to-one column="until_id" class="org.pgist.wfengine.activity.UntilActivity" cascade="all"
     */
    public UntilActivity getUntil() {
        return until;
    }
    
    
    public void setUntil(UntilActivity until) {
        this.until = until;
    }
    
    
    public void reach(Activity from, WorkflowEnvironment env) {
        loopCount++;
    }//reach()

    
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
        } else if (expression==0 && until!=null) {
            until.reach(this, env);
            stack.push(until);
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
    
    
}//class RepeatActivity
