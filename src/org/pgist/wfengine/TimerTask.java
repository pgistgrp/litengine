package org.pgist.wfengine;


/**
 * Automatic Task
 * @author kenny
 *
 * @hibernate.joined-subclass name="Task" table="litwf_task_auto"
 * @hibernate.joined-subclass-key column="id"
 */
public abstract class TimerTask extends Task {
    
    
    public int getType() {
        return TASK_TIMERED;
    }//getType()
    
    
    /**
     * @param activity
     * @return
     */
    public Task clone(Activity activity) {
        try {
            Task result = (TimerTask) super.clone();
            result.setActivity(activity);
            return result;
        } catch(Exception e) {
            return null;
        }
    }//clone()
    
    
    /**
     * initialize itself
     */
    public void initialize(Workflow workflow) {
        //TODO add this to Quartz Server
    }//initialize()
    
    
    /**
     * finalize itself
     */
    public void finalize(Workflow workflow) {
        //TODO delete this from Quartz Server
    }//finalize()
    
    
}//abstract class TimerTask
