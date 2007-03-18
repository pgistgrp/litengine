package org.pgist.wfengine;

import java.util.List;


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
    
    private List records;
    
    
    public WorkflowTracker() {
    }


    /**
     * @return
     * 
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
     * @hibernate.one-to-one name="workflow" cascade="all" class="org.pgist.wfengine.Workflow"
     */
    public Workflow getWorkflow() {
        return workflow;
    }


    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }


    /**
     * @return
     * 
     * @hibernate.list table="litwf_task" lazy="true" cascade="all"
     * @hibernate.collection-key column="tracker_id"
     * @hibernate.collection-index column="task_order"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     * 
     */
    public List getRecords() {
        return records;
    }


    public void setRecords(List records) {
        this.records = records;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
}//class WorkflowTracker
