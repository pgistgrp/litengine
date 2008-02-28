package org.pgist.wfengine.activity;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="RepeatActivity" table="litwf_activity_repeat"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.RepeatActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepeatActivity extends Activity implements SingleIn, SingleOut {
    
    
    private static final long serialVersionUID = -6112252588666664653L;
    
    
    protected int loopCount = 0;
    
    protected int expression;
    
    protected UntilActivity until = new UntilActivity(this);
    
    protected Activity prev;
    
    protected Activity next;
    
    protected transient UntilActivity embryoUntil;
    
    
    public RepeatActivity() {
        type = TYPE_REPEAT;
    }
    
    
    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public int getLoopCount() {
        return loopCount;
    }


    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public int getExpression() {
        return expression;
    }


    public void setExpression(int expression) {
        this.expression = expression;
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
     * @hibernate.many-to-one column="until_id" class="org.pgist.wfengine.activity.UntilActivity" cascade="all"
     */
    public UntilActivity getUntil() {
        return until;
    }
    
    
    public void setUntil(UntilActivity until) {
        this.until = until;
    }
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public RepeatActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        RepeatActivity newRepeat = new RepeatActivity();
        
        //basic info
        newRepeat.setCounts(0);
        newRepeat.setPrev(clonedPrev);
        
        //repeat body
        clonedStop.push(newRepeat.getUntil());
        stop.push(getUntil());
        Activity act = getNext();
        if (act!=null) {
            Activity newAct = act.clone(newRepeat, clonedStop, stop);
            newRepeat.setNext(newAct);
        }
        
        return newRepeat;
    }//clone()
    
    
    public Activity getEnd() {
        return getUntil().getEnd();
    }//getEnd()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected void doActivate(RunningContext context) {
        loopCount = 0;
    }//doActivate()
    
    
    protected boolean doExecute(RunningContext context) throws Exception {
        context.addRunningActivity(getNext());
        
        context.getEnvironment().getStrValues().put(getUntil().getExitCondition(), null);
        
        return true;
    }//doExecute()
    
    
    protected void doDeActivate(RunningContext context) {
        loopCount++;
    }//doDeActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
    public void setFuture(List futures, Set embedding) {
        getNext().setFuture(futures, embedding);
        getUntil().getNext().setFuture(futures, embedding);
    }//setFuture()
    
    
    @Override
    public void getAgenda(List activities) {
        getNext().getAgenda(activities);
    }//getAgenda
    
    
    @Override
    public Activity getNextStep(NextStepInfo nsi) {
        return getNext().getNextStep(nsi);
    }//getNextStep()
    
    
    @Override
    public void setSerial(SortedSet set) {
        getNext().setSerial(set);
    }//setSerial()


	@Override
	public void reEnable(RunningContext context, Activity start) {
		getNext().reEnable(context, start);
	}


}//class RepeatActivity
