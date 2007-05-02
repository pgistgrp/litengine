package org.pgist.wfengine;

import org.pgist.wfengine.web.WorkflowListener;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


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
        
        System.out.printf("(wfid, ctxid, actid) = (%d, %d, %d)\n", workflowId, contextId, activityId);
        
        try {
            WorkflowEngine engine = WorkflowListener.getEngine();
            engine.executeWorkflow(workflowId, contextId, activityId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JobExecutionException(e);
        }
    }//execute()
    
    
}//class WorkflowTimer
