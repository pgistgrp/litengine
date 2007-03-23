package org.pgist.wfengine.activity;

import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleOut;


/**
 * <p>Class MeetingActivity is a special GroupActivity for meeting.
 * 
 * @hibernate.joined-subclass name="GroupActivity" table="litwf_activity_group_meeting"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.MeetingActivity"
 * @hibernate.joined-subclass-key column="id"
 * 
 * @author kenny
 */
public class MeetingActivity extends GroupActivity {
    
    
    private static final long serialVersionUID = -8792782439457666295L;
    

    private SituationActivity situation;
    
    private MeetingActivity definition;
    
    
    public MeetingActivity() {
        type = TYPE_PMETHOD;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.many-to-one column="definition_id" lazy="true" cascade="all"
     */
    public MeetingActivity getDefinition() {
        return definition;
    }


    public void setDefinition(MeetingActivity definition) {
        this.definition = definition;
    }


    /**
     * @return
     * 
     * @hibernate.many-to-one column="situation_id" lazy="true" cascade="all"
     */
    public SituationActivity getSituation() {
        return situation;
    }


    public void setSituation(SituationActivity situation) {
        this.situation = situation;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public MeetingActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        MeetingActivity meeting = new MeetingActivity();
        
        //basic info
        meeting.setCounts(0);
        meeting.setPrev(clonedPrev);
        meeting.setStatus(STATUS_INACTIVE);
        meeting.setType(getType());
        meeting.setName(getName());
        meeting.setDescription(getDescription());
        meeting.setDefinition(getDefinition());
        
        Activity act = getNext();
        if (act!=null) {
            Activity newAct = act.clone(meeting, clonedStop, stop);
            meeting.setNext(newAct);
        }
        
        return meeting;
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
    
    
}//class MeetingActivity
