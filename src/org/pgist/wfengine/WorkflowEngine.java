package org.pgist.wfengine;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.pgist.wfengine.activity.MeetingActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.PMethodActivity;
import org.pgist.wfengine.activity.SituationActivity;
import org.pgist.wfengine.parser.DeclarationParser;
import org.pgist.wfengine.parser.EnvironmentParser;
import org.pgist.wfengine.parser.MeetingParser;
import org.pgist.wfengine.parser.PGameParser;
import org.pgist.wfengine.parser.PMethodParser;
import org.pgist.wfengine.parser.SituationParser;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;


/**
 * The Workflow Engine.
 * 
 * Workflow Engine is the helper class for clients to operate workflow.
 * 
 * @author kenny
 */
public class WorkflowEngine {
    
    
    private WorkflowEngineDAO engineDAO;
    
    private WorkflowTaskRegistry registry;
    
    private Scheduler scheduler;
    
    
    public void setEngineDAO(WorkflowEngineDAO engineDAO) {
        this.engineDAO = engineDAO;
    }
    
    
    public WorkflowTaskRegistry getRegistry() {
        return registry;
    }


    public void setRegistry(WorkflowTaskRegistry registry) {
        this.registry = registry;
    }
    
    
    public Scheduler getScheduler() {
        return scheduler;
    }


    public void setScheduler(Scheduler scheduler) throws SchedulerException {
        this.scheduler = scheduler;
        scheduler.getContext().put("engine", this);
    }
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public void importTemplates(Document document) throws Exception{
        DeclarationParser declParser = new DeclarationParser();
        EnvironmentParser envParser = new EnvironmentParser();
        PGameParser pgameParser = new PGameParser();
        PMethodParser pmethodParser = new PMethodParser();
        MeetingParser meetingParser = new MeetingParser();
        SituationParser situationParser = new SituationParser();
        
        pgameParser.setDeclParser(declParser);
        pmethodParser.setDeclParser(declParser);
        pmethodParser.setEnvParser(envParser);
        meetingParser.setDeclParser(declParser);
        meetingParser.setEnvParser(envParser);
        situationParser.setDeclParser(declParser);
        situationParser.setEnvParser(envParser);
        
        for (Element element : (List<Element>) (document.selectNodes("//templates/pgames/pgame"))) {
            PGameActivity pgame = pgameParser.parse(element);
            engineDAO.saveActivity(pgame);
        }//for
        
        pmethodParser.setPgames(engineDAO.getTemplatePGames());
        
        for (Element element : (List<Element>) (document.selectNodes("//templates/pmethods/pmethod"))) {
            PMethodActivity pmethod = pmethodParser.parse(element);
            engineDAO.saveActivity(pmethod);
        }//for
        
        meetingParser.setPmethods(engineDAO.getTemplatePMethods());
        
        for (Element element : (List<Element>) (document.selectNodes("//templates/meetings/meeting"))) {
            MeetingActivity meeting = meetingParser.parse(element);
            engineDAO.saveActivity(meeting);
        }//for
        
        situationParser.setMeetings(engineDAO.getTemplateMeetings());
        
        for (Element element : (List<Element>) (document.selectNodes("//templates/situations/situation"))) {
            SituationActivity situation = situationParser.parse(element);
            engineDAO.saveActivity(situation);
        }//for
        
    }//importTemplates()
    
    
    public List<SituationActivity> getTemplateSituations() throws Exception {
        return engineDAO.getTemplateSituations();
    }//getTemplateSituations()
    
    
    public List<Workflow> getNewWorkflows() throws Exception {
        return engineDAO.getRunningWorkflows(Workflow.STATUS_NEW);
    }//getNewWorkflows()
    
    
    public List<Workflow> getRunningWorkflows() throws Exception {
        return engineDAO.getRunningWorkflows(Workflow.STATUS_RUNNING);
    }//getWorkflows()
    
    
    public List<Workflow> getFinishedWorkflows() throws Exception {
        return engineDAO.getRunningWorkflows(Workflow.STATUS_FINISHED);
    }//getWorkflows()
    
    
    public Workflow createWorkflow(Long situationId) throws Exception {
        Workflow workflow = engineDAO.createWorkflow(situationId);
        workflow.setEngine(this);
        
        return workflow;
    }//createWorkflow()
    
    
    public void startWorkflow(Long workflowId) throws Exception {
        Workflow workflow = engineDAO.getWorkflowById(workflowId);
        workflow.setEngine(this);
        
        workflow.start();
        
        engineDAO.saveWorkflow(workflow);
    }//startWorkflow()
    
    
    public void executeWorkflow(Long workflowId, Long contextId, Long activityId) throws Exception {
        Workflow workflow = engineDAO.getWorkflowById(workflowId);
        workflow.setEngine(this);
        
        if (workflow==null) throw new WorkflowException("cannot find workflow with id "+workflowId);
        switch (workflow.getStatus()) {
            case Workflow.STATUS_NEW:
                throw new WorkflowException("workflow with id "+workflowId+" is not started yet.");
            case Workflow.STATUS_FINISHED:
                throw new WorkflowException("workflow with id "+workflowId+" is already finished.");
            case Workflow.STATUS_CANCELLED:
                throw new WorkflowException("workflow with id "+workflowId+" is already cancelled.");
        }//switch
        
        RunningContext context = engineDAO.getContextById(contextId);
        
        if (context==null) throw new WorkflowException("cannot find context with id "+contextId);
        
        Activity activity = engineDAO.getActivityById(activityId);
        
        if (activity==null) throw new WorkflowException("cannot find activity with id "+activityId);
        
        workflow.execute(context, activity);
        
        engineDAO.saveWorkflow(workflow);
    }//executeWorkflow()
    
    
    
    
    
    
    
    
    /**
     * Client programs use this method to get the running activities in specified workflow instance.
     * 
     * @param id
     * @return
     */
    public List getRunningActivities(Long workflowId) {
        return null;
    }//getRunningActivities()
    
    
    public void saveWorkflow(Workflow workflow) throws Exception {
        engineDAO.saveWorkflow(workflow);
    }//saveWorkflow()
    
    
    public void executeWorkflow(Workflow workflow) throws Exception {
        engineDAO.saveWorkflow(workflow);
    }//executeWorkflow()


    public void saveActivity(Activity activity) throws Exception {
        engineDAO.saveActivity(activity);
    }


}//class WorkflowEngine
