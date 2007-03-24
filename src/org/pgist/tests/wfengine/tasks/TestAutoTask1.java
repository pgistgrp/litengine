package org.pgist.tests.wfengine.tasks;

import org.pgist.wfengine.EnvironmentInOuts;
import org.pgist.wfengine.WorkflowTask;


/**
 * 
 * @author kenny
 *
 */
public class TestAutoTask1 implements WorkflowTask {
    
    
    public static final String OUT_SUITE_ID = "suite_id";
    
    
    public void execute(EnvironmentInOuts inouts) throws Exception {
        System.out.println("@ TestAutoTask1.execute()");
        System.out.println("    out (suite_id) : 1000");
        inouts.setIntValue(OUT_SUITE_ID, 1000);
    }//execute()
    
    
}//class TestAutoTask1
