package org.pgist.wfengine.activity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * Join Activity class for Branch/Join.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="JoinActivity" table="litwf_activity_join"
 * @hibernate.joined-subclass-key column="id"
 */
public class JoinActivity extends Activity implements PushDownable {
    
    
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


    public void reach(Activity from, WorkflowEnvironment env) {
        if (joins.contains(from)) {
            joinCount++;
        }
    }//reach()
    
    
    public boolean activate(WorkflowEnvironment env) {
        if (joinCount<joins.size()) return false;
        
        Stack stack = (Stack) env.getExecuteStack();
        List waitingList = (List) env.getWaitingList();
        
        int result = UNDEFINED;
        
        if (automatic) {
            
            //For the automatic sequence activity, discard the return value
            //jump to the next activity
            
            if (performerClass!=null && !"".equals(performerClass)) {
                doPerform(env);
            }
            
            result = 1;
            
        } else {
            
            //For the non-automatic sequence activity, check the return value,
            //if ==UNDEFINED, wait for user response
            //else, jump to the other activity
            if (performerClass!=null && !"".equals(performerClass)) {
                result = doPerform(env);;
            }
            
        }
        
        if (result==UNDEFINED) {
            waitingList.add(this);
        } else {
            if (next!=null) {
                next.reach(this, env);
                stack.add(next);
            }
        }
        
        return (result!=UNDEFINED);
    }//activate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class JoinActivity
