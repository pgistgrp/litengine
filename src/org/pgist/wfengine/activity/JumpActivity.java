package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;


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
    
    
    private static final long serialVersionUID = -8328987946706349239L;
    

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
    
    
    public Activity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        return null;
    }//clone()


    public Activity getEnd() {
        return null;
    }//getEnd()


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context) throws Exception {
        if (getExpression()>jumps.size()) setExpression(jumps.size());
        
        if (getExpression()==0) {
        } else if (getExpression()<0) {
            next.activate(context);
            context.addRunningActivity(next);
        } else {
            Activity activity = (Activity) jumps.get(getExpression()-1);
            activity.activate(context);
            context.addRunningActivity(activity);
        }
        
        return true;
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


    @Override
    public void getAgenda(List activities) {
        //Do nothing
    }//getAgenda
    
    
    @Override
    public Activity getNextStep(NextStepInfo nsi) {
        return getNext().getNextStep(nsi);
    }//getNextStep()
    
    
}//class JumpActivity
