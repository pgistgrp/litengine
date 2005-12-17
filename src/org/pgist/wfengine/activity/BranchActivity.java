package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.BackTracable;
import org.pgist.wfengine.WorkflowEnvironment;


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
            embryo.setAutomatic(this.automatic);
            embryo.setCaption(this.caption);
            embryo.setPerformerClass(this.performerClass);
            embryo.setUrl(this.url);
            embryo.setPrev(prev);
            embryo.getBranches().clear();
            
            //set the status
            if (joinActivity!=null) {
                embryoJoin = new JoinActivity();
                embryoJoin.setAutomatic(joinActivity.getAutomatic());
                embryoJoin.setCaption(joinActivity.getCaption());
                embryoJoin.setPerformerClass(joinActivity.getPerformerClass());
                embryoJoin.setUrl(joinActivity.getUrl());
                embryo.setJoinActivity(embryoJoin);
                embryoJoin.setBranchActivity(embryo);
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
    
    
    public boolean activate(WorkflowEnvironment env) {
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
            stack.addAll(branches);
        }
        
        return (result!=UNDEFINED);
    }//activate()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
        for (int i=0, n=branches.size(); i<n; i++) {
            Activity one = (Activity) branches.get(i);
            one.saveState(session);
        }//for i
    }//saveState()
    
    
}//class SwitchActivity
