package org.pgist.wfengine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pgist.model.IUser;


/**
 * Manually running Task.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="Task" table="litwf_task_manual"
 * @hibernate.joined-subclass-key column="id"
 */
public abstract class ManualTask extends Task {
    
    
    protected IUser user;
    
    protected AccessManager accessManager;
    
    
    public void setUser(IUser user) {
        this.user = user;
    }


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
    
    
    /**
     * Package Accessible
     * @param workflow
     * @param activity
     * @return
     * @throws Exception
     */
    String perform(Workflow workflow, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //Check Access Permission
        if (accessManager!=null && !accessManager.check(user)) throw new Exception("User has no access permission!");
        
        return execute(workflow, request, response);
    }//perform()
    
    
    /**
     * @param activity
     * @return
     */
    public Task clone(Activity activity) {
        try {
            Task result = (ManualTask) super.clone();
            result.setActivity(activity);
            return result;
        } catch(Exception e) {
            return null;
        }
    }

    
    /**
     * Execute the task.
     * @param workflow
     * @param activity
     * @return
     */
    abstract protected String execute(Workflow workflow,  HttpServletRequest request, HttpServletResponse response);
    
    
    /**
     * Give Task object an oppotunity to initialize itself
     *
     */
    public void init(Workflow workflow) {
    }
    
    
    /**
     * Give Task object an oppotunity to finalize itself
     *
     */
    public void destroy(Workflow workflow) {
    }


}//abstract class ManualTask
