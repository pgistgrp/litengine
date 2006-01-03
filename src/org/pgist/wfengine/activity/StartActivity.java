package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.PushDownable;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="StartActivity" table="litwf_activity_start"
 * @hibernate.joined-subclass-key column="id"
 */
public class StartActivity extends Activity implements PushDownable {
    
    
    protected Activity next;

    
    public StartActivity() {
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

    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    /**
     * Clone of start activity need not be implemented
     */
    public Activity clone(Activity prev) {
        return null;
    }
    
    
    public Activity probe() {
        if (next==null) return this;
        return next.probe();
    }


    protected Activity[] doExecute(Workflow workflow) throws Exception {
        if (task==null) {
            expression = 1;
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            task.execute(workflow);
        }
        
        if (expression>0) {//task is finished
            return new Activity[] { next };
        } else {
            return new Activity[] { this };
        }
    }//doExecute()
    
    
    public void proceed() throws Exception {
        expression = 1;
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        //discard the decision
        expression = 1;
    }//proceed()


    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class StartActivity
