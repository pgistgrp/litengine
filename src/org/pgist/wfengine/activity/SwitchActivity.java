package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


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
 * @hibernate.joined-subclass-key column="id"
 */
public class SwitchActivity extends Activity implements SingleIn {
    
    
    protected EndSwitchActivity endSwitchActivity;
    
    protected List switches = new ArrayList();
    
    protected Activity others = null;
    
    protected Activity prev = null;
    
    protected transient EndSwitchActivity embryoEndSwitch;
    
    
    public SwitchActivity() {
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
    public List getSwitches() {
        return switches;
    }
    
    
    public void setSwitches(List branches) {
        this.switches = branches;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="other_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getOthers() {
        return others;
    }
    
    
    public void setOthers(Activity others) {
        this.others = others;
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
        
        if (getExpression()<0) setExpression(0);
        if (getExpression()>switches.size()) setExpression(switches.size());
        
        if (getExpression()==0) {
            return new Activity[] { this };
        } else {
            return new Activity[] { (Activity) switches.get(getExpression()-1) };
        }
    }//doExecute()
    
    
    public void proceed() throws Exception {
        setExpression(1);
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        setExpression(decision);
    }//proceed()


    public void saveState(Session session) {
        session.saveOrUpdate(this);
        for (int i=0, n=switches.size(); i<n; i++) {
            Activity one = (Activity) switches.get(i);
            one.saveState(session);
        }//for i
        if (others!=null) others.saveState(session);
    }//saveState()


}//class SwitchActivity
