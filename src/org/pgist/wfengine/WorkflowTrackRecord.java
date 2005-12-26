package org.pgist.wfengine;

import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_tracker_record"
 */
public class WorkflowTrackRecord {

    
    private Long id;
    
    private WorkflowTracker workflowTracker;
    
    private Set parents = new HashSet();
    
    private Set children = new HashSet();
    
    
    public WorkflowTrackRecord() {
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


    public WorkflowTracker getWorkflowTracker() {
        return workflowTracker;
    }


    /**
     * @return
     * @hibernate.many-to-one column="tracker_id" class="org.pgist.wfengine.WorkflowTracker" cascade="all"
     */
    public void setWorkflowTracker(WorkflowTracker workflowTracker) {
        this.workflowTracker = workflowTracker;
    }


    /**
     * @return
     * @hibernate.set lazy="false" table="litwf_tracker_record" cascade="all" order-by="id"
     * @hibernate.collection-key column="parent_id"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.WorkflowTrackRecord"
     */
    public Set getChildren() {
        return children;
    }


    public void setChildren(Set children) {
        this.children = children;
    }

    
    /**
     * @return
     * @hibernate.set lazy="false" table="litwf_tracker_record" cascade="all" order-by="id"
     * @hibernate.collection-key column="child_id"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.WorkflowTrackRecord"
     */
    public Set getParents() {
        return parents;
    }


    public void setParents(Set parents) {
        this.parents = parents;
    }
    
    
}//class WorkflowTrackRecord
