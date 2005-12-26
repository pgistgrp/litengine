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
    
    protected Task task;
    
    protected WorkflowTrackRecord trackRecord;
    
    
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
     * @hibernate.many-to-one column="task_id" class="org.pgist.wfengine.Task" cascade="all"
     */
    public Task getTask() {
        return task;
    }


    public void setTask(Task task) {
        this.task = task;
    }


    /**
     * @return
     * @hibernate.many-to-one column="track_id" class="org.pgist.wfengine.WorkflowTrackRecord" cascade="all"
     */
    public WorkflowTrackRecord getTrackRecord() {
        return trackRecord;
    }


    public void setTrackRecord(WorkflowTrackRecord trackRecord) {
        this.trackRecord = trackRecord;
    }


    /**
     * Package Accessible
     * @param env
     */
    Activity[] activate(Workflow workflow, Activity parent) {
        trackRecord = new WorkflowTrackRecord();
        trackRecord.setWorkflowTracker(workflow.getTracker());
        if (parent!=null) trackRecord.getParents().add(parent.getTrackRecord());
        
        Activity[] activities = doActivate(workflow);
        if (activities!=null && (activities.length!=1 || activities[0]!=this)) count++;
        return activities;
    }//activate
    
    
    abstract protected Activity[] doActivate(Workflow workflow);
    
    abstract public void saveState(Session session);
    
    abstract public Activity clone(Activity prev);
    
    abstract public Activity probe();
    

}//abstract class Activity
