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
import org.pgist.wfengine.Workflow;


/**
 * While Activity class for While/Loop.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="WhileActivity" table="litwf_activity_while"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.WhileActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class WhileActivity extends Activity implements SingleIn, SingleOut {
    
    
    private static final long serialVersionUID = 5888916404788773433L;

    protected int loopCount = 0;

    protected int expression = 0;
    
    protected LoopActivity loop = new LoopActivity(this);
    
    protected Activity prev;
    
    protected Activity next;
    
    protected transient LoopActivity embryoLoop;
    
    
    public WhileActivity() {
        type = TYPE_WHILE;
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
     * @hibernate.many-to-one column="loop_id" class="org.pgist.wfengine.activity.LoopActivity" cascade="all"
     */
    public LoopActivity getLoop() {
        return loop;
    }
    
    
    public void setLoop(LoopActivity loop) {
        this.loop = loop;
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
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public WhileActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        WhileActivity newWhile = new WhileActivity();
        
        //basic info
        newWhile.setCounts(0);
        newWhile.setPrev(clonedPrev);
        
        //loop body
        clonedStop.push(newWhile.getLoop());
        stop.push(getLoop());
        Activity act = getNext();
        if (act!=null) {
            Activity newAct = act.clone(newWhile, clonedStop, stop);
            newWhile.setNext(newAct);
        }
        
        return newWhile;
    }//clone()
    
    
    public Activity getEnd() {
        return getLoop().getEnd();
    }//getEnd()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected void doActivate(Workflow workflow) {
    }//doActivate()
    
    
    protected boolean doExecute(RunningContext context) throws Exception {
        if (getExpression()==0) {//repeat
        	//re-enable all activities between repeat and until
        	getNext().reEnable(context, this);
        	context.addRunningActivity(getNext());
        } else {
        	context.addRunningActivity(getLoop().getNext());
        }
        
        return true;
    }//doActivate()
    
    
    protected void doDeActivate(Workflow workflow) {
        loopCount++;
    }//doDeActivate()
    
    
    public void proceed() throws Exception {
        setExpression(1);
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        setExpression(decision);
    }//proceed()


    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
    public void setFuture(List futures, Set embedding) {
        getNext().setFuture(futures, embedding);
        getLoop().getNext().setFuture(futures, embedding);
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


}//class WhileActivity
