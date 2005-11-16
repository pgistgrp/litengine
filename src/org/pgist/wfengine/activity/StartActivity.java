package org.pgist.wfengine.activity;

import java.util.List;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="StartActivity" table="litwf_activity_start"
 * @hibernate.joined-subclass-key column="id"
 */
public class StartActivity extends Activity {
    
    
    protected Activity next = null;
    
    
    public StartActivity() {
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" casecad="all"
     */
    public Activity getNext() {
        return next;
    }


    public void setNext(Activity next) {
        this.next = next;
    }


    public boolean activate(WorkflowEnvironment env) {
        int result = UNDEFINED;
        
        if (performerClass!=null && !"".equals(performerClass)) {
            result = doPerform(env);
        }
        
        Stack stack = (Stack) env.getExecuteStack();
        List waitingList = (List) env.getWaitingList();
        
        if (automatic || result==1) {
            if (next!=null) {
                stack.push(next);
            }
        } else {
            waitingList.add(this);
        }
        
        return (result==1);
    }//activate()
    
    
    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class StartActivity
