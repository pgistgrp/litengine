package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


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
 * @hibernate.joined-subclass-key column="id"
 */
public class BranchActivity extends Activity implements SingleIn {
    
    
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
    
    
    protected Activity[] doExecute(Workflow workflow) throws Exception {
        if (task==null) {
            setExpression(1);
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            task.execute(workflow);
        }
        
        if (getExpression()>0) {//task is finished
            Activity[] activities = new Activity[branches.size()];
            for (int i=0; i<activities.length; i++) {
                activities[i] = (Activity) branches.get(i);
            }//for i
            
            //initialize branch/join pair
            joinActivity.setJoinCount(0);
            
            return activities;
        } else {
            return new Activity[] { this };
        }
    }//doExecute()
    
    
    public void proceed() throws Exception {
        setExpression(1);
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        //discard the decision
        setExpression(1);
    }//proceed()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
        for (int i=0, n=branches.size(); i<n; i++) {
            Activity one = (Activity) branches.get(i);
            one.saveState(session);
        }//for i
    }//saveState()


}//class SwitchActivity
