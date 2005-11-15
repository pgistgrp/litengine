package org.pgist.wfengine.activity;

import java.util.List;
import java.util.Stack;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.FlowEnvironment;


/**
 * The SequenceActivity implement the sequence flow activity.
 * 
 * @author kenny
 *
 */
public class SequenceActivity extends Activity {
    
    
    private Activity next;
    
    
    public SequenceActivity(boolean automatic) {
        this.automatic = automatic;
    }
    
    
    public Activity getNext() {
        return next;
    }
    
    
    public Activity setNext(Activity next) {
        this.next = next;
        return this.next;
    }
    
    
    public void proceed() {
        active = false;
    }//proceed()


    public List getLoopback() {
        return null;
    }
    
    
    public boolean activate(FlowEnvironment env) {
        active = true;
        
        Stack stack = (Stack) env.getExecuteStack();
        List waitingList = (List) env.getWaitingList();
        
        if (automatic) {
            
            if (action!=null) {
                action.execute(env);
            }
            
            if (next!=null) stack.push(next);
            
            return true;
        } else {
            
            if (action!=null && action.execute(env)) {
                proceed();
                if (next!=null) stack.push(next);
            } else {
                waitingList.add(this);
            }
            
        }
        
        return false;
    }//activate()
    
    
}//class SequenceActivity
