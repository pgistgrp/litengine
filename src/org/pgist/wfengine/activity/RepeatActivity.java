package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.AutoTask;
import org.pgist.wfengine.BackTracable;
import org.pgist.wfengine.ManualTask;
import org.pgist.wfengine.PushDownable;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="RepeatActivity" table="litwf_activity_repeat"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepeatActivity extends Activity implements BackTracable, PushDownable {
    
    
    protected int loopCount = 0;
    
    protected int expression;

    protected UntilActivity until;
    
    protected Activity prev;
    
    protected Activity next;
    
    protected transient UntilActivity embryoUntil;

    
    public RepeatActivity() {
    }
    
    
    public Activity clone(Activity prev) {
        try {
            RepeatActivity embryo = new RepeatActivity();
            embryo.setCaption(this.caption);
            embryo.setUrl(this.url);
            embryo.setPrev(prev);
            if (task!=null) embryo.setTask( (Task) task.clone() );
            
            //set the status
            if (until!=null) {
                embryoUntil = new UntilActivity();
                embryoUntil.setCaption(until.getCaption());
                embryoUntil.setUrl(until.getUrl());
                embryo.setUntil(embryoUntil);
                embryoUntil.setRepeat(embryo);
            }
            
            if (next!=null) {
                Activity embryoNext = next.clone(embryo);
                embryo.setNext(embryoNext);
            }
            
            //reset the status
            embryoUntil = null;
            
            return embryo;
        } catch(Exception e) {
            return null;
        }
    }

    
    public Activity probe() {
        return until.probe();
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
    
    
    protected Activity[] doActivate(Workflow workflow, WorkflowEnvironment env) {
        if (task==null) {
            loopCount++;
            return new Activity[] { next };
        } else if (task instanceof AutoTask) {
            ((AutoTask)task).execute(workflow, env, this);
            loopCount++;
            return new Activity[] { next };
        } else {
            ((ManualTask)task).init(workflow, env, this);
            return new Activity[] { this };
        }
    }//doActivate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class RepeatActivity
