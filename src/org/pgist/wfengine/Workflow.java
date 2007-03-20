package org.pgist.wfengine;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.Session;
import org.pgist.wfengine.activity.GroupActivity;
import org.pgist.wfengine.util.Utils;
import org.springframework.beans.factory.BeanFactory;


/**
 * The Workflow is the working instance for a workflow.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_workflow"
 */
public class Workflow implements Serializable {
    
    
    private static final long serialVersionUID = -8038860928226339011L;
    
    
    protected Long id = null;
    
    private GroupActivity situation = null;
    
    //A workflow can't be definitioned more than once
    private boolean born = false;
    
    //
    private boolean finished;
    
    //
    private boolean cancelled;
    
    //
    private Date beginTime;
    
    //
    private Date endTime;
    
    
    public Workflow() {
    }
    
    
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
     * @hibernate.many-to-one column="group_id" cascade="all"
     */
    public GroupActivity getSituation() {
        return situation;
    }


    public void setSituation(GroupActivity situation) {
        this.situation = situation;
    }


    /**
     * @return
     * 
     * @hibernate.property unique="false" not-null="true"
     */
    public boolean isBorn() {
        return born;
    }
    
    
    public void setBorn(boolean born) {
        this.born = born;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.property unique="false" not-null="true"
     */
    public boolean isFinished() {
        return finished;
    }
    
    
    public void setFinished(boolean finished) {
        this.finished = finished;
    }


    /**
     * @return
     * 
     * @hibernate.property unique="false" not-null="true"
     */
    public boolean isCancelled() {
        return cancelled;
    }


    public void setCancelled(boolean canceled) {
        this.cancelled = canceled;
    }


    /**
     * @return
     * 
     * @hibernate.property unique="false"
     */
    public Date getBeginTime() {
        return beginTime;
    }


    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }


    /**
     * @return
     * 
     * @hibernate.property unique="false"
     */
    public Date getEndTime() {
        return endTime;
    }


    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    /*
     * -------------------------------------------------------------------
     */
    
    
    /**
     * Package Accessible.
     * Initailize this workflow.
     * This method can only be execute exactly ONCE!
     */
    void initialize() throws Exception {
        //Check if this workflow already finished, cancelled or born
        if (finished || cancelled || born) return;
        
        //Set born
        born = true;
        beginTime = new Date();
    }//initialize()
    
    
    /**
     * Package Accessible.
     * Execute this flow.
     */
    void execute(BeanFactory beanFactory) throws Exception {
        //Check if this workflow already finished, cancelled
        if (finished || cancelled) return;
        
        RunningContext context = situation.getContext();
        
        context.setBeanFactory(beanFactory);
        context.execute();
    }//execute()
    
    
    /**
     * Execute a specific activity, this activity have to be the active activity in the environment
     * @param activity
     */
    synchronized public void proceed(Activity activity) throws Exception {
        if (finished || cancelled || !born) return;
        
        situation.getContext().proceed(activity);
    }//proceed()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(this);
        situation.saveState(session);
    }//saveState()
    
    
    public Set getRunningActivities(int type) {
        Set set = new HashSet();
        Set activities = situation.getContext().getRunningActivities();
        for (Iterator iter=activities.iterator(); iter.hasNext(); ) {
            Activity one = (Activity) Utils.narrow(iter.next());
            if (type == one.getType()) set.add(one);
        }//for iter
        return set;
    }//getRunningActivities()
    
    
}//class Workflow
