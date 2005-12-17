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
    
    protected Environment env = new Environment();
    
    protected BackTracable head;
    
    protected PushDownable tail;
    
    
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
     * @hibernate.many-to-one column="head_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public BackTracable getHead() {
        return head;
    }


    public void setHead(BackTracable activity) {
        this.head = activity;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="tail_id" class="org.pgist.wfengine.Activity" cascade="all"
     */
    public PushDownable getTail() {
        return tail;
    }


    public void setTail(PushDownable tail) {
        this.tail = tail;
    }
    
    
    public LinearTasks spawn() {
        BackTracable newHead = null;
        PushDownable newTail = null;
        
        Activity headActivity = ((Activity) head).clone(null);
        newHead = (BackTracable) headActivity;
        newTail = (PushDownable) headActivity.probe();
        
        LinearTasks tasks = new LinearTasks(newHead, newTail);
        return tasks;
    }//spawn()


}//class WFProcess
