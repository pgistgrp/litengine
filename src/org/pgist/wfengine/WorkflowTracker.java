package org.pgist.wfengine;


/**
 * The trancker of a workflow instance. It records and track the trajactory of workflow running.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_tracker"
 */
public class WorkflowTracker {

    
    private Long id;
    
    private Workflow workflow;
    
    
    public WorkflowTracker() {
    }


    /**
     * @return
     * @hibernate.id generator-class="foreign"
     * @hibernate.generator-param name="property" value="workflow"
     */
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    /**
     * @return
     * @hibernate.one-to-one cascade="all" class="org.pgist.wfengine.Workflow"
     */
    public Workflow getWorkflow() {
        return workflow;
    }


    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }
    
    
}//class WorkflowTracker
