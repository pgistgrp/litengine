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
 * <p>Class PMethodActivity is a special GroupActivity for pmethod.
 * 
 * @hibernate.joined-subclass name="PMethodActivity" table="litwf_activity_group_pmethod"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.PMethodActivity"
 * @hibernate.joined-subclass-key column="id"
 * 
 * @author kenny
 */
public class PMethodActivity extends GroupActivity {
    
    
    protected PMethodActivity definition;
    
    
    public PMethodActivity() {
        type = TYPE_PMETHOD;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.many-to-one column="definition_id" lazy="true" cascade="all"
     */
    public PMethodActivity getDefinition() {
        return definition;
    }


    public void setDefinition(PMethodActivity definition) {
        this.definition = definition;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public PMethodActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        PMethodActivity pmethod = new PMethodActivity();
        
        //basic info
        pmethod.setCounts(0);
        pmethod.setPrev(clonedPrev);
        pmethod.setStatus(STATUS_INACTIVE);
        pmethod.setType(getType());
        pmethod.setName(getName());
        pmethod.setDescription(getDescription());
        pmethod.setDefinition(getDefinition());
        
        Activity act = getNext();
        if (act!=null) {
            Activity newAct = act.clone(pmethod, clonedStop, stop);
            pmethod.setNext(newAct);
        }
        
        return pmethod;
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
    
    
}//class PMethodActivity
