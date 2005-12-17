package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.BackTracable;
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
public class SwitchActivity extends Activity implements BackTracable {
    
    
    protected int expression = -1;
    
    protected EndSwitchActivity endSwitchActivity;
    
    protected List switches = new ArrayList();
    
    protected Activity others = null;
    
    protected Activity prev = null;

    protected transient EndSwitchActivity embryoEndSwitch;
    
    
    public SwitchActivity() {
    }
    
    
    public Activity clone(Activity prev) {
        try {
            SwitchActivity embryo = new SwitchActivity();
            embryo.setAutomatic(this.automatic);
            embryo.setCaption(this.caption);
            embryo.setPerformerClass(this.performerClass);
            embryo.setUrl(this.url);
            embryo.setPrev(prev);
            embryo.getSwitches().clear();
            
            //set the status
            if (endSwitchActivity!=null) {
                embryoEndSwitch = new EndSwitchActivity();
                embryoEndSwitch.setAutomatic(endSwitchActivity.getAutomatic());
                embryoEndSwitch.setCaption(endSwitchActivity.getCaption());
                embryoEndSwitch.setPerformerClass(endSwitchActivity.getPerformerClass());
                embryoEndSwitch.setUrl(endSwitchActivity.getUrl());
                embryo.setEndSwitchActivity(embryoEndSwitch);
                embryoEndSwitch.setSwitchActivity(embryo);
            }
            
            for (Iterator iter=switches.iterator(); iter.hasNext(); ) {
                Activity branch = (Activity) iter.next();
                BackTracable embryoBranch = (BackTracable) branch.clone(embryo);
                embryo.getSwitches().add(embryoBranch);
            }//for iter
            
            //reset the status
            embryoEndSwitch = null;
            
            return embryo;
        } catch(Exception e) {
            return null;
        }
    }
    
    
    public Activity probe() {
        return endSwitchActivity.probe();
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
     * @hibernate.many-to-one column="other_id" class="org.pgist.wfengine.Activity" cascade="all"
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
