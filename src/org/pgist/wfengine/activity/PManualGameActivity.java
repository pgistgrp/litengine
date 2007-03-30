package org.pgist.wfengine.activity;

import java.util.Stack;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;


/**
 * The PGameActivity implement the manual pgame activity.<br>
 * 
 * One PGame is an atomic step or activity in a work flow.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="PManualGameActivity" table="litwf_activity_pmangame"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.PManualGameActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class PManualGameActivity extends PGameActivity {
    
    
    private static final long serialVersionUID = -3825305313202911455L;
    
    
    protected String actionName = null;
    
    protected String access = "all";
    
    protected long extension;
    
    
    public PManualGameActivity() {
        type = TYPE_PMANUALGAME;
    }
    
    
    /**
     * @return
     * 
     * @hibernate.property column="action_name" not-null="true"
     */
    public String getActionName() {
        return actionName;
    }


    public void setActionName(String actionName) {
        this.actionName = actionName;
    }


    /**
     * @return
     * 
     * @hibernate.property column="access" not-null="true"
     */
    public String getAccess() {
        return access;
    }


    public void setAccess(String access) {
        this.access = access;
    }


    /**
     * @return
     * 
     * @hibernate.property column="extension" not-null="true"
     */
    public long getExtension() {
        return extension;
    }


    public void setExtension(long extension) {
        this.extension = extension;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public PManualGameActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        PManualGameActivity newGame = new PManualGameActivity();
        
        //basic info
        newGame.setCounts(0);
        newGame.setPrev(clonedPrev);
        newGame.setName(getName());
        newGame.setDescription(getDescription());
        newGame.setActionName(getActionName());
        newGame.setAccess(getAccess());
        newGame.setExtension(getExtension());
        
        Activity act = getNext();
        if (act!=null) {
            Activity newAct = act.clone(newGame, clonedStop, stop);
            newGame.setNext(newAct);
        }
        
        return newGame;
    }//clone()
    
    
    public Activity getEnd() {
        return (getNext()==null) ? this : getNext().getEnd(); 
    }//getEnd()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    protected boolean doExecute(RunningContext context) throws Exception {
        context.getRunningActivities().add(this);
        
        if (getExtension()>0) {
            context.addJob(this, getExtension());
        }
        
        return false;
    }//doExecute()
    
    
    protected void doDeActivate(RunningContext context) {
        context.getStack().push(getNext());
    }//doDeActivate()
    
    
}//class PManualGameActivity
