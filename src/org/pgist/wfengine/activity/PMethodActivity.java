package org.pgist.wfengine.activity;

import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleOut;


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
    
    
    private static final long serialVersionUID = 4426804473952459196L;
    
    
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
        
        //duplicate from the definition
        setHeadActivity(getDefinition().getHeadActivity().clone(null, new Stack<Activity>(), new Stack<Activity>()));
        
        SingleOut sout = (SingleOut) getHeadActivity().getEnd();
        sout.setNext(returnActivity);
        returnActivity.setPrev((Activity) sout);
        
        setTailActivity(returnActivity);
    }//doActivate()
    
    
    synchronized protected boolean doExecute(RunningContext context) throws Exception {
        //put head in the context
        getContext().getStack().push(getHeadActivity());
        
        getContext().setParent(context);
        
        getContext().execute();
        
        context.getPendingActivities().add(this);
        
        return false;
    }//doExecute()
    
    
    protected void doDeActivate(RunningContext context) {
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
    public void finish(RunningContext context) {
        
    }//finish()
    
    
}//class PMethodActivity
