package org.pgist.wfengine;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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
public abstract class Activity implements Serializable {
    
    
    public final static int TYPE_UNDEFINED = -99999999;
    
    public static final int TYPE_PAUTOGAME = 0;
    
    public static final int TYPE_PMANUALGAME = 1;
    
    public static final int TYPE_PMETHOD   = 2;
    
    public static final int TYPE_MEETING   = 3;
    
    public static final int TYPE_SITUATION = 4;
    
    public static final int TYPE_RETURN    = 5;
    
    public static final int TYPE_BRANCH    = 6;
    
    public static final int TYPE_JOIN      = 7;
    
    public static final int TYPE_SWITCH    = 8;
    
    public static final int TYPE_ENDSWITCH = 9;
    
    public static final int TYPE_WHILE     = 10;
    
    public static final int TYPE_LOOP      = 11;
    
    public static final int TYPE_REPEAT    = 12;
    
    public static final int TYPE_UNTIL     = 13;
    
    public static final int TYPE_JUMP      = 14;
    
    public static final int TYPE_TERMINATE = 15;
    
    
    protected Long id = null;
    
    protected int type;
    
    protected int counts = 0;
    
    protected RunningContext runningContext;
    
    
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
    public int getType() {
        return type;
    }


    public void setType(int type) {
        this.type = type;
    }


    /**
     * @return
     * 
     * @hibernate.property not-null="true"
     */
    public int getCounts() {
        return counts;
    }


    public void setCounts(int count) {
        this.counts = count;
    }


    /**
     * @return
     * 
     * @hibernate.many-to-one column="running_id" cascade="all"
     */
    public RunningContext getRunningContext() {
        return runningContext;
    }


    public void setRunningContext(RunningContext runningContext) {
        this.runningContext = runningContext;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (!(obj instanceof Activity)) return false;
        Activity object = (Activity) obj;
        Long myId = getId();
        Long id = object.getId();
        if ( (myId==null && id!=null) || (myId!=null && id==null) ) {
            return false;
        } else if (myId!=null && id!=null) {
            return myId.longValue()==id.longValue();
        }
        return false;
    }//equals()
    
    
    /**
     * Package Access Only
     * 
     * @param context
     */
    public final void activate(RunningContext context) throws Exception {
        //Increase Count. That means the total visiting times for this activity.
        setCounts(getCounts()+1);
        
        doActivate(context);
    }//activate
    
    
    /**
     * Package Access Only
     * 
     * @param context
     * @param stack
     */
    final boolean execute(RunningContext context) throws Exception {
        return doExecute(context);
    }//activate
    
    
    /**
     * Package Access Only
     * 
     * @param context
     */
    final void deActivate(RunningContext context) {
        doDeActivate(context);
    }//deActivate
    
    
    /**
     * default implementation
     * 
     * @param context
     */
    protected void doActivate(RunningContext context) throws Exception {
    }//doActivate()
    
    
    /**
     * default implementation
     * 
     * @param context
     */
    protected void doDeActivate(RunningContext context) {
    }//doDeActivate()
    
    
    abstract protected boolean doExecute(RunningContext context) throws Exception;
    
    
    protected void proceed() throws Exception {
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
    }//proceed()
    
    
    abstract public void saveState(Session session);
    
    
    abstract public Activity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop);
    
    abstract public Activity getEnd();
    
    abstract public void getAgenda(List activities);
    
    
    /**
     * Static Inner class
     */
    public static class NextStepInfo {
        
        private boolean flag;
        private String access;
        private Activity activity;
        
        public NextStepInfo(Activity activity, String access) {
            flag = false;
            this.access = access;
            this.activity = activity;
        }
        
        public String getAccess() {
            return access;
        }
        
        public boolean isFlag() {
            return flag;
        }
        public void setFlag() {
            this.flag = true;
        }

        public Activity getActivity() {
            return activity;
        }
        
    }//class NextStepInfo
    
    abstract public Activity getNextStep(NextStepInfo nsi);

    
    public void setFuture(List futures, Set embedding) {}


}//abstract class Activity
