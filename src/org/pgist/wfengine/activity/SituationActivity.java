package org.pgist.wfengine.activity;

import java.util.Date;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.Workflow;


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
    
    
    private static final long serialVersionUID = 3121004311692237796L;
    
    
    private SituationActivity definition;
    
    private Workflow workflow;
    
    
    public SituationActivity() {
        type = TYPE_SITUATION;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.many-to-one column="definition_id" lazy="true" cascade="all"
     */
    public SituationActivity getDefinition() {
        return definition;
    }


    public void setDefinition(SituationActivity definition) {
        this.definition = definition;
    }


    /**
     * @return
     * 
     * @hibernate.many-to-one column="workflow_id" lazy="true" cascade="all"
     */
    public Workflow getWorkflow() {
        return workflow;
    }


    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public SituationActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        SituationActivity situation = new SituationActivity();
        
        //basic info
        situation.setCounts(0);
        situation.setPrev(clonedPrev);
        situation.setName(getName());
        situation.setDescription(getDescription());
        situation.getContext().getDeclaration().duplicate(getContext().getDeclaration());
        situation.getContext().getEnvironment().duplicate(getContext().getEnvironment());
        
        //create a return activity
        ReturnActivity returnActivity = new ReturnActivity();
        returnActivity.setCounts(0);
        returnActivity.setGroup(situation);
        
        //duplicate myself
        situation.setHeadActivity(getHeadActivity().clone(null, new Stack<Activity>(), new Stack<Activity>()));
        
        SingleOut sout = (SingleOut) situation.getHeadActivity().getEnd();
        sout.setNext(returnActivity);
        returnActivity.setPrev((Activity) sout);
        
        situation.setTailActivity(returnActivity);
        
        return situation;
    }//clone()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    synchronized protected boolean doExecute(RunningContext context) throws Exception {
        //put head in the context
        getContext().addRunningActivity(getHeadActivity());
        
        return false;
    }//doExecute()
    
    
    protected void doDeActivate(RunningContext context) {
        getWorkflow().setStatus(Workflow.STATUS_FINISHED);
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
    public void finish(RunningContext context) {
        getWorkflow().setStatus(Workflow.STATUS_FINISHED);
        getWorkflow().setEndTime(new Date());
    }//finish()
    
    
}//class SituationActivity
