package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleOut;


/**
 * Join Activity class for Branch/Join.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="JoinActivity" table="litwf_activity_join"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.JoinActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class JoinActivity extends Activity implements SingleOut {
    
    
    private static final long serialVersionUID = 6282303570463799069L;
    

    protected Activity next;
    
    protected BranchActivity branchActivity;
    
    private List<Activity> joins = new ArrayList<Activity>();
    
    private int joinCount = 0;
    
    
    public JoinActivity() {
        type = TYPE_JOIN;
    }
    
    
    public JoinActivity(BranchActivity branchActivity) {
        this.branchActivity = branchActivity;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="next_id" cascade="all"
     */
    public Activity getNext() {
        return next;
    }
    
    
    public void setNext(Activity next) {
        this.next = next;
    }

    
    /**
     * @return
     * @hibernate.many-to-one column="branch_id" cascade="all"
     */
    public BranchActivity getBranchActivity() {
        return branchActivity;
    }


    public void setBranchActivity(BranchActivity branchActivity) {
        this.branchActivity = branchActivity;
    }


    /**
     * @return
     * 
     * @hibernate.list table="litwf_activity" lazy="true" cascade="all"
     * @hibernate.collection-key column="join_id"
     * @hibernate.collection-index column="join_order"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     * 
     */
    public List<Activity> getJoins() {
        return joins;
    }
    
    
    public void setJoins(List<Activity> joins) {
        this.joins = joins;
    }
    
    
    /**
     * @return
     * @hibernate.property
     */
    public int getJoinCount() {
        return joinCount;
    }


    public void setJoinCount(int joinCount) {
        this.joinCount = joinCount;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public JoinActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        JoinActivity newJoin = (JoinActivity) clonedStop.peek();
        newJoin.getJoins().add(clonedPrev);
        
        //check to propagate
        if (newJoin.getJoins().size()==getJoins().size()) {
            clonedStop.pop();
            stop.pop();
            Activity act = getNext();
            if (act!=null) {
                Activity newAct = act.clone(newJoin, clonedStop, stop);
                newJoin.setNext(newAct);
            }
        }
        
        return newJoin;
    }//clone()
    
    
    public Activity getEnd() {
        Activity act = getNext();
        if (act==null) {
            return this;
        } else {
            return act.getEnd();
        }
    }//getEnd()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected void doActivate(RunningContext context) {
        setJoinCount(getJoinCount()+1);
    }//doActivate()
    
    
    protected boolean doExecute(RunningContext context) throws Exception {
        if (getJoinCount()>=joins.size()) {
            context.addRunningActivity(getNext());
            return true;
        }
        return false;
    }//doExecute()
    
    
    protected void doDeActivate(RunningContext context) {
        joinCount = 0;
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
    public void setFuture(List futures, Set embedding) {
        if (!embedding.contains(this)) {
            embedding.add(this);
            getNext().setFuture(futures, embedding);
        }
    }//setFuture()
    
    
}//class JoinActivity
