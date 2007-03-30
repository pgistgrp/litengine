package org.pgist.tests.other;

import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;


/**
 * 
 * @author kenny
 *
 */
public class MockService {
    
    
    Scheduler scheduler;
    
    
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    
    /*
     * ------------------------------------------------------------------------
     */


    public void setJob() throws Exception {
        System.out.println(scheduler);
        System.out.println("@ MockService.test()");
        
        JobDetail jobDetail = new JobDetail("myJob1", null, MockJob.class);
        
        SimpleTrigger trigger = new SimpleTrigger("myTrigger1", null,
                new Date(System.currentTimeMillis() + 3000L), null, 0, 0L);
        
        scheduler.scheduleJob(jobDetail, trigger);
        
        jobDetail = new JobDetail("myJob2", null, MockJob.class);
        
        trigger = new SimpleTrigger("myTrigger2", null,
                new Date(System.currentTimeMillis() + 3000L), null, 0, 0L);
        
        scheduler.scheduleJob(jobDetail, trigger);
        
        jobDetail = new JobDetail("myJob3", null, MockJob.class);
        
        trigger = new SimpleTrigger("myTrigger3", null,
                new Date(System.currentTimeMillis() + 3000L), null, 0, 0L);
        
        scheduler.scheduleJob(jobDetail, trigger);
        
        throw new Exception("force exception");
    }//setJob()
    
    
}//class MockService
