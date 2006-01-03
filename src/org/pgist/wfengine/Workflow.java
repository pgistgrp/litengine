package org.pgist.wfengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

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
    private List waitingList = new ArrayList(16);
    
    //
    private boolean finished;
    
    //
    private boolean cancelled;
    
    //
    private Date beginTime;
    
    //
    private Date endTime;
    
    //
    private WorkflowTracker tracker;
    
    //
    private transient Stack stack = new Stack();
    private transient Stack parentStack = new Stack();
    
    
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
     * 
     * @return
     * 
     * @hibernate.list table="litwf_activity" lazy="true" cascade="all"
     * @hibernate.collection-key column="waiting_id"
     * @hibernate.collection-index column="order_num"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     */
    public List getWaitingList() {
        return waitingList;
    }


    public void setWaitingList(List waitingList) {
        this.waitingList = waitingList;
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
     * @hibernate.one-to-one cascade="all" class="org.pgist.wfengine.WorkflowTracker"
     */
    public WorkflowTracker getTracker() {
        return tracker;
    }


    public void setTracker(WorkflowTracker tracker) {
        this.tracker = tracker;
    }
    
    
    /*
     * -------------------------------------------------------------------
     */
    
    
    private void perform(Activity activity, Activity parent) throws Exception {
        //Execute this activity
        Activity[] list = activity.execute(this, parent);
        
        if (list==null || list.length==0) {
            //This activity is executed and flow branch finished
        } else if (list.length==1 && list[0]==activity) {
            //This activity is not executed
            waitingList.add(activity);
            return;
        } else {
            //This activity is executed, and its successive activities are returned
            
            for (int i=0,n=list.length; i<n; i++) {
                parentStack.push(activity);
                stack.push(list[i]);
            }//for i
            
            //Deactivate this activity
            activity.deActivate(this, null);
        }
    }//perform()
    
    
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
        
        stack.push(definition);
        parentStack.push(null);
        
        while (!stack.empty()) {
            //Pop out an activity and it's parent
            Activity activity = (Activity) stack.pop();
            Activity parent = (Activity) parentStack.pop();
            
            //Activity this activity
            activity.activate(this, parent);
            
            perform(activity, parent);
        }//while
        
    }//execute()
    
    
    /**
     * Execute a specific activity, this activity have to be the active activity in the environment
     * @param activity
     */
    synchronized public void proceed(Activity activity) throws Exception {
        if (finished || cancelled || !born) return;
        
        //check if the activity is current activity in the environment
        if (!waitingList.contains(activity)) return;
        
        waitingList.remove(activity);
        
        activity.proceed();
        perform(activity, null);
        
        while (!stack.empty()) {
            //Pop out an activity and it's parent
            activity = (Activity) stack.pop();
            Activity parent = (Activity) parentStack.pop();
            
            //Activity this activity
            activity.activate(this, parent);
            
            perform(activity, parent);
        }//while
    }//proceed()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
        definition.saveState(session);
    }//saveState()
    
    
}//class Workflow
