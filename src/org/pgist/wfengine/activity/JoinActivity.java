package org.pgist.wfengine.activity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


/**
 * Join Activity class for Branch/Join.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="JoinActivity" table="litwf_activity_join"
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
    
    
    protected void doActivate(Workflow workflow) {
        joinCount++;
    }//doActivate()
    
    
    protected Activity[] doExecute(Workflow workflow) throws Exception {
        if (joinCount>=joins.size()) { 
            if (task==null) {
                expression = 1;
            } else if (task.getType()==Task.TASK_AUTOMATIC) {
                task.execute(workflow);
                expression = 1;
            }
        } else {
            //temporarily kill this thread
            return new Activity[] {};
        }
        
        if (expression>0) {//task is finished
            return new Activity[] { next };
        } else {
            return new Activity[] { this };
        }
    }//doExecute()
    
    
    public void proceed() throws Exception {
        setExpression(1);
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        setExpression(1);
    }//proceed()


    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class JoinActivity
