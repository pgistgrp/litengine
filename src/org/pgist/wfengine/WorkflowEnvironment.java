package org.pgist.wfengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Encapsulate the workflow internal environment
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_environment"
 */
public class WorkflowEnvironment {
    
    
    protected Long id = null;

    private List waitingList = new ArrayList(16);
    
    private Map values = new HashMap();
    
    private Workflow workflow;
    
    
    public WorkflowEnvironment() {
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
     * 
     * @return
     * 
     * @hibernate.list table="litwf_activity" lazy="true" cascade="all"
     * @hibernate.collection-key column="env_id"
     * @hibernate.collection-index column="order_num"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     */
    public List getWaitingList() {
        return waitingList;
    }
    
    
    public void setWaitingList(List waitingList) {
        this.waitingList = waitingList;
    }
    
    
    public Map getValues() {
        return values;
    }
    
    
    public void setValues(Map values) {
        this.values = values;
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
    
    
}//class WorkflowEnvironment
