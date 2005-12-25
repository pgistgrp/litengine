package org.pgist.wfengine;

import java.io.Serializable;
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
    private WorkflowEnvironment env = new WorkflowEnvironment();
    
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
    
    
    public Workflow() {
    }
    
    
    /**
     * @return
     * Notes: the id comes from one-to-one mapped Content object.
     * 
     * @hibernate.id generator-class="foreign"
     * @hibernate.generator-param name="property" value="env"
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
     * @hibernate.one-to-one cascade="all" class="org.pgist.wfengine.WorkflowTracker"
     */
    public WorkflowTracker getTracker() {
        return tracker;
    }


    public void setTracker(WorkflowTracker tracker) {
        this.tracker = tracker;
    }


    /**
     * Initially execute the workflow.
     * This method can only be execute exactly ONCE!
     */
    synchronized void execute() {
        //Check if this workflow already finished, cancelled or born
        if (finished || cancelled || born) return;
        
        //The stack to execute this workflow
        Stack stack = env.getExecuteStack();
        
        //Set born
        born = true;
        beginTime = new Date();
        
        stack.push(definition);
        
        List waitingList = env.getWaitingList();
        
        while (!stack.empty()) {
            //Pop out an activity
            Activity activity = (Activity) stack.pop();
            
            //Activity this activity
            Activity[] list = activity.activate(this, env);
            
            if (list==null || list.length==0) {
                //This activity is executed and flow branch finished
            } else if (list.length==1 && list[0]==activity) {
                //This activity is not executed
                waitingList.add(activity);
            } else {
                //This activity is executed, and its successive activities are returned
                for (int i=0; i<list.length; i++) {
                    stack.add(list[i]);
                }//for i
            }
        }//while
        
    }//execute()
    
    
    /**
     * Execute a specific activity, this activity have to be the active activity in the environment
     * @param activity
     */
    public void execute(Activity activity) {
        if (finished || cancelled) return;
        
        List waitingList = env.getWaitingList();
        
        //check if the activity is current activity in the environment
        if (!waitingList.contains(activity)) return;
        
        Stack stack = new Stack();
        waitingList.remove(activity);
        stack.push(activity);
        
        while (!stack.empty()) {
            Activity one = (Activity) stack.pop();
            System.out.println("---> active activity: "+one.getId());
            one.activate(this, env);
        }//while
    }//execute()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(env);
        session.saveOrUpdate(this);
        definition.saveState(session);
    }//saveState()
    
    
}//class Workflow
