package org.pgist.wfengine.activity;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


/**
 * The SequenceActivity implement the sequence flow activity.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="PActActivity" table="litwf_activity_pact"
 * @hibernate.joined-subclass-key column="id"
 */
public class PActActivity extends Activity implements SingleIn, SingleOut {
    
    
    private static final long serialVersionUID = 3222935387069104483L;

    protected Long refId = null;
    
    protected String name = null;
    
    protected String description = null;
    
    protected Activity prev;
    
    protected Activity next;
    
    
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
     * @hibernate.property not-null="true"
     */
    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
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
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" cascade="all"

     * @return
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
    
    
    protected Activity[] doExecute(Workflow workflow) throws Exception {
        if (task==null) {
            setExpression(1);
        } else if (task.getType()==Task.TASK_AUTOMATIC) {
            task.execute(workflow);
        }
        
        if (getExpression()>0) {//task is finished
            return new Activity[] { next };
        } else {
            return new Activity[] { this };
        }
    }//doExecute()
    
    
    public void proceed() throws Exception {
        setExpression(1);
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        //discard the decision
        setExpression(1);
    }//proceed()


    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()


}//class PActActivity
