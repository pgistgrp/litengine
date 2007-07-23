package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;


/**
 * Switch Activity class for Switch/EndSwitch.
 * 
 * The structure Switch/EndSwitch of LIT WF Engine is different from that of Branch/Join
 * in that Switch/EndSwitch only select exactly one branch to execute, while ranch/Join
 * will execute all switches.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="SwitchActivity" table="litwf_activity_switch"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.SwitchActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class SwitchActivity extends Activity implements SingleIn {
    
    
    private static final long serialVersionUID = 8853658605696294187L;
    
    
    protected EndSwitchActivity endSwitchActivity = new EndSwitchActivity(this);
    
    protected List<Activity> switches = new ArrayList<Activity>();
    
    protected Activity other = null;
    
    protected Activity prev = null;
    
    
    public SwitchActivity() {
        type = TYPE_SWITCH;
    }
    
    
    /**
     * @return
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
     * @hibernate.many-to-one column="endswitch_id" class="org.pgist.wfengine.activity.EndSwitchActivity" cascade="all"
     */
    public EndSwitchActivity getEndSwitchActivity() {
        return endSwitchActivity;
    }


    public void setEndSwitchActivity(EndSwitchActivity endSwitchActivity) {
        this.endSwitchActivity = endSwitchActivity;
    }


    /**
     * @return
     * 
     * @hibernate.list table="litwf_activity" lazy="true" cascade="all"
     * @hibernate.collection-key column="switch_id"
     * @hibernate.collection-index column="switch_order"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     * 
     */
    public List<Activity> getSwitches() {
        return switches;
    }
    
    
    public void setSwitches(List<Activity> branches) {
        this.switches = branches;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="other_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getOther() {
        return other;
    }
    
    
    public void setOther(Activity other) {
        this.other = other;
    }
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public SwitchActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        SwitchActivity newSwitch = new SwitchActivity();
        
        //basic info
        newSwitch.setCounts(0);
        newSwitch.setPrev(clonedPrev);
        
        //branches
        clonedStop.push(newSwitch.getEndSwitchActivity());
        stop.push(getEndSwitchActivity());
        for (Activity one : getSwitches()) {
            Activity newOne = one.clone(newSwitch, clonedStop, stop);
            newSwitch.getSwitches().add(newOne);
        }//for
        
        return newSwitch;
    }//clone()
    
    
    public Activity getEnd() {
        return getEndSwitchActivity().getEnd();
    }//getEnd()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context) throws Exception {
        return true;
    }//doExecute()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
        for (int i=0, n=switches.size(); i<n; i++) {
            Activity one = (Activity) switches.get(i);
            one.saveState(session);
        }//for i
        if (other!=null) other.saveState(session);
    }//saveState()


    public boolean isAutomatic() {
        return false;
    }//isAutomatic()
    
    
    public void setFuture(List futures, Set embedding) {
        for (Activity activity : getSwitches()) {
            activity.setFuture(futures, embedding);
        }//for
    }//setFuture()


}//class SwitchActivity
