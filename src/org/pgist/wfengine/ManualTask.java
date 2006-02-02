package org.pgist.wfengine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Manually running Task.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="Task" table="litwf_task_manual"
 * @hibernate.joined-subclass-key column="id"
 */
public abstract class ManualTask extends Task {
    
    
    protected AccessManager accessManager;
    
    
    /**
     * @return
     * @hibernate.many-to-one column="access_id" class="org.pgist.wfengine.AccessManager" cascade="all"
     */
    public AccessManager getAccessManager() {
        return accessManager;
    }
    
    
    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }
    
    
    public int getType() {
        return TASK_MANUAL;
    }//getType()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    /**
     * Package Accessible
     * 
     * @param workflow
     * @param activity
     * @return
     * @throws Exception
     */
    public void execute(Workflow workflow) throws Exception {
        //Check Access Permission
        //if (accessManager!=null && !accessManager.check(user)) throw new Exception("User has no access permission!");
        
        execute(workflow, (HttpServletRequest) properties.get("request"), (HttpServletResponse) properties.get("response"));
    }//execute()
    
    
    /**
     * @param activity
     * @return
     */
    public Task clone(Activity activity) {
        try {
            Task result = (ManualTask) super.clone();
            result.setId(null);
            result.setActivity(activity);
            return result;
        } catch(Exception e) {
            return null;
        }
    }//clone()

    
    /**
     * Give Task object an oppotunity to initialize itself, default implementation
     *
     */
    public void initialize(Workflow workflow) {
    }//initialize()
    
    
    /**
     * Give Task object an oppotunity to finalize itself, default implementation
     *
     */
    public void finalize(Workflow workflow) {
    }//finalize()


    /**
     * Execute the task.
     * 
     * @param workflow
     * @param activity
     * @return
     */
    abstract protected void execute(Workflow workflow,  HttpServletRequest request, HttpServletResponse response);
    
    
}//abstract class ManualTask
