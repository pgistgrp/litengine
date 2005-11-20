package org.pgist.wfengine.activity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * EndSwitch Activity class for Switch/EndSwitch.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="EndSwitchActivity" table="litwf_activity_endswitch"
 * @hibernate.joined-subclass-key column="id"
 */
public class EndSwitchActivity extends Activity {
    
    
    private SwitchActivity switchActivity;
    
    private Set choices = new HashSet();
    
    private Activity next;
    
    
    public EndSwitchActivity() {
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="switch_id" class="org.pgist.wfengine.activity.SwitchActivity" casecad="all"
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


}//class EndSwitchActivity
