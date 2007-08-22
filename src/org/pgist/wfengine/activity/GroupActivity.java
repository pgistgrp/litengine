package org.pgist.wfengine.activity;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;


/**
 * <p>Class GroupActivity is a special activity which denotes a group of activities.
 * Those activities compose a piece of workflow instance, which has ONE head activity and ONE
 * tail activity. The head activity must be a backtracable activity and the tail activity must
 * be a pushdownable activity.
 * 
 * <p>It's possible that head activity is the same as the tail activity, that is, the group contains
 * only one activity.
 * 
 * <p>GroupActivity with a null definition is template. Before a GroupActivity running,
 * workflow engine will duplicate the definition.
 * 
 * @hibernate.joined-subclass name="GroupActivity" table="litwf_activity_group"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.GroupActivity"
 * @hibernate.joined-subclass-key column="id"
 * 
 * @author kenny
 */
public abstract class GroupActivity extends Activity implements SingleIn, SingleOut {
    
    
    protected String name;
    
    protected String description;
    
    protected String access = "all";
    
    protected Activity headActivity;
    
    protected Activity tailActivity;
    
    protected Activity prev;
    
    protected Activity next;
    
    protected RunningContext context = new RunningContext();
    
    
    public GroupActivity() {
        context.setGroup(this);
    }
    
    
    /**
     * @return
     * @hibernate.property
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
     * 
     * @hibernate.property column="access" not-null="true"
     */
    public String getAccess() {
        return access;
    }


    public void setAccess(String access) {
        this.access = access;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.many-to-one column="head_id" lazy="true" cascade="all"
     */
    public Activity getHeadActivity() {
        return headActivity;
    }
    
    
    public void setHeadActivity(Activity headActivity) {
        this.headActivity = headActivity;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.many-to-one column="tail_id" lazy="true" cascade="all"
     */
    public Activity getTailActivity() {
        return tailActivity;
    }
    
    
    public void setTailActivity(Activity tailActivity) {
        this.tailActivity = tailActivity;
    }


    /**
     * @return
     * @hibernate.many-to-one column="prev_id" lazy="true" cascade="all"
     */
    public Activity getPrev() {
        return prev;
    }


    public void setPrev(Activity prev) {
        this.prev = prev;
    }


    /**
     * @return
     * @hibernate.many-to-one column="next_id" lazy="true" cascade="all"
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
     * @hibernate.many-to-one column="context_id" lazy="true" cascade="all"
     */
    public RunningContext getContext() {
        return context;
    }


    public void setContext(RunningContext context) {
        this.context = context;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public Activity getEnd() {
        return (getNext()==null) ? this : getNext().getEnd(); 
    }//getEnd()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
    public void setFuture(List futures, Set embedding) {
        futures.add(this);
        getNext().setFuture(futures, embedding);
    }//setFuture()
    
    
    abstract public void finish(RunningContext context);
    
    
    @Override
    public Activity getNextStep(NextStepInfo nsi) {
        return getHeadActivity().getNextStep(nsi);
    }//getNextStep()


    @Override
    public void setSerial(SortedSet set) {
        getHeadActivity().setSerial(set);
    }//setSerial()


}//class GroupActivity
