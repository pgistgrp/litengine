package org.pgist.wfengine.activity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="JoinActivity" table="litwf_activity_join"
 * @hibernate.joined-subclass-key column="id"
 */
public class JoinActivity extends Activity {
    
    
    protected BranchActivity branchActivity;
    
    private Set joins = new HashSet();
    
    private Activity next;
    
    
    public JoinActivity() {
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="branch_id" class="org.pgist.wfengine.activity.BranchActivity" casecad="all"
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
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" casecad="all"
     */
    public Activity getNext() {
        return next;
    }
    
    
    public void setNext(Activity next) {
        this.next = next;
    }
    
    
    public void reach(Activity from, WorkflowEnvironment env) {
        
    }//reach()
    
    
    public boolean activate(WorkflowEnvironment env) {
        return false;
    }//activate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class JoinActivity
