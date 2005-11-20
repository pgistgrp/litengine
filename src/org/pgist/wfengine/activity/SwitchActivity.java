package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;
import org.pgist.wfengine.IPerformer;


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
public class SwitchActivity extends Activity {
    
    
    protected int expression = -1;
    
    protected EndSwitchActivity endSwitchActivity;
    
    protected List switches = new ArrayList();
    
    protected Activity others = null;
    
    
    public SwitchActivity() {
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="endswitch_id" class="org.pgist.wfengine.activity.EndSwitchActivity" casecad="all"
     */
    public EndSwitchActivity getEndSwitchActivity() {
        return endSwitchActivity;
    }


    public void setEndSwitchActivity(EndSwitchActivity endSwitchActivity) {
        this.endSwitchActivity = endSwitchActivity;
    }


    /**
     * @return
     * @hibernate.property unique="false" not-null="true"
     */
    public int getExpression() {
        return expression;
    }
    
    
    public void setExpression(int expression) {
        this.expression = expression;
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
     * @hibernate.many-to-one column="other_id" class="org.pgist.wfengine.Activity" casecad="all"
     */
    public Activity getOthers() {
        return others;
    }
    
    
    public void setOthers(Activity others) {
        this.others = others;
    }
    
    
    public boolean activate(WorkflowEnvironment env) {
        Stack stack = (Stack) env.getExecuteStack();
        
        expression = -1;
        
        try {
            if (performerClass!=null) {
                IPerformer performer = (IPerformer) Class.forName(performerClass).newInstance();
                expression = performer.perform(this, env);
            }
            
            if (expression<0 || expression>switches.size()) {
                if (others!=null) stack.push(others);
            } else {
                stack.push(switches.get(expression));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }//activate()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
        for (int i=0, n=switches.size(); i<n; i++) {
            Activity one = (Activity) switches.get(i);
            one.saveState(session);
        }//for i
        if (others!=null) others.saveState(session);
    }//saveState()
    
    
}//class SwitchActivity
