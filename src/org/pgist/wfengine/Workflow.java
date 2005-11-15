package org.pgist.wfengine;

import java.util.List;
import java.util.Stack;


/**
 * The Workflow is the working instance for a workflow.
 * 
 * @author kenny
 *
 */
public class Workflow {
    
    
    //The definition activity of a workflow instance
    private Activity definition = null;
    
    //A workflow can't be definitioned more than once
    private boolean born = false;
    
    //
    private FlowEnvironment env = new FlowEnvironment();
    
    
    public Workflow() {
    }
    
    
    public Workflow(Activity definition) {
        this.definition = definition;
        this.born = false;
    }
    
    
    public Activity setdefinition(Activity definition) {
        this.definition = definition;
        return this.definition;
    }//setdefinition()
    
    
    public boolean isFinished() {
        Stack stack = env.getExecuteStack();
        List waitingList = env.getWaitingList();
        return (stack.empty() && waitingList.size()==0);
    }
    
    
    public void execute() {
        if (definition==null) return;
        
        Stack stack = env.getExecuteStack();
        List waitingList = env.getWaitingList();
        
        if (born==false) {
            born = true;
            stack.push(definition);
        }
        
        while (!stack.empty()) {
            Activity activity = (Activity) stack.pop();
            activity.activate(env);
        }//while
        
        if (waitingList.size()>0) {
            stack.addAll(waitingList);
            waitingList.clear();
        }
    }//execute()
    
    
}//class Workflow
