package org.pgist.wfengine.test;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WorkflowEnvironment;
import org.pgist.wfengine.IPerformer;


public class DefaultPerformer implements IPerformer {
    
    
    public DefaultPerformer() {
    }
    
    
    public int perform(Activity activity, WorkflowEnvironment env) {
        System.out.println(" I am "+activity.getClass().getName());
        return 1;
    }
    
    
}//class DefaultPerformer
