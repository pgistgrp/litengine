package org.pgist.wfengine;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;


/**
 * A Quartz Job for workflow timer.
 * 
 * @author kenny
 */
public class WorkflowTimer implements Job {
    
    
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("@ WorkflowTimer.execute()");
        
        JobDataMap params = context.getJobDetail().getJobDataMap();
        
        Long workflowId = (Long) params.get("wf_id");
        Long contextId = (Long) params.get("ctx_id");
        Long activityId = (Long) params.get("act_id");
        
        try {
            SchedulerContext skedCtx = context.getScheduler().getContext();
            WorkflowEngine engine = (WorkflowEngine) skedCtx.get("engine");
            engine.executeWorkflow(workflowId, contextId, activityId);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }//execute()
    
    
}//class WorkflowTimer
