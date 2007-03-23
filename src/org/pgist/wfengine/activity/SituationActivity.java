package org.pgist.wfengine.activity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.util.Utils;


/**
 * <p>Class SituationActivity is a special GroupActivity for decision situation.
 * 
 * @hibernate.joined-subclass name="SituationActivity" table="litwf_activity_group_situation"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.SituationActivity"
 * @hibernate.joined-subclass-key column="id"
 * 
 * @author kenny
 */
public class SituationActivity extends GroupActivity {
    
    
    public SituationActivity() {
        type = TYPE_PMETHOD;
    }
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public SituationActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        SituationActivity situation = new SituationActivity();
        
        //basic info
        situation.setCounts(0);
        situation.setPrev(clonedPrev);
        situation.setStatus(STATUS_INACTIVE);
        situation.setType(getType());
        situation.setName(getName());
        situation.setDescription(getDescription());
        
        Activity act = getNext();
        if (act!=null) {
            Activity newAct = act.clone(situation, clonedStop, stop);
            situation.setNext(newAct);
        }
        
        return situation;
    }//clone()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected void doActivate(RunningContext context) {
        //create a return activity
        ReturnActivity returnActivity = new ReturnActivity();
        returnActivity.setCounts(0);
        returnActivity.setExpression(0);
        returnActivity.setGroup(this);
        returnActivity.setType(Activity.TYPE_RETURN);
        
        //duplicate from the definition
        //FlowPiece piece = duplicate((SingleIn) getDefinition().getHeadActivity(), (SingleOut) getDefinition().getTailActivity());
        //piece.getTail().setNext(returnActivity);
        
        //set head and tail
        //setHeadActivity((Activity) piece.getHead());
        setTailActivity(returnActivity);
        
        //put head in the context
        getContext().addActivity(getHeadActivity());
        setExpression(0);
    }//doActivate()
    
    
    synchronized protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        RunningContext myContext = getContext();
        if (myContext!=null) {
            myContext.execute();
        }
        if (getExpression()>0) {//task is finished
            next.activate(context);
            stack.push(next);
            return true;
        }
        return false;
    }//doExecute()
    
    
    protected void doDeActivate(RunningContext context) {
        setHeadActivity(null);
        setTailActivity(null);
        setContext(null);
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
    public Set getRunningActivities(int type) {
        Set set = new HashSet();
        if (context==null) return set;
        Set activities = context.getRunningActivities();
        for (Iterator iter=activities.iterator(); iter.hasNext(); ) {
            Activity one = (Activity) Utils.narrow(iter.next());
            if (type == one.getType()) set.add(one);
        }//for iter
        return set;
    }//getRunningActivities()
    
    
}//class SituationActivity
