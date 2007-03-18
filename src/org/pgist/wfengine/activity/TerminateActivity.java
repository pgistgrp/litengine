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
 * @hibernate.joined-subclass name="TerminateActivity" table="litwf_activity_terminate"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.TerminateActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class TerminateActivity extends Activity implements SingleIn {
    
    
    protected Activity prev = null;
    
    
    public TerminateActivity() {
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
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        return true;
    }//doExecute()
    
    
    public void saveState(Session session) {
        session.save(this);
    }//saveState()


}//class TerminateActivity
