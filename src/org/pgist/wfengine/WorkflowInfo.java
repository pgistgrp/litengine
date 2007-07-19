package org.pgist.wfengine;


/**
 * A class to wrap the workflow information.
 *  
 * @author kenny
 */
public class WorkflowInfo {
    
    
    public WorkflowInfo(Workflow workflow, RunningContext context, Activity activity) {
        this.workflow = workflow;
        this.context  = context;
        this.activity = activity;
    }
    
    
    private Workflow workflow;
    
    private RunningContext context;
    
    private Activity activity;
    
    
    public Workflow getWorkflow() {
        return workflow;
    }
    
    
    public RunningContext getContext() {
        return context;
    }


    public Activity getActivity() {
        return activity;
    }


}//class WorkflowInfo
