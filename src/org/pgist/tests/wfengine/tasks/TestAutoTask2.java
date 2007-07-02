package org.pgist.tests.wfengine.tasks;

import org.pgist.wfengine.EnvironmentInOuts;
import org.pgist.wfengine.WorkflowInfo;
import org.pgist.wfengine.WorkflowTask;


/**
 * 
 * @author kenny
 *
 */
public class TestAutoTask2 implements WorkflowTask {
    
    
    public static final String IN_SUITE_ID = "suite_id";
    
    public static final String OUT_ISID = "isid";
    
    
    public void execute(WorkflowInfo info, EnvironmentInOuts inouts) throws Exception {
        System.out.println("@ TestAutoTask2.execute()");
        System.out.println("    in (suite_id) "+inouts.getIntValue(IN_SUITE_ID));
        System.out.println("    out (isid) : 2000");
        inouts.setIntValue(OUT_ISID, 2000);
    }//execute()
    
    
}//class TestAutoTask2
