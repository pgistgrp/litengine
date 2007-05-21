package org.pgist.wfengine;


/**
 * Running History.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_running_history"
 */
public class RunningHistory {
    
    
    private Long id;
    
    private Long activityId;
    
    private String paramStr;
    
    private String description;
    
    private String access;
    
    
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
     * @hibernate.property not-null="true"
     */
    public Long getActivityId() {
        return activityId;
    }


    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }


    /**
     * @return
     * 
     * @hibernate.property length="512" not-null="true"
     */
    public String getParamStr() {
        return paramStr;
    }


    public void setParamStr(String paramStr) {
        this.paramStr = paramStr;
    }


    /**
     * @return
     * 
     * @hibernate.property not-null="true"
     */
    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return
     * 
     * @hibernate.property not-null="true"
     */
    public String getAccess() {
        return access;
    }


    public void setAccess(String access) {
        this.access = access;
    }


}//class RunningHistory
