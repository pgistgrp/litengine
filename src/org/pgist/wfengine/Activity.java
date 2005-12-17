package org.pgist.wfengine;

import org.hibernate.Session;


/**
 * Abstract Activity class.
 * 
 * This class is the parent and root class for all activity classes.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_activity"
 */
public abstract class Activity implements Cloneable {
    
    
    public final static int UNDEFINED = -99999999;
    
    protected Long id = null;
    
    protected String caption = "";

    protected boolean automatic = false;
    
    protected String performerClass = null;
    
    protected String url = null;
    
    
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
    public String getCaption() {
        return caption;
    }


    public void setCaption(String caption) {
        this.caption = caption;
    }


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public boolean getAutomatic() {
        return automatic;
    }
    
    
    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }
    
    
    /**
     * @return
     * @hibernate.property
     */
    public String getPerformerClass() {
        return performerClass;
    }
    
    
    public void setPerformerClass(String performerClass) {
        this.performerClass = performerClass;
    }
    
    
    /**
     * @return
     * @hibernate.property
     */
    public String getUrl() {
        return url;
    }
    
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    
    public int doPerform(WorkflowEnvironment env) {
        if (performerClass==null || "".equals(performerClass)) return UNDEFINED;
        
        int result = UNDEFINED;
        
        try {
            IPerformer performer = (IPerformer) Class.forName(performerClass).newInstance();
            result = performer.perform(this, env);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }//activate()
    
    
    /**
     * The reach() should be called exactly once before any activity is activated.
     * @param from
     * @param env
     */
    public void reach(Activity from, WorkflowEnvironment env) {
        //Default do nothing.
    }//reach()
    
    
    public abstract boolean activate(WorkflowEnvironment env);
    
    public abstract void saveState(Session session);
    
    abstract public Activity clone(Activity prev);
    
    abstract public Activity probe();


}//abstract class Activity
