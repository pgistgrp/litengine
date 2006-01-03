package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.BackTracable;
import org.pgist.wfengine.PushDownable;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


/**
 * While Activity class for While/Loop.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="WhileActivity" table="litwf_activity_while"
 * @hibernate.joined-subclass-key column="id"
 */
public class WhileActivity extends Activity implements BackTracable, PushDownable {
    
    
    protected int loopCount = 0;

    protected int expression = 0;
    
    protected LoopActivity loop;
    
    protected Activity prev;
    
    protected Activity next;

    protected transient LoopActivity embryoLoop;

    
    public WhileActivity() {
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
    
    
    public Activity clone(Activity prev) {
        try {
            WhileActivity embryo = new WhileActivity();
            embryo.setCaption(this.caption);
            embryo.setUrl(this.url);
            embryo.setPrev(prev);
            if (task!=null) embryo.setTask( (Task) task.clone(embryo) );
            
            //set the status
            if (loop==null) {
                embryoLoop = new LoopActivity();
                embryoLoop.setCaption(loop.getCaption());
                embryoLoop.setUrl(loop.getUrl());
                embryo.setLoop(embryoLoop);
                embryoLoop.setWhilst(embryo);
            }
            
            if (next!=null) {
                Activity embryoNext = next.clone(embryo);
                embryo.setNext(embryoNext);
            }
            
            //reset the status
            embryoLoop = null;
            
            return embryo;
        } catch(Exception e) {
            return null;
        }
    }
    
    
    public Activity probe() {
        return loop.probe();
    }


    protected void doActivate(Workflow workflow) {
    }//doActivate()
    
    
    protected Activity[] doExecute(Workflow workflow) throws Exception {
        if (task==null) {
            expression = 1;
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            task.execute(workflow);
        }
        
        if (expression==0) {
            return new Activity[] { this };
        } else if (expression>0) {
            return new Activity[] { next };
        } else {
            return new Activity[] { loop.getNext() };
        }
    }//doActivate()
    
    
    protected void doDeActivate(Workflow workflow) {
        loopCount++;
    }//doDeActivate()
    
    
    public void proceed() throws Exception {
        expression = 1;
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        expression = decision;
    }//proceed()


    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class WhileActivity
