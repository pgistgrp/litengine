package org.pgist.wfengine.activity;

import java.util.Stack;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.WorkflowTask;


/**
 * The PAutoGameActivity implement the automatic pgame activity.<br>
 * 
 * One PGame is an atomic step or activity in a work flow.
 * 
 * @author kenny
 *
 * @hibernate.joined-subclass name="PAutoGameActivity" table="litwf_activity_pautogame"
 *                            lazy="true" proxy="org.pgist.wfengine.activity.PAutoGameActivity"
 * @hibernate.joined-subclass-key column="id"
 */
public class PAutoGameActivity extends PGameActivity implements SingleIn, SingleOut {
    
    
    protected String taskName = null;
    
    
    public PAutoGameActivity() {
        type = TYPE_PAUTOGAME;
    }
    
    
    public PAutoGameActivity clone() {
        PAutoGameActivity act = new PAutoGameActivity();
        
        act.getDeclaration().getIns().putAll(getDeclaration().getIns());
        act.getDeclaration().getOuts().putAll(getDeclaration().getOuts());
        act.setCounts(0);
        act.setName(getName());
        act.setDescription(getDescription());
        act.setTaskName(getTaskName());
        act.setExpression(getExpression());
        act.setPrev(null);
        act.setNext(null);
        act.setStatus(Activity.STATUS_INACTIVE);
        
        return act;
    }
    
    
    /**
     * @return
     * @hibernate.property column="task_name"
     */
    public String getTaskName() {
        return taskName;
    }


    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public PAutoGameActivity clone(Activity clonedPrev, Stack<Activity> clonedStop, Stack<Activity> stop) {
        PAutoGameActivity newGame = new PAutoGameActivity();
        
        //basic info
        newGame.setCounts(0);
        newGame.setPrev(clonedPrev);
        newGame.setStatus(STATUS_INACTIVE);
        newGame.setName(getName());
        newGame.setDescription(getDescription());
        newGame.setTaskName(getTaskName());
        
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
        Object object = null;//context.getTask(getTaskName());
        if (object instanceof WorkflowTask) {
            WorkflowTask task = (WorkflowTask) object;
            return task.execute(this, context, getDeclaration().getProperties());
        }
        
        return false;
//        if (getExpression()>0) {//task is finished
//            next.activate(context);
//            stack.push(next);
//            return true;
//        } else {
//            return false;
//        }
    }//doExecute()
    
    
}//class PAutoGameActivity
