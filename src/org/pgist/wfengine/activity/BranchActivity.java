package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;

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
    
    
    private static final long serialVersionUID = 5281548477173534413L;
    

    protected Activity prev;
    
    protected JoinActivity joinActivity = new JoinActivity(this);
    
    protected List<Activity> branches = new ArrayList<Activity>();
    
    
    public BranchActivity() {
        type = TYPE_BRANCH;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.many-to-one column="prev_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getPrev() {
        return prev;
    }


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
    public List<Activity> getBranches() {
        return branches;
    }
    
    
    public void setBranches(List<Activity> branches) {
        this.branches = branches;
    }
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public BranchActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        BranchActivity newBranch = new BranchActivity();
        
        //basic info
        newBranch.setCounts(0);
        newBranch.setPrev(clonedPrev);
        
        //branches
        clonedStop.push(newBranch.getJoinActivity());
        stop.push(getJoinActivity());
        for (Activity one : getBranches()) {
            Activity newOne = one.clone(newBranch, clonedStop, stop);
            newBranch.getBranches().add(newOne);
        }//for
        
        return newBranch;
    }//clone()
    
    
    public Activity getEnd() {
        return getJoinActivity().getEnd();
    }//getEnd()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context) throws Exception {
        /*
         * Push all branches into the stack
         */
        for (Activity activity : getBranches()) {
            context.addRunningActivity(activity);
        }//for
        
        return true;
    }//doExecute()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
        for (int i=0, n=branches.size(); i<n; i++) {
            Activity one = (Activity) branches.get(i);
            one.saveState(session);
        }//for i
    }//saveState()
    
    
    public void setFuture(List futures, Set embedding) {
        for (Activity activity : getBranches()) {
            activity.setFuture(futures, embedding);
        }//for
    }//setFuture()


    @Override
    public void getAgenda(List activities) {
        for (Activity one : getBranches()) {
            one.getAgenda(activities);
        }//for
        
        getJoinActivity().getNext().getAgenda(activities);
    }//getAgenda()


    @Override
    public Activity getNextStep(NextStepInfo nsi) {
        Activity target = null;
        
        for (Activity one : getBranches()) {
            target = one.getNextStep(nsi);
            if (target!=null) return target;
        }
        
        return getJoinActivity().getNext().getNextStep(nsi);
    }//getNextStep()
    
    
    @Override
    public void setSerial(SortedSet set) {
        for (Activity one : getBranches()) {
            one.setSerial(set);
        }
        
        getJoinActivity().getNext().setSerial(set);
    }//setSerial()


	@Override
	public void reEnable(RunningContext context, Activity start) {
		for (Activity one : getBranches()) {
            one.reEnable(context, start);
        }
		
		getJoinActivity().reEnable(context, start);
	}


}//class BranchActivity
