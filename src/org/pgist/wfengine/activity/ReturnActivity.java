package org.pgist.wfengine.activity;

import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="ReturnActivity" table="litwf_activity_return"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.ReturnActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class ReturnActivity extends Activity implements SingleIn {
    
    
    private static final long serialVersionUID = -7064274249683190904L;
    

    private Activity prev;
    
    protected GroupActivity group;
    
    
    public ReturnActivity() {
        type = TYPE_RETURN;
    }
    
    
    /**
     * @return
     * 
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
     * 
     * @hibernate.many-to-one column="group_id" lazy="true" cascade="all"
     */
    public GroupActivity getGroup() {
        return group;
    }


    public void setGroup(GroupActivity group) {
        this.group = group;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public Activity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        return null;
    }//clone()


    public Activity getEnd() {
        return this;
    }//getEnd()


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context) throws Exception {
        context.getParent().getStack().add(getGroup());
        
        return true;
    }//doExecute()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
    }//saveState()


}//class ReturnActivity
