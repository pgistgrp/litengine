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
    
    private Activity activity;
    
    
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
     * @hibernate.many-to-one column="activity_id" cascade="all" lazy="true"
     */
    public Activity getActivity() {
        return activity;
    }


    public void setActivity(Activity activity) {
        this.activity = activity;
    }


}//class RunningHistory
