package org.pgist.wfengine.activity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.AutoTask;
import org.pgist.wfengine.ManualTask;
import org.pgist.wfengine.PushDownable;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * EndSwitch Activity class for Switch/EndSwitch.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="EndSwitchActivity" table="litwf_activity_endswitch"
 * @hibernate.joined-subclass-key column="id"
 */
public class EndSwitchActivity extends Activity implements PushDownable {
    
    
    protected Activity next;
    
    private SwitchActivity switchActivity;
    
    private Set choices = new HashSet();
    
    
    public EndSwitchActivity() {
    }
    
    
    public Activity clone(Activity prev) {
        try {
            EndSwitchActivity embryo = switchActivity.embryoEndSwitch;
            embryo.getChoices().add(prev);
            
            if (embryo.next==null && next!=null) {
                Activity embryoNext = next.clone(embryo);
                embryo.setNext(embryoNext);
            }
            
            if (task!=null) embryo.setTask( (Task) task.clone() );
            
            return embryo;
        } catch(Exception e) {
            return null;
        }
    }


    public Activity probe() {
        if (next==null) return this;
        return next.probe();
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
     * @hibernate.many-to-one column="switch_id" class="org.pgist.wfengine.activity.SwitchActivity" cascade="all"
     */
    public SwitchActivity getSwitchActivity() {
        return switchActivity;
    }


    public void setSwitchActivity(SwitchActivity switchActivity) {
        this.switchActivity = switchActivity;
    }


    /**
     * @return
     * 
     * @hibernate.set table="litwf_activity" lazy="true" cascade="all"
     * @hibernate.collection-key column="choice_id"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     * 
     */
    public Set getChoices() {
        return choices;
    }
    
    
    public void setChoices(Set switches) {
        this.choices = switches;
    }
    
    
    protected Activity[] doActivate(Workflow workflow, WorkflowEnvironment env) {
        if (task==null) {
            return new Activity[] { next };
        } else if (task instanceof AutoTask) {
            ((AutoTask)task).execute(workflow, env, this);
            return new Activity[] { next };
        } else {
            ((ManualTask)task).init(workflow, env, this);
            return new Activity[] { this };
        }
    }//doActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class EndSwitchActivity
