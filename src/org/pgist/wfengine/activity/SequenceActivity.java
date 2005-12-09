package org.pgist.wfengine.activity;

import java.util.List;
import java.util.Stack;

import org.hibernate.Session;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;


/**
 * The SequenceActivity implement the sequence flow activity.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="SequenceActivity" table="litwf_activity_sequence"
 * @hibernate.joined-subclass-key column="id"
 */
public class SequenceActivity extends Activity implements BackTracable, PushDownable {
    
    
    protected Activity prev;
    
    protected Activity next;
    
    
    public SequenceActivity() {
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
     * @hibernate.many-to-one column="next_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getNext() {
        return next;
    }
    
    
    public void setNext(Activity next) {
        this.next = next;
    }

    
    public boolean activate(WorkflowEnvironment env) {
        
        Stack stack = (Stack) env.getExecuteStack();
        List waitingList = (List) env.getWaitingList();
        
        int result = UNDEFINED;
        
        if (automatic) {
            
            //For the automatic sequence activity, discard the return value
            //jump to the next activity
            
            if (performerClass!=null && !"".equals(performerClass)) {
                doPerform(env);
            }
            
            result = 1;
            
        } else {
            
            //For the non-automatic sequence activity, check the return value,
            //if ==0, wait for user response
            //if ==1, jump to the next activity
            
            if (performerClass!=null && !"".equals(performerClass)) {
                result = doPerform(env);;
            }
            
        }
        
        if (result==1 && next!=null) {
            next.reach(this, env);
            stack.push(next);
        } else {
            waitingList.add(this);
        }
        
        return (result==1);
    }//activate()


    public void saveState(Session session) {
        session.save(this);
        if (next!=null) next.saveState(session);
    }//saveState()
    
    
}//class SequenceActivity
