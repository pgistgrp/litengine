package org.pgist.wfengine.activity;

import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="ReturnActivity" table="litwf_activity_return"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.ReturnActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class ReturnActivity extends Activity {
    
    
    protected GroupActivity group;
    
    
    public ReturnActivity() {
        type = TYPE_RETURN;
    }
    
    
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
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        getGroup().setExpression(1);
        return true;
    }//doExecute()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
    }//saveState()


}//class ReturnActivity
