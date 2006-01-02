package org.pgist.wfengine;

import java.util.HashMap;
import java.util.Map;


/**
 * Task is a reusable step in workflow definition.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_task"
 */
public abstract class Task implements Cloneable {
    
    
    public static final int TASK_AUTOMATIC = 1;
    
    public static final int TASK_MANUAL = 2;
    
    public static final int TASK_TIMERED = 3;
    
    
    protected Long id;
    
    protected Activity activity;
    
    protected transient Workflow workflow;
    
    protected transient Map properties = new HashMap();
    
    
    public Task() {
    }
    
    
    /**
     * @return
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
     * @hibernate.one-to-one cascade="all" class="org.pgist.wfengine.Activity"
     */
    public Activity getActivity() {
        return activity;
    }


    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    
    
    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }


    public Map getProperties() {
        return properties;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    abstract public Task clone(Activity activity);
    
    abstract public int getType();
    
    
    /**
     * Give Task object an oppotunity to initialize itself
     *
     */
    abstract public void initialize(Workflow workflow);
    
    
    /**
     * Execute the task.
     * @param workflow
     * @param activity
     * @return
     */
    abstract public int execute(Workflow workflow) throws Exception;
    
    
    /**
     * Give Task object an oppotunity to finalize itself
     *
     */
    abstract public void finalize(Workflow workflow);
    
    
}//class Task
