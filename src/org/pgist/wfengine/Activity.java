package org.pgist.wfengine;

import org.hibernate.Session;


/**
 * Abstract Activity class.
 * 
 * This class is the parent and root class for all activity classes.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_activity"
 */
public abstract class Activity implements Cloneable {
    
    
    public final static int UNDEFINED = -99999999;
    
    protected Long id = null;
    
    protected String caption = "";

    protected String url = null;
    
    protected int count = 0;
    
    protected int expression = 0;
    
    protected Task task;
    
    
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
     * @hibernate.property not-null="true"
     */
    public String getCaption() {
        return caption;
    }


    public void setCaption(String caption) {
        this.caption = caption;
    }


    /**
     * @return
     * @hibernate.property
     */
    public String getUrl() {
        return url;
    }
    
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    
    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public int getCount() {
        return count;
    }


    public void setCount(int count) {
        this.count = count;
    }


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public int getExpression() {
        return expression;
    }


    public void setExpression(int expression) {
        this.expression = expression;
    }


    /**
     * @return
     * @hibernate.many-to-one column="task_id" class="org.pgist.wfengine.Task" cascade="all"
     */
    public Task getTask() {
        return task;
    }


    public void setTask(Task task) {
        this.task = task;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    /**
     * Package Accessible
     * @param workflow
     * @param parent
     */
    final void activate(Workflow workflow, Activity parent) {
        System.out.println(this+" !!!! "+count);
        //Increase Count. That means the total visiting times for this activity.
        count++;
        System.out.println(this+" #### "+count);
        
        //expression==0 means for manual task, the task is waiting for performing
        expression = 0;
        
        //initialize the task
        if (task!=null) task.initialize(workflow);
        
        doActivate(workflow);
    }//activate
    
    
    /**
     * Package Accessible
     * @param env
     */
    final Activity[] execute(Workflow workflow, Activity parent) throws Exception {
        return doExecute(workflow);
    }//activate
    
    
    /**
     * Package Accessible
     * @param env
     */
    final void deActivate(Workflow workflow, Activity parent) {
        if (task!=null) task.finalize(workflow);
        
        doDeActivate(workflow);
        
        //Track the task
        if (task!=null) {
            workflow.getTracker().record(task);
        }
    }//activate
    
    
    /**
     * default implementation
     * @param workflow
     */
    protected void doActivate(Workflow workflow) {
    }//doActivate()
    
    
    /**
     * default implementation
     * @param workflow
     */
    protected void doDeActivate(Workflow workflow) {
    }//doDeActivate()
    
    
    abstract protected Activity[] doExecute(Workflow workflow) throws Exception;
    
    abstract protected void proceed() throws Exception;
    
    abstract protected void proceed(int decision) throws Exception;
    
    abstract public void saveState(Session session);
    
    
    abstract public Activity clone(Activity prev);
    
    abstract public Activity probe();
    

}//abstract class Activity
