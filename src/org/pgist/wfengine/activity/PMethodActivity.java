package org.pgist.wfengine.activity;

import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Declaration;
import org.pgist.wfengine.Environment;
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
        pmethod.setName(getName());
        pmethod.setDescription(getDescription());
        pmethod.setDefinition(getDefinition());
        pmethod.getContext().getDeclaration().duplicate(getContext().getDeclaration());
        pmethod.getContext().getEnvironment().duplicate(getContext().getEnvironment());
        
        Activity act = getNext();
        if (act!=null) {
            Activity newAct = act.clone(pmethod, clonedStop, stop);
            pmethod.setNext(newAct);
        }
        
        //create a return activity
        ReturnActivity returnActivity = new ReturnActivity();
        returnActivity.setCounts(0);
        returnActivity.setGroup(pmethod);
        
        //duplicate from the definition
        pmethod.setHeadActivity(getDefinition().getHeadActivity().clone(null, new Stack<Activity>(), new Stack<Activity>()));
        
        SingleOut sout = (SingleOut) pmethod.getHeadActivity().getEnd();
        sout.setNext(returnActivity);
        returnActivity.setPrev((Activity) sout);
        
        pmethod.setTailActivity(returnActivity);
        
        return pmethod;
    }//clone()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected void doActivate(RunningContext context) throws Exception {
        //inherite environment from the parent context
        Environment initEnv = getContext().getInitEnvironment();
        Environment myEnv = getContext().getEnvironment();
        
        myEnv.getIntValues().putAll(initEnv.getIntValues());
        myEnv.getDblValues().putAll(initEnv.getDblValues());
        myEnv.getStrValues().putAll(initEnv.getStrValues());
        myEnv.getDateValues().putAll(initEnv.getDateValues());
        
        context.save(this);
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
        //oupout the declared environment
        Declaration myDecl = getContext().getDeclaration();
        Environment myEnv = getContext().getEnvironment();
        Environment upEnv = context.getEnvironment();
        upEnv.merge(myEnv, myDecl);
        
        //clear my environment
        getContext().getEnvironment().clear();
        
        getContext().getParent().getPendingActivities().remove(this);
        getContext().getParent().getStack().add(getNext());
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
    public void finish(RunningContext context) {
        context.getPendingActivities().remove(this);
    }//finish()
    
    
}//class PMethodActivity
