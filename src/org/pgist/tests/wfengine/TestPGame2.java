package org.pgist.tests.wfengine;

import java.util.Map;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.WorkflowTask;


/**
 * 
 * @author kenny
 *
 */
public class TestPGame2 implements WorkflowTask {
    
    
    public boolean execute(Activity activity, RunningContext context, Map<String, String> properties) {
        System.out.println("I am in pgame2");
        return true;
    }//execute()
    
    
}//class TestPGame2
