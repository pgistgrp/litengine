package org.pgist.wfengine;


/**
 * Automatic Task
 * @author kenny
 *
 * @hibernate.joined-subclass name="Task" table="litwf_task_auto"
 * @hibernate.joined-subclass-key column="id"
 */
public abstract class AutoTask extends Task {
    
    
    public int getType() {
        return TASK_AUTOMATIC;
    }//getType()
    
    
    /**
     * @param activity
     * @return
     */
    public Task clone(Activity activity) {
        try {
            Task result = (AutoTask) super.clone();
            result.setActivity(activity);
            return result;
        } catch(Exception e) {
            return null;
        }
    }//clone()
    
    
}//abstract class AutoTask
