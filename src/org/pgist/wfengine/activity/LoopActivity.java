package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="LoopActivity" table="litwf_activity_loop"
 * @hibernate.joined-subclass-key column="id"
 */
public class LoopActivity extends Activity {
    
    
    protected int count = 0;
    
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
    public int getCount() {
        return count;
    }


    public void setCount(int count) {
        this.count = count;
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
     * @hibernate.many-to-one column="while_id" class="org.pgist.wfengine.activity.WhileActivity" casecad="all"
     */
    public WhileActivity getWhilst() {
        return whilst;
    }
    
    
    public void setWhilst(WhileActivity whilst) {
        this.whilst = whilst;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" casecad="all"
     */
    public Activity getNext() {
        return next;
    }
    
    
    public void setNext(Activity next) {
        this.next = next;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="prev_id" class="org.pgist.wfengine.Activity" casecad="all"
     */
    public Activity getPrev() {
        return prev;
    }
    
    
    public void setPrev(Activity prev) {
        this.prev = prev;
    }
    
    
    public boolean activate(WorkflowEnvironment env) {
        return false;
    }
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class LoopActivity
