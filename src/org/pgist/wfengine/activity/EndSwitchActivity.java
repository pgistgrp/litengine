package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleOut;


/**
 * EndSwitch Activity class for Switch/EndSwitch.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="EndSwitchActivity" table="litwf_activity_endswitch"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.EndSwitchActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class EndSwitchActivity extends Activity implements SingleOut {
    
    
    private static final long serialVersionUID = -1212634166705377596L;
    
    
    protected Activity next;
    
    private SwitchActivity switchActivity;
    
    private List<Activity> choices = new ArrayList<Activity>();
    
    
    public EndSwitchActivity() {
        type = TYPE_ENDSWITCH;
    }
    
    
    public EndSwitchActivity(SwitchActivity switchActivity) {
        this.switchActivity = switchActivity;
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
     * @hibernate.list table="litwf_activity" lazy="true" cascade="all"
     * @hibernate.collection-key column="choice_id"
     * @hibernate.collection-index column="switch_order"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     * 
     */
    public List<Activity> getChoices() {
        return choices;
    }
    
    
    public void setChoices(List<Activity> switches) {
        this.choices = switches;
    }
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public EndSwitchActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        EndSwitchActivity newEswt = (EndSwitchActivity) clonedStop.peek();
        newEswt.getChoices().add(clonedPrev);
        
        //check to propagate
        if (newEswt.getChoices().size()==getChoices().size()) {
            clonedStop.pop();
            stop.pop();
            Activity act = getNext();
            if (act!=null) {
                Activity newAct = act.clone(newEswt, clonedStop, stop);
                newEswt.setNext(newAct);
            }
        }
        
        return newEswt;
    }//clone()
    
    
    public Activity getEnd() {
        Activity act = getNext();
        if (act==null) {
            return this;
        } else {
            return act.getEnd();
        }
    }//getEnd()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context) throws Exception {
        context.addRunningActivity(getNext());
        
        return true;
    }//doExecute()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
    public void setFuture(List futures, Set embedding) {
        if (!embedding.contains(this)) {
            embedding.add(this);
            getNext().setFuture(futures, embedding);
        }
    }//setFuture()
    
    
}//class EndSwitchActivity
