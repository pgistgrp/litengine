package org.pgist.wfengine.activity;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;

import org.pgist.wfengine.Activity;
import org.pgist.wfengine.EnvironmentInOuts;
import org.pgist.wfengine.Linkable;
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
public class PManualGameActivity extends PGameActivity implements Linkable {
    
    
    private static final long serialVersionUID = -3825305313202911455L;
    
    
    protected String actionName = null;
    
    protected String access = "all";
    
    protected String historyLink;
    
    protected long time;
    
    protected Date beginTime;
    
    protected Date endTime;
    
    protected long extension;
    
    /**
     * Whether this activity revisitable.
     * When a revisitable activity finishes running, it will add itself into the running history.
     */
    protected boolean revisitable = false;
    
    
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
     * @hibernate.property column="history_link" length="1024"
     */
    public String getHistoryLink() {
        return historyLink;
    }


    public void setHistoryLink(String historyLink) {
        this.historyLink = historyLink;
    }


    /**
     * @return
     * 
     * @hibernate.property column="time" not-null="true"
     */
    public long getTime() {
        return time;
    }


    public void setTime(long time) {
        this.time = time;
    }


    /**
     * @return
     * 
     * @hibernate.property column="begin_time"
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
     * @hibernate.property column="end_time"
     */
    public Date getEndTime() {
        return endTime;
    }


    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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


    /**
     * @return
     * 
     * @hibernate.property column="revisitable" not-null="true"
     */
    public boolean isRevisitable() {
        return revisitable;
    }


    public void setRevisitable(boolean revisitable) {
        this.revisitable = revisitable;
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
        newGame.setTitle(getTitle());
        newGame.setDescription(getDescription());
        newGame.setActionName(getActionName());
        newGame.setAccess(getAccess());
        newGame.setTime(getTime());
        newGame.setExtension(getExtension());
        newGame.getDeclaration().duplicate(getDeclaration());
        newGame.setRevisitable(isRevisitable());
        
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
        
        if (getTime()>0) {
            /*
             * an absolute timestamp is setup
             */
            context.addJob(this, getTime());
        } else if (getExtension()>0) {
            /*
             * an relative time extension is setup
             */
            context.addJob(this, System.currentTimeMillis() + getExtension());
        }
        
        /*
         * if neither time nor extension is greater than 0, it's not a time dependent activity
         */
        
        return false;
    }//doExecute()
    
    
    protected void doDeActivate(RunningContext context) {
        /*
         * Record the history
         */
        System.out.println("  ---> "+isRevisitable());
        
        if (isRevisitable()) {
            context.getHistories().add(this);
        }
        
        setHistoryLink(getLink(context));
        
        context.addRunningActivity(getNext());
    }//doDeActivate()


    public String getLink(RunningContext context) {
        EnvironmentInOuts inouts = new EnvironmentInOuts(getRunningContext(), getDeclaration());
        
        String uri = context.getRegistry().getAction(getActionName()) + "?";
        
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String> entry : getDeclaration().getIns().entrySet()) {
            params.append(entry.getKey());
            params.append('=');
            Object value = inouts.getValue(entry.getValue());
            if (value!=null) params.append(value.toString());
            params.append('&');
        }//for
        
        return uri + params.toString();
    }//getLink()
    
    
    public void setFuture(List futures, Set embedding) {
        futures.add(this);
        getNext().setFuture(futures, embedding);
    }//setFuture()
    
    
    @Override
    public void getAgenda(List activities) {
        activities.add(this);
        getNext().getAgenda(activities);
    }//getAgenda
    
    
    @Override
    public Activity getNextStep(NextStepInfo nsi) {
        if (this==nsi.getActivity()) nsi.setFlag();
        
        if (nsi.isFlag() && this!=nsi.getActivity() && getAccess().equals(nsi.getAccess())) return this;
        
        return getNext().getNextStep(nsi);
    }//getNextStep()
    
    
    @Override
    public void setSerial(SortedSet set) {
        set.add(this);
        
        getNext().setSerial(set);
    }//setSerial()


}//class PManualGameActivity
