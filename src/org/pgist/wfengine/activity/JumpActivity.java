package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.IPerformer;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * The JumpActivity class.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="JumpActivity" table="litwf_activity_jump"
 * @hibernate.joined-subclass-key column="id"
 */
public class JumpActivity extends Activity implements BackTracable, PushDownable {
    
    
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


    public boolean activate(WorkflowEnvironment env) {
        Stack stack = (Stack) env.getExecuteStack();
        
        expression = -1;
        
        try {
            if (performerClass!=null) {
                IPerformer performer = (IPerformer) Class.forName(performerClass).newInstance();
                expression = performer.perform(this, env);
            }
            
            if (expression<0 || expression>jumps.size()) {
                if (next!=null) stack.push(next);
            } else {
                stack.push(jumps.get(expression));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }//activate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    

}//class JumpActivity
