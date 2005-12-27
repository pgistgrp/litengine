package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.AutoTask;
import org.pgist.wfengine.BackTracable;
import org.pgist.wfengine.ManualTask;
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
public class BranchActivity extends Activity implements BackTracable {
    
    
    protected Activity prev;

    protected JoinActivity joinActivity;
    
    protected List branches = new ArrayList();
    
    protected transient JoinActivity embryoJoin;
    
    
    public BranchActivity() {
    }
    
    
    public Activity clone(Activity prev) {
        try {
            BranchActivity embryo = new BranchActivity();
            embryo.setCaption(this.caption);
            embryo.setUrl(this.url);
            embryo.setPrev(prev);
            embryo.getBranches().clear();
            
            //set the status
            if (joinActivity!=null) {
                embryoJoin = new JoinActivity();
                embryoJoin.setCaption(joinActivity.getCaption());
                embryoJoin.setUrl(joinActivity.getUrl());
                embryo.setJoinActivity(embryoJoin);
                embryoJoin.setBranchActivity(embryo);
                if (task!=null) embryo.setTask( (Task) task.clone(embryo) );
            }
            
            for (Iterator iter=branches.iterator(); iter.hasNext(); ) {
                Activity branch = (Activity) iter.next();
                BackTracable embryoBranch = (BackTracable) branch.clone(embryo);
                embryo.getBranches().add(embryoBranch);
            }//for iter
            
            //reset the status
            embryoJoin = null;
            
            return embryo;
        } catch(Exception e) {
            return null;
        }
    }//clone()

    
    public Activity probe() {
        return joinActivity.probe();
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
    
    
    protected Activity[] doActivate(Workflow workflow) {
        //initialize branch/join pair
        joinActivity.setJoinCount(0);
        
        if (task==null) {
            Activity[] activities = new Activity[branches.size()];
            for (int i=0; i<activities.length; i++) {
                activities[i] = (Activity) branches.get(i);
            }//for i
            
            return activities;
        } else if (task instanceof AutoTask) {
            //Execute Auto Task, discard the return value
            ((AutoTask)task).execute(workflow);
            
            Activity[] activities = new Activity[branches.size()];
            for (int i=0; i<activities.length; i++) {
                activities[i] = (Activity) branches.get(i);
            }//for i
            
            return activities;
        } else {
            //Execute Manual Task, discard the return value
            ((ManualTask)task).init(workflow);
            return new Activity[] { this };
        }
    }//doActivate()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
        for (int i=0, n=branches.size(); i<n; i++) {
            Activity one = (Activity) branches.get(i);
            one.saveState(session);
        }//for i
    }//saveState()


}//class SwitchActivity
