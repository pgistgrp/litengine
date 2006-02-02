package org.pgist.wfengine;


/**
 * AccessManager to contraint the access to an ManualTask
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_access_manager"
 */
public class AccessManager {
    
    
    protected Long id;
    
    
    public AccessManager() {
    }
    
    
    /**
     * @return
     * @hibernate.id generator-class="native"
     */
    public Long getId() {
        return id;
    }
    
    
    public void setId(Long id) {
        this.id = id;
    }

    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    /**
     * Check if the current user have the previlege
     * @param user
     * @return
     */
    //public boolean check(IUser user) {
    //    return false;
    //}//check()
    
    
}//class AccessManager
