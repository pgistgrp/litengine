package org.pgist.wfengine;


/**
 * Task is a reusable step in workflow definition.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_task"
 */
public abstract class Task implements Cloneable {
    
    
    protected Long id;
    
    protected Activity activity;
    
    
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
    
    
    abstract public Task clone(Activity activity);
    
    
}//class Task
