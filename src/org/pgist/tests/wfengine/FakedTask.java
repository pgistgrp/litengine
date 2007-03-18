package org.pgist.tests.wfengine;

import java.util.Map;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.WorkflowTask;


/**
 * Faked Workflow Task for testing.
 * 
 * @author kenny
 */
public class FakedTask implements WorkflowTask {
    
    
    public static final String IN_SUITE_ID = "proj_suite_id";
    
    
    public boolean execute(Activity activity, RunningContext context, Map<String, String> properties) {
        Integer suiteId = context.getIntValue(IN_SUITE_ID);
        
        System.out.print("    Running @ FakedTask.execute()");
        System.out.print("        suiteId: "+suiteId);
        
        return false;
    }//execute()
    
    
}//class FakedTask
