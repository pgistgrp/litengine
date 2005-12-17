package org.pgist.servlets;

import org.pgist.wfengine.Task;
import org.pgist.wfengine.Workflow;


/**
 * 
 * @author kenny
 *
 */
public class WorkflowContext implements IWorkflowContext {

    
    private Long workflowId;
    
    private Workflow workflow;
    
    private Long taskId;
    
    private Task task;
    
    
    public Long getWorkflowId() {
        return workflowId;
    }//getWorkflowId()


    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }//setWorkflowId()


    public Workflow getWorkflow() {
        return workflow;
    }//getWorkflow()


    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }//setWorkflow()
    
    
    public Long getTaskId() {
        return taskId;
    }//getTaskId()


    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }//setTaskId()


    public Task getTask() {
        return task;
    }//getTask()


    public void setTask(Task task) {
        this.task = task;
    }//setTask()


}//class WorkflowContext
