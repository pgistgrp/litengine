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
    
    
    protected String actionName = null;
    
    
    public PManualGameActivity() {
        type = TYPE_PMANUALGAME;
    }
    
    
    public PManualGameActivity clone() {
        PManualGameActivity act = new PManualGameActivity();
        
        act.getDeclaration().getIns().putAll(getDeclaration().getIns());
        act.getDeclaration().getOuts().putAll(getDeclaration().getOuts());
        act.setCounts(0);
        act.setName(getName());
        act.setDescription(getDescription());
        act.setActionName(getActionName());
        act.setExpression(getExpression());
        act.setPrev(null);
        act.setNext(null);
        act.setStatus(Activity.STATUS_INACTIVE);
        
        return act;
    }//clone()
    
    
    /**
     * @return
     * @hibernate.property column="action_name"
     */
    public String getActionName() {
        return actionName;
    }


    public void setActionName(String actionName) {
        this.actionName = actionName;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public PManualGameActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        PManualGameActivity newGame = new PManualGameActivity();
        
        //basic info
        newGame.setCounts(0);
        newGame.setPrev(clonedPrev);
        newGame.setStatus(STATUS_INACTIVE);
        newGame.setName(getName());
        newGame.setDescription(getDescription());
        newGame.setActionName(getActionName());
        
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
    
    
    protected boolean doExecute(RunningContext context, Stack stack) throws Exception {
        if (getExpression()>0) {//task is finished
            next.activate(context);
            stack.push(next);
            return true;
        } else {
            return false;
        }
    }//doExecute()
    
    
}//class PManualGameActivity
