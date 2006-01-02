package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.BackTracable;
import org.pgist.wfengine.PushDownable;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


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


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public Activity clone(Activity prev) {
        return null;
    }
    

    public Activity probe() {
        if (next==null) return this;
        return next.probe();
    }


    protected void doActivate(Workflow workflow) {
    }//doActivate()
    
    
    protected Activity[] doExecute(Workflow workflow) throws Exception {
        if (task==null) {
            return new Activity[] { next };
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            //Execute Auto Task
            task.initialize(workflow);
            int result = task.execute(workflow);
            task.finalize(workflow);
            
            if (result>=jumps.size()) result = jumps.size()-1;
            if (result<0) {
                return new Activity[] { next };
            } else {
                return new Activity[] { (Activity) jumps.get(result) };
            }
        } else {
            //initialize the task
            task.initialize(workflow);
            return new Activity[] { this };
        }
    }//doExecute()
    
    
    protected void doDeActivate(Workflow workflow) {
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class JumpActivity
