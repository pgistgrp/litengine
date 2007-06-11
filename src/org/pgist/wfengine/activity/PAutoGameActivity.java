package org.pgist.wfengine.activity;

import java.util.List;
import java.util.Stack;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.EnvironmentInOuts;
import org.pgist.wfengine.RunningContext;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.WorkflowInfo;
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
    
    
    private static final long serialVersionUID = 4257849359571840340L;
    
    
    protected String taskName = null;
    
    
    public PAutoGameActivity() {
        type = TYPE_PAUTOGAME;
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
        newGame.setName(getName());
        newGame.setDescription(getDescription());
        newGame.setTaskName(getTaskName());
        newGame.getDeclaration().duplicate(getDeclaration());
        
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
        WorkflowTask task = null;
        
        try {
            task = context.getRegistry().getTask(getTaskName());
            if (task!=null) {
                WorkflowInfo info = new WorkflowInfo(context.getWorkflow(), context, this);
                EnvironmentInOuts inouts = new EnvironmentInOuts(context, getDeclaration());
                task.execute(info, inouts);
                context.merge(inouts);
            }
            
            context.addRunningActivity(getNext());
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            context.getHaltingActivities().add(this);
            return false;
        }
    }//doExecute()
    
    
    public void setFuture(List futures) {
        getNext().setFuture(futures);
    }//setFuture()
    
    
}//class PAutoGameActivity
