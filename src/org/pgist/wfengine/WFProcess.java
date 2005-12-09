package org.pgist.wfengine;


/**
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_process"
 */
public class WFProcess {

    
    public Long id;
    
    public String name;
    
    public Environment env = new Environment();
    
    public Activity activity;
    
    
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


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Environment getEnv() {
        return env;
    }


    public void setEnv(Environment env) {
        this.env = env;
    }


    /**
     * @return
     * @hibernate.many-to-one column="activity_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getActivity() {
        return activity;
    }


    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    
    
}//class WFProcess
