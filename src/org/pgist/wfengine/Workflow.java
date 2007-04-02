package org.pgist.wfengine;

import java.io.Serializable;
import java.util.Date;

import org.pgist.wfengine.activity.SituationActivity;
import org.quartz.Scheduler;


/**
 * The Workflow is the working instance for a workflow.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_workflow"
 */
public class Workflow implements Serializable {
    
    
    private static final long serialVersionUID = -8038860928226339011L;
    
    public static final int STATUS_NEW       = 0;
    
    public static final int STATUS_RUNNING   = 100;
    
    public static final int STATUS_FINISHED  = 200;
    
    public static final int STATUS_CANCELLED = -1;
    
    
    private Long id;
    
    private SituationActivity situation;
    
    private int status = STATUS_NEW;
    
    private Date beginTime;
    
    private Date endTime;
    
    private WorkflowEngine engine;
    
    
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
     * @hibernate.many-to-one column="situation_id" cascade="all"
     */
    public SituationActivity getSituation() {
        return situation;
    }


    public void setSituation(SituationActivity situation) {
        this.situation = situation;
    }


    /**
     * @return
     * 
     * @hibernate.property not-null="true"
     */
    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
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
    
    
    public WorkflowEngine getEngine() {
        return engine;
    }


    public void setEngine(WorkflowEngine engine) {
        this.engine = engine;
    }


    /*
     * -------------------------------------------------------------------
     */
    
    
    /**
     * Package Accessible.
     * Start this workflow.
     * This method can only be executed exactly ONCE!
     * 
     * @throws Exception
     */
    public void start() throws Exception {
        /*
         * First check if this workflow already finished, cancelled or running
         */
        switch (status) {
            case STATUS_RUNNING:
                throw new WorkflowException("flow is in running state");
            case STATUS_FINISHED:
                throw new WorkflowException("flow is in finished state");
            case STATUS_CANCELLED:
                throw new WorkflowException("flow is in cancelled state");
        }//switch
        
        beginTime = new Date();
        
        /*
         * bootstrap
         */
        situation.getContext().getStack().push(situation);
        
        status = STATUS_RUNNING;
        
        situation.getContext().execute();
    }//start()
    
    
    /**
     * Package Accessible.
     * Cancel this workflow.
     * This method can only be executed exactly ONCE!
     * 
     * @throws Exception
     */
    void cancel() throws Exception {
        /*
         * First check if this workflow already finished or cancelled
         */
        switch (status) {
            case STATUS_FINISHED:
                throw new WorkflowException("flow is in finished state");
            case STATUS_CANCELLED:
                throw new WorkflowException("flow is in cancelled state");
        }//switch
        
        endTime = new Date();
        
        status = STATUS_CANCELLED;
    }//cancel()


    /**
     * Package Accessible.
     * Finish this workflow.
     * This method can only be executed exactly ONCE!
     * 
     * @throws Exception
     */
    void finish() throws Exception {
        /*
         * First check if this workflow already finished or cancelled
         */
        switch (status) {
            case STATUS_NEW:
                throw new WorkflowException("flow is in new state");
            case STATUS_CANCELLED:
                throw new WorkflowException("flow is in cancelled state");
        }//switch
        
        endTime = new Date();
        
        status = STATUS_FINISHED;
    }//finish()


    public void execute(RunningContext context, Activity activity) throws Exception {
        //Check if the given context is in the this workflow
        RunningContext parent = context;
        while (parent.getParent()!=null) parent = parent.getParent();
        if (parent!=getSituation().getContext()) throw new WorkflowException("the given context is not in the given workflow");
        
        //run the given activity in the given context
        context.execute(activity);
    }//execute()
    
    
}//class Workflow
