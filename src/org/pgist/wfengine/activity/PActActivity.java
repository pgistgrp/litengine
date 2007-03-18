package org.pgist.wfengine.activity;

import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;


/**
 * The SequenceActivity implement the sequence flow activity.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="PActActivity" table="litwf_activity_pact"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.PActActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class PActActivity extends Activity implements SingleIn, SingleOut {
    
    
    protected Long refId = null;
    
    protected String name = null;
    
    protected String description = null;
    
    protected String action = null;
    
    protected Activity prev;
    
    protected Activity next;
    
    protected Activity depending;
    
    
    public PActActivity() {
    }
    
    
    /**
     * @return
     * @hibernate.property
     */
    public Long getRefId() {
        return refId;
    }


    public void setRefId(Long refId) {
        this.refId = refId;
    }


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return
     * @hibernate.property
     */
    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return
     * @hibernate.property
     */
    public String getAction() {
        return action;
    }


    public void setAction(String action) {
        this.action = action;
    }


    /**
     * @return
     * @hibernate.many-to-one column="prev_id" class="org.pgist.wfengine.Activity" cascade="all" lazy="true"
     */
    public Activity getPrev() {
        return prev;
    }
    
    
    public void setPrev(Activity prev) {
        this.prev = prev;
    }


    /**
     * @return
     *
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" cascade="all" lazy="true"
     */
    public Activity getNext() {
        return next;
    }
    
    
    public void setNext(Activity next) {
        this.next = next;
    }

    
    /**
     * @return
     *
     * @hibernate.many-to-one column="depend_id" class="org.pgist.wfengine.Activity" cascade="all" lazy="true"
     */
    public Activity getDepending() {
        return depending;
    }


    public void setDepending(Activity depending) {
        this.depending = depending;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
//        if (task==null) {
//            setExpression(1);
//        } else if (task.getType()==Task.TASK_AUTOMATIC) {
//            task.execute(context);
//        }
        
        if (getExpression()>0) {//task is finished
            next.activate(context);
            stack.push(next);
            return true;
        } else {
            return false;
        }
    }//doExecute()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


    public boolean isAutomatic() {
        return false;
    }//isAutomatic()


}//class PActActivity
