package org.pgist.wfengine.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Declaration;
import org.pgist.wfengine.Environment;
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
        meeting.setName(getName());
        meeting.setDescription(getDescription());
        meeting.setAccess(getAccess());
        meeting.setDefinition(getDefinition());
        meeting.getContext().getDeclaration().duplicate(getContext().getDeclaration());
        meeting.getContext().getEnvironment().duplicate(getContext().getEnvironment());
        
        Activity act = getNext();
        if (act!=null) {
            Activity newAct = act.clone(meeting, clonedStop, stop);
            meeting.setNext(newAct);
        }
        
        //create a return activity
        ReturnActivity returnActivity = new ReturnActivity();
        returnActivity.setCounts(0);
        returnActivity.setGroup(meeting);
        
        //duplicate from the definition
        meeting.setHeadActivity(getDefinition().getHeadActivity().clone(null, new Stack<Activity>(), new Stack<Activity>()));
        
        SingleOut sout = (SingleOut) meeting.getHeadActivity().getEnd();
        sout.setNext(returnActivity);
        returnActivity.setPrev((Activity) sout);
        
        meeting.setTailActivity(returnActivity);
        
        return meeting;
    }//clone()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected void doActivate(RunningContext context) {
        //inherite environment from the parent context
        Environment initEnv = getContext().getInitEnvironment();
        Environment myEnv = getContext().getEnvironment();
        
        myEnv.getIntValues().putAll(initEnv.getIntValues());
        myEnv.getDblValues().putAll(initEnv.getDblValues());
        myEnv.getStrValues().putAll(initEnv.getStrValues());
        myEnv.getDateValues().putAll(initEnv.getDateValues());
    }//doActivate()
    
    
    synchronized protected boolean doExecute(RunningContext context) throws Exception {
        context.getPendingActivities().add(this);
        
        //put head in the context
        getContext().addRunningActivity(getHeadActivity());
        
        getContext().setParent(context);
        
        getContext().execute();
        
        return false;
    }//doExecute()
    
    
    protected void doDeActivate(RunningContext context) {
        /*
         * Record the history
         */
        context.getHistories().add(this);
        
        //output the declared environment
        Declaration myDecl = getContext().getDeclaration();
        Environment myEnv = getContext().getEnvironment();
        Environment upEnv = context.getEnvironment();
        upEnv.merge(myEnv, myDecl);
        
        //clear my environment
        //getContext().getEnvironment().clear();
        
        getContext().getParent().getPendingActivities().remove(this);
        getContext().getParent().addRunningActivity(getNext());
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
    public void finish(RunningContext context) {
    }//finish()
    
    
    @Override
    public void getAgenda(List activities) {
        List<Activity> list = new ArrayList<Activity>();
        getHeadActivity().getAgenda(list);
        activities.add(list);
        
        if (getNext()!=null) getNext().getAgenda(activities);
    }//getAgenda
    
    
}//class MeetingActivity
