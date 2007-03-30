package org.pgist.tests.other;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * 
 * @author kenny
 *
 */
public class MockJob implements Job {
    
    
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("@ MockJob.execute()");
        
        //throw new JobExecutionException("throw @ MockJob.execute()");
    }//execute()
    
    
}//class MockJob
