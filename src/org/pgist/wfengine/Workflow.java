package org.pgist.wfengine;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.Session;


/**
 * The Workflow is the working instance for a workflow.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_workflow"
 */
public class Workflow implements Serializable {
    
    
    private static final long serialVersionUID = -8038860928226339011L;
    
    
    protected Long id = null;
    
    //The definition activity of a workflow instance
    private Activity definition = null;
    
    //A workflow can't be definitioned more than once
    private boolean born = false;
    
    //
    private WorkflowEnvironment env;
    
    //
    private boolean finished;
    
    //
    private boolean cancelled;
    
    //
    private Date beginTime;
    
    //
    private Date endTime;
    
    //
    private RunningContext context;
    
    public Workflow() {
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
     * 
     * @hibernate.property unique="false" not-null="true"
     */
    public boolean isBorn() {
        return born;
    }
    
    
    public void setBorn(boolean born) {
        this.born = born;
    }
    
    
    /**
     * 
     * @return
     * 
     * @hibernate.one-to-one class="org.pgist.wfengine.WorkflowEnvironment" cascade="all"
     */
    public WorkflowEnvironment getEnv() {
        return env;
    }
    
    
    public void setEnv(WorkflowEnvironment env) {
        this.env = env;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="definition_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getDefinition() {
        return definition;
    }
    
    
    public Activity setDefinition(Activity definition) {
        this.definition = definition;
        return this.definition;
    }//setDefinition()
    
    
    /**
     * @return
     * 
     * @hibernate.property unique="false" not-null="true"
     */
    public boolean isFinished() {
        return finished;
    }
    
    
    public void setFinished(boolean finished) {
        this.finished = finished;
    }


    /**
     * @return
     * 
     * @hibernate.property unique="false" not-null="true"
     */
    public boolean isCancelled() {
        return cancelled;
    }


    public void setCancelled(boolean canceled) {
        this.cancelled = canceled;
    }


    /**
     * @return
     * 
     * @hibernate.property unique="false"
     */
    public Date getBeginTime() {
        return beginTime;
    }


    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }


    /**
     * @return
     * 
     * @hibernate.property unique="false"
     */
    public Date getEndTime() {
        return endTime;
    }


    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    /**
     * @return
     * @hibernate.many-to-one column="context_id" class="org.pgist.wfengine.RunningContext" cascade="all"
     */
    public RunningContext getContext() {
        return context;
    }


    public void setContext(RunningContext context) {
        this.context = context;
    }


    /*
     * -------------------------------------------------------------------
     */
    
    
    /**
     * Package Accessible.
     * Initially execute the workflow.
     * This method can only be execute exactly ONCE!
     */
    void execute() throws Exception {
        //Check if this workflow already finished, cancelled or born
        if (finished || cancelled || born) return;
        
        //Set born
        born = true;
        beginTime = new Date();
        
        context.getRunningActivities().add(getDefinition());
        context.execute();
    }//execute()
    
    
    /**
     * Execute a specific activity, this activity have to be the active activity in the environment
     * @param activity
     */
    synchronized public void proceed(Activity activity) throws Exception {
        if (finished || cancelled || !born) return;
        
        context.proceed(activity);
    }//proceed()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
        definition.saveState(session);
    }//saveState()
    
    
}//class Workflow
