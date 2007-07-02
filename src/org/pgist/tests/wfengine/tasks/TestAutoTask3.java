package org.pgist.tests.wfengine.tasks;

import org.pgist.wfengine.EnvironmentInOuts;
import org.pgist.wfengine.WorkflowInfo;
import org.pgist.wfengine.WorkflowTask;


/**
 * 
 * @author kenny
 *
 */
public class TestAutoTask3 implements WorkflowTask {
    
    
    public static final String IN_SUITE_ID = "suite_id";
    
    public static final String IN_ISID = "isid";
    
    public static final String OUT_GOODBYE_STR = "goodbye_str";
    
    
    public void execute(WorkflowInfo info, EnvironmentInOuts inouts) throws Exception {
        System.out.println("@ TestAutoTask3.execute()");
        System.out.println("    in (suite_id) "+inouts.getIntValue(IN_SUITE_ID));
        System.out.println("    in (isid) "+inouts.getIntValue(IN_ISID));
        System.out.println("    out (goodbye_str) : Good Bye, Zhong!");
        inouts.setStrValue(OUT_GOODBYE_STR, "Good Bye, Zhong!");
    }//execute()
    
    
}//class TestAutoTask3
