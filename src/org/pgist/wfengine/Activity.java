package org.pgist.wfengine;


/**
 * Abstract Activity class.
 * This class is the parent and root class for all activity classes.
 * 
 * @author kenny
 *
 */
public abstract class Activity {
    
    
    protected Activity previous;
    protected boolean automatic = false;
    protected boolean active = false;
    protected IAction action = null;
    
    
    public Activity getPrevious() {
        return previous;
    }
    
    
    public Activity setPrevious(Activity previous) {
        this.previous = previous;
        return this.previous;
    }
    
    
    public boolean getActive() {
        return active;
    }
    
    
    public abstract boolean activate(FlowEnvironment env);
    
    
    public abstract void proceed();
    
    
}//abstract class Activity
