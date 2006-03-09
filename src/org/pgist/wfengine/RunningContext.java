package org.pgist.wfengine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;


/**
 * RunningContext is a class to management the execution context of a workflow piece.
 * It is included in each GroupActivity and Workflow class.
 * 
 * The running context has its parent context and child contexts, so it's actually a
 * context tree.
 * 
 * RunningContext keeps track of current running activities and all records generated
 * by activities.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_running_context"
 */
public class RunningContext {
    
    
    private Long id;
    
    private RunningContext parent = null;
    
    private Set runningActivities = new HashSet();
    
    private List records = new ArrayList();
    
    
    /*
     * Constructors
     */
    
    
    public RunningContext() {}
    
    
    public RunningContext(RunningContext parent) {
        this.parent = parent;
    }
    
    
    /*
     * Getters and Setters
     */
    
    
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
     * @hibernate.many-to-one column="parent_id" class="org.pgist.wfengine.RunningContext" cascade="all"
     */
    public RunningContext getParent() {
        return parent;
    }


    public void setParent(RunningContext parent) {
        this.parent = parent;
    }


    /**
     * 
     * @return
     * 
     * @hibernate.set table="litwf_activity" lazy="true" cascade="all" order-by="id"
     * @hibernate.collection-key column="context_id"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     */
    public Set getRunningActivities() {
        return runningActivities;
    }


    public void setRunningActivities(Set runningActivity) {
        this.runningActivities = runningActivity;
    }


    /**
     * @return
     * 
     * @hibernate.list table="litwf_task" lazy="true" cascade="all"
     * @hibernate.collection-key column="context_id"
     * @hibernate.collection-index column="task_order"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Task"
     * 
     */
    public List getRecords() {
        return records;
    }


    public void setRecords(List records) {
        this.records = records;
    }
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public void addActivity(Activity activity) {
        //Activate this activity
        activity.activate(this);
        getRunningActivities().add(activity);
    }//addActivity()
    
    
    protected void perform(Stack stack, Set activities, Activity activity) throws Exception {
        //Execute this activity
        boolean finished = activity.execute(this, stack);
        
        if (finished) {
            //Deactivate this activity
            activity.deActivate(this);
        } else {
            //Return to running list
            activities.add(activity);
        }
    }//perform()
    
    
    synchronized public void execute() throws Exception {
        Stack stack = new Stack();
        
        //Put all running activities into stack
        stack.addAll(getRunningActivities());
        
        while (!stack.empty()) {
            //Pop out an activity
            Activity activity = (Activity) stack.pop();
            
            for (Iterator iter=runningActivities.iterator(); iter.hasNext(); ) {
                Activity one = (Activity) iter.next();
                if (one.equals(activity)) {
                    runningActivities.remove(one);
                    break;
                }
            }//for iter
            
            perform(stack, runningActivities, activity);
        }//while
        
    }//execute()
    
    
    /**
     * Execute a specific activity, this activity have to be the active activity in the environment
     * @param activity
     * @return
     */
    synchronized public boolean proceed(Activity activity) throws Exception {
        Stack stack = new Stack();
        
        //check if the activity is current activity in the environment
        //if (!runningActivities.contains(activity)) return;
        
        activity.proceed();
        stack.push(activity);
        
        while (!stack.empty()) {
            //Pop out an activity and it's parent
            activity = (Activity) stack.pop();
            
            for (Iterator iter=runningActivities.iterator(); iter.hasNext(); ) {
                Activity one = (Activity) iter.next();
                if (one.equals(activity)) {
                    runningActivities.remove(one);
                    break;
                }
            }//for iter
            
            perform(stack, runningActivities, activity);
        }//while
        
        return runningActivities.size()==0;
    }//proceed()
    
    
}//class RunningContext
