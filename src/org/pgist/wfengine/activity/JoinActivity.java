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
    
    
    private int joinType = 0;
    
    private Set branches = new HashSet();
    
    private Activity next;
    
    
    public int getJoinType() {
        return joinType;
    }
    
    
    public void setJoinType(int joinType) {
        this.joinType = joinType;
    }
    
    
    public Set getBranches() {
        return branches;
    }
    
    
    public void setBranches(Set branches) {
        this.branches = branches;
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
