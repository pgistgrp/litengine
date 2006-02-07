package org.pgist.wfengine.activity;

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
 * @hibernate.joined-subclass name="ReturnActivity" table="litwf_activity_return"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.ReturnActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class ReturnActivity extends Activity implements SingleIn, SingleOut {
    
    
    private static final long serialVersionUID = -1550544562044625071L;
    
    protected GroupActivity group;
    
    protected Activity prev;
    
    protected Activity next;
    
    
    /**
     * @return
     * @hibernate.many-to-one column="group_id" class="org.pgist.wfengine.activity.GroupActivity" lazy="true" cascade="all"
     */
    public GroupActivity getGroup() {
        return group;
    }


    public void setGroup(GroupActivity group) {
        this.group = group;
    }


    /**
     * @return
     * @hibernate.many-to-one column="prev_id" class="org.pgist.wfengine.Activity" lazy="true" cascade="all"
     */
    public Activity getPrev() {
        return prev;
    }


    public void setPrev(Activity prev) {
        this.prev = prev;
    }


    /**
     * @return
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" lazy="true" cascade="all"
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
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        getGroup().setExpression(1);
        return true;
    }//doExecute()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
    }//saveState()


}//class ReturnActivity
