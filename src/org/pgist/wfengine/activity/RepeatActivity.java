package org.pgist.wfengine.activity;

import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="RepeatActivity" table="litwf_activity_repeat"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.RepeatActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepeatActivity extends Activity implements SingleIn, SingleOut {
    
    
    private static final long serialVersionUID = 1399861713508242529L;

    protected int loopCount = 0;
    
    protected int expression;

    protected UntilActivity until;
    
    protected Activity prev;
    
    protected Activity next;
    
    protected transient UntilActivity embryoUntil;

    
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
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        next.activate(context);
        stack.push(next);
        return true;
    }//doExecute()
    
    
    protected void doDeActivate(RunningContext context) {
        loopCount++;
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class RepeatActivity
