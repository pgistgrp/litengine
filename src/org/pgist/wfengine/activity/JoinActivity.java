package org.pgist.wfengine.activity;

import java.util.HashSet;
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
    
    
    private static final long serialVersionUID = 3802620632662633415L;

    protected Activity next;
    
    protected BranchActivity branchActivity;
    
    private Set joins = new HashSet();
    
    private int joinCount = 0;
    
    
    public JoinActivity() {
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
     * @hibernate.many-to-one column="branch_id" class="org.pgist.wfengine.activity.BranchActivity" cascade="all"
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
     * @hibernate.set table="litwf_activity" lazy="true" cascade="all"
     * @hibernate.collection-key column="join_id"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     * 
     */
    public Set getJoins() {
        return joins;
    }
    
    
    public void setJoins(Set joins) {
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
    
    
    protected void doActivate(RunningContext context) {
        joinCount++;
    }//doActivate()
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        if (joinCount>=joins.size()) {
            next.activate(context);
            stack.push(next);
            return true;
        }
        return false;
    }//doExecute()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class JoinActivity
