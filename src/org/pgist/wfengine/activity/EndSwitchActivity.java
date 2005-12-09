package org.pgist.wfengine.activity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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
public class EndSwitchActivity extends Activity implements PushDownable {
    
    
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
            //if ==0, wait for user response
            //if ==1, jump to the next activity
            
            if (performerClass!=null && !"".equals(performerClass)) {
                result = doPerform(env);;
            }
            
        }
        
        if (result==1 && next!=null) {
            stack.push(next);
        } else {
            waitingList.add(this);
        }
        
        return (result==1);
    }//activate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class EndSwitchActivity
