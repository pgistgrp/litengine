package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;


/**
 * Branch Activity class for Branch/Join.
 * 
 * The structure Switch/EndSwitch of LIT WF Engine is different from that of Branch/Join
 * in that Switch/EndSwitch only select exactly one branch to execute, while Branch/Join
 * will execute all branches.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="BranchActivity" table="litwf_activity_branch"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.BranchActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class BranchActivity extends Activity implements SingleIn {
    
    
    private static final long serialVersionUID = 4312508990687343279L;
    
    protected Activity prev;

    protected JoinActivity joinActivity;
    
    protected List branches = new ArrayList();
    
    
    public BranchActivity() {
    }
    
    
    public Activity getPrev() {
        return prev;
    }


    /**
     * @return
     * @hibernate.many-to-one column="prev_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public void setPrev(Activity prev) {
        this.prev = prev;
    }


    /**
     * @return
     * @hibernate.many-to-one column="join_id" class="org.pgist.wfengine.activity.JoinActivity" cascade="all"
     */
    public JoinActivity getJoinActivity() {
        return joinActivity;
    }


    public void setJoinActivity(JoinActivity joinActivity) {
        this.joinActivity = joinActivity;
    }


    /**
     * @return
     * 
     * @hibernate.list table="litwf_activity" lazy="true" cascade="all"
     * @hibernate.collection-key column="branch_id"
     * @hibernate.collection-index column="branch_order"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     * 
     */
    public List getBranches() {
        return branches;
    }
    
    
    public void setBranches(List branches) {
        this.branches = branches;
    }
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected Activity[] doExecute(RunningContext context) throws Exception {
        Activity[] activities = new Activity[branches.size()];
        for (int i=0; i<activities.length; i++) {
            activities[i] = (Activity) branches.get(i);
        }//for i
        
        //initialize branch/join pair
        joinActivity.setJoinCount(0);
        
        return activities;
    }//doExecute()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
        for (int i=0, n=branches.size(); i<n; i++) {
            Activity one = (Activity) branches.get(i);
            one.saveState(session);
        }//for i
    }//saveState()


}//class SwitchActivity
