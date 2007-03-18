package org.pgist.wfengine;

import java.io.Serializable;
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
    
    
    public final static int UNDEFINED      = -99999999;
    
    public static final int TYPE_PACT      = 0;
    
    public static final int TYPE_PGAME     = 1;
    
    public static final int TYPE_PMETHOD   = 2;
    
    public static final int TYPE_MEETING   = 3;
    
    public static final int TYPE_RETURN    = 4;
    
    public static final int TYPE_BRANCH    = 5;
    
    public static final int TYPE_JOIN      = 6;
    
    public static final int TYPE_SWITCH    = 7;
    
    public static final int TYPE_ENDSWITCH = 8;
    
    public static final int TYPE_WHILE     = 9;
    
    public static final int TYPE_LOOP      = 10;
    
    public static final int TYPE_REPEAT    = 11;
    
    public static final int TYPE_UNTIL     = 12;
    
    public static final int TYPE_JUMP      = 13;
    
    public static final int TYPE_TERMINATE = 14;
    
    public static final int STATUS_INACTIVE = 0;
    
    public static final int STATUS_ACTIVE   = 1;
    
    
    protected Long id = null;
    
    protected int type;
    
    protected int counts = 0;
    
    protected int expression = 0;
    
    protected int status = STATUS_INACTIVE;
    
    
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
    public int getType() {
        return type;
    }


    public void setType(int type) {
        this.type = type;
    }


    /**
     * @return
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
     * @hibernate.property not-null="true"
     */
    public int getExpression() {
        return expression;
    }


    public void setExpression(int expression) {
        this.expression = expression;
    }


    /**
     * @return
     * @hibernate.property not-null="true"
     */
    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
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
     * @param context
     */
    public final void activate(RunningContext context) {
        //Increase Count. That means the total visiting times for this activity.
        setCounts(getCounts()+1);
        
        //expression==0 means for manual task, the task is waiting for performing
        setExpression(0);
        
        //set status
        setStatus(STATUS_ACTIVE);
        
        doActivate(context);
    }//activate
    
    
    /**
     * Package Access Only
     * @param context
     * @param stack
     */
    final boolean execute(RunningContext context, Stack stack) throws Exception {
        return doExecute(context, stack);
    }//activate
    
    
    /**
     * Package Access Only
     * @param context
     */
    final void deActivate(RunningContext context) {
        doDeActivate(context);
        
        //set status
        setStatus(STATUS_INACTIVE);
    }//deActivate
    
    
    /**
     * default implementation
     * @param context
     */
    protected void doActivate(RunningContext context) {
    }//doActivate()
    
    
    /**
     * default implementation
     * @param context
     */
    protected void doDeActivate(RunningContext context) {
    }//doDeActivate()
    
    
    public boolean isAutomatic() {
        return true;
    }//isAutomatic()
    
    
    abstract protected boolean doExecute(RunningContext context, Stack stack) throws Exception;
    
    
    protected void proceed() throws Exception {
        setExpression(1);
    }//proceed()
    
    
    protected void proceed(int decision) throws Exception {
        setExpression(decision);
    }//proceed()
    
    
    abstract public void saveState(Session session);
    
    
}//abstract class Activity
