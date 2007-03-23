package org.pgist.wfengine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.pgist.wfengine.activity.GroupActivity;
import org.pgist.wfengine.activity.SituationActivity;


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
    
    private RunningContext parent;
    
    private GroupActivity group;
    
    private Set<Activity> runningActivities = new HashSet<Activity>();
    
    private Set<Activity> pendingActivities = new HashSet<Activity>();
    
    private Set<Activity> haltingActivities = new HashSet<Activity>();
    
    private List records = new ArrayList();
    
    /* workflow environment */
    private Environment environment = new Environment();
    
    Stack<Activity> stack = new Stack<Activity>();
    
    
    /*
     * Constructors
     */
    
    
    public RunningContext() {
        environment.setContext(this);
    }
    
    
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
     * 
     * @hibernate.many-to-one column="parent_id" cascade="all"
     */
    public RunningContext getParent() {
        return parent;
    }


    public void setParent(RunningContext parent) {
        this.parent = parent;
    }


    /**
     * @return
     * 
     * @hibernate.many-to-one column="group_id" cascade="all"
     */
    public GroupActivity getGroup() {
        return group;
    }


    public void setGroup(GroupActivity group) {
        this.group = group;
    }


    /**
     * 
     * @return
     * 
     * @hibernate.set lazy="true" cascade="all" order-by="id"
     * @hibernate.collection-key column="running_id"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     */
    public Set<Activity> getRunningActivities() {
        return runningActivities;
    }


    public void setRunningActivities(Set<Activity> runningActivity) {
        this.runningActivities = runningActivity;
    }


    /**
     * 
     * @return
     * 
     * @hibernate.set lazy="true" cascade="all" order-by="id"
     * @hibernate.collection-key column="pending_id"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     */
    public Set<Activity> getPendingActivities() {
        return pendingActivities;
    }


    public void setPendingActivities(Set<Activity> pendingActivities) {
        this.pendingActivities = pendingActivities;
    }


    /**
     * 
     * @return
     * 
     * @hibernate.set lazy="true" cascade="all" order-by="id"
     * @hibernate.collection-key column="halting_id"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     */
    public Set<Activity> getHaltingActivities() {
        return haltingActivities;
    }


    public void setHaltingActivities(Set<Activity> haltingActivities) {
        this.haltingActivities = haltingActivities;
    }


    /**
     * @return
     * 
     * @hibernate.list table="litwf_task" lazy="true" cascade="all"
     * @hibernate.collection-key column="record_id"
     * @hibernate.collection-index column="task_order"
     * @hibernate.collection-one-to-many class="org.pgist.wfengine.Activity"
     * 
     */
    public List getRecords() {
        return records;
    }


    public void setRecords(List records) {
        this.records = records;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.one-to-one cascade="all"
     */
    public Environment getEnvironment() {
        return environment;
    }


    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public Stack<Activity> getStack() {
        return stack;
    }


    public WorkflowTaskRegistry getRegistry() {
        if (getParent()!=null) {
            return getParent().getRegistry();
        } else {
            SituationActivity situation = (SituationActivity) group;
            return situation.getWorkflow().getRegistry();
        }
    }//getRegistry()


    protected void perform(Stack<Activity> stack, Set activities, Activity activity) throws Exception {
        //Execute this activity
        try {
            activity.execute(this);
            activity.deActivate(this);
        } catch (Exception e) {
            getHaltingActivities().add(activity);
        }
    }//perform()
    
    
    synchronized public void execute() throws Exception {
        while (!stack.empty()) {
            //Pop out an activity
            Activity activity = stack.pop();
            activity.activate(this);
            if (activity.execute(this)) {
                activity.deActivate(this);
            }
        }//while
    }//execute()
    
    
    /**
     * Execute a specific activity, this activity have to be the active activity in the environment
     * @param activity
     * @return
     */
    synchronized public boolean proceed(Activity activity) throws Exception {
        Stack<Activity> stack = new Stack<Activity>();
        
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
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    public Integer getIntValue(String name) {
        return environment.getIntValues().get(name);
    }//getIntValue()
    
    
    public void setIntValue(String name, Integer value) {
        environment.getIntValues().put(name, value);
    }//getIntValue()
    
    
    public String getStrValue(String name) {
        return environment.getStrValues().get(name);
    }//getIntValue()
    
    
    public void setStrValue(String name, String value) {
        environment.getStrValues().put(name, value);
    }//setStrValue()
    
    
}//class RunningContext
