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
     * @hibernate.set table="litwf_activity" lazy="true" cascade="all"
     * @hibernate.collection-key  column="context_id"
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
     * 
     */
    
    
    protected void perform(Stack stack, Stack parentStack, Activity activity, Activity parent) throws Exception {
        //Execute this activity
        Activity[] list = activity.execute(this, parent);
        
        if (list==null || list.length==0) {
            //This activity is executed and flow branch finished
        } else if (list.length==1 && list[0].getId()==activity.getId()) {
            //This activity is not executed
            if (activity.getTask()!=null) records.add(activity.getTask());
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
    
    
    synchronized public void execute() throws Exception {
        Stack stack = new Stack();
        Stack parentStack = new Stack();
        
        for (Iterator iter=runningActivities.iterator(); iter.hasNext(); ) {
            stack.push(iter.next());
            parentStack.push(null);
        }//for iter
        
        while (!stack.empty()) {
            //Pop out an activity and it's parent
            Activity activity = (Activity) stack.pop();
            Activity parent = (Activity) parentStack.pop();
            
            //Activity this activity
            activity.activate(this, parent);
            
            perform(stack, parentStack, activity, parent);
        }//while
    }//execute()
    
    
    /**
     * Execute a specific activity, this activity have to be the active activity in the environment
     * @param activity
     */
    synchronized public void proceed(Activity activity) throws Exception {
        Stack stack = new Stack();
        Stack parentStack = new Stack();
        
        //check if the activity is current activity in the environment
        if (!runningActivities.contains(activity)) return;
        
        runningActivities.remove(activity);
        
        activity.proceed();
        perform(stack, parentStack, activity, null);
        
        while (!stack.empty()) {
            //Pop out an activity and it's parent
            activity = (Activity) stack.pop();
            Activity parent = (Activity) parentStack.pop();
            
            //Activity this activity
            activity.activate(this, parent);
            
            perform(stack, parentStack, activity, parent);
        }//while
    }//proceed()
    
    
}//class RunningContext
