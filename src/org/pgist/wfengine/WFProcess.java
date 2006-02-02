package org.pgist.wfengine;


/**
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_process"
 */
public class WFProcess {

    
    protected Long id;
    
    protected String name;
    
    protected String description;

    protected Environment env = new Environment();
    
    protected Activity head;
    
    protected Activity tail;
    
    protected boolean deleted;
    
    
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


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Environment getEnv() {
        return env;
    }


    public void setEnv(Environment env) {
        this.env = env;
    }


    /**
     * @return
     * @hibernate.many-to-one column="head_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getHead() {
        return (Activity) head;
    }


    public void setHead(Activity activity) {
        this.head = activity;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="tail_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public Activity getTail() {
        return (Activity) tail;
    }


    public void setTail(Activity tail) {
        this.tail = tail;
    }
    
    
    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public boolean isDeleted() {
        return deleted;
    }


    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }


    /*
    public FlowPiece spawn() {
        BackTracable newHead = null;
        PushDownable newTail = null;
        
        Activity headActivity = ((Activity) head).clone(null);
        newHead = (BackTracable) headActivity;
        newTail = (PushDownable) headActivity.probe();
        
        FlowPiece tasks = new FlowPiece(newHead, newTail);
        return tasks;
    }//spawn()
    */


}//class WFProcess
