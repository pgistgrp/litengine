package org.pgist.wfengine.activity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


/**
 * EndSwitch Activity class for Switch/EndSwitch.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="EndSwitchActivity" table="litwf_activity_endswitch"
 * @hibernate.joined-subclass-key column="id"
 */
public class EndSwitchActivity extends Activity implements SingleOut {
    
    
    private static final long serialVersionUID = 3209784851120949570L;

    protected Activity next;
    
    private SwitchActivity switchActivity;
    
    private Set choices = new HashSet();
    
    
    public EndSwitchActivity() {
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
            return new Activity[] { next };
        } else {
            return new Activity[] { this };
        }
    }//doActivate()
    
    
    public void proceed() throws Exception {
        setExpression(1);
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        setExpression(1);
    }//proceed()


    protected void deActivate(Workflow workflow) {
        if (task!=null) {
            workflow.getTracker().record(task);
        }
    }//deActivate()

    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class EndSwitchActivity
