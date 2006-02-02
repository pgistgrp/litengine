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
    
    public static final int TYPE_TERMINATE = 0;
    
    public static final int TYPE_PGAME     = 1;
    
    public static final int TYPE_MEETING   = 2;
    
    public static final int TYPE_PMETHOD   = 3;
    
    public static final int TYPE_BRANCH    = 4;
    
    public static final int TYPE_JOIN      = 5;
    
    public static final int TYPE_SWITCH    = 6;
    
    public static final int TYPE_ENDSWITCH = 7;
    
    public static final int TYPE_WHILE     = 8;
    
    public static final int TYPE_LOOP      = 9;
    
    public static final int TYPE_REPEAT    = 10;
    
    public static final int TYPE_UNTIL     = 11;
    
    public static final int TYPE_JUMP      = 12;
    
    
    protected Long id = null;
    
    protected int type;
    
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


    public int getType() {
        return type;
    }


    public void setType(int type) {
        this.type = type;
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
        //Increase Count. That means the total visiting times for this activity.
        setCount(getCount()+1);
        
        //expression==0 means for manual task, the task is waiting for performing
        setExpression(0);
        
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
    
    
}//abstract class Activity
