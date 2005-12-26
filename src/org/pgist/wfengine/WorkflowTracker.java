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
    
    private WorkflowTrackRecord root;
    
    
    public WorkflowTracker() {
    }


    /**
     * @return
     * 
     * @hibernate.id generator-class="native"
     */
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    /**
     * @return
     * @hibernate.many-to-one column="workflow_id" cascade="all" class="org.pgist.wfengine.Workflow"
     */
    public Workflow getWorkflow() {
        return workflow;
    }


    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }


    /**
     * @return
     * @hibernate.many-to-one column="root_id" class="org.pgist.wfengine.WorkflowTrackRecord" cascade="all"
     */
    public WorkflowTrackRecord getRoot() {
        return root;
    }


    public void setRoot(WorkflowTrackRecord root) {
        this.root = root;
    }
    
    
}//class WorkflowTracker
