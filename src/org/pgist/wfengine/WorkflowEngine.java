package org.pgist.wfengine;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.pgist.wfengine.activity.GroupActivity;
import org.pgist.wfengine.activity.MeetingActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.PManualGameActivity;
import org.pgist.wfengine.activity.PMethodActivity;
import org.pgist.wfengine.activity.SituationActivity;
import org.pgist.wfengine.parser.DeclarationParser;
import org.pgist.wfengine.parser.EnvironmentParser;
import org.pgist.wfengine.parser.MeetingParser;
import org.pgist.wfengine.parser.PGameParser;
import org.pgist.wfengine.parser.PMethodParser;
import org.pgist.wfengine.parser.SituationParser;
import org.pgist.wfengine.util.Utils;
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
    }
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public void importTemplates(Document document) throws Exception{
        importTemplates(document.getRootElement());
    }//importTemplates()
    
    
    public void importTemplates(Element root) throws Exception{
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
        
        for (Element element : (List<Element>) (root.selectNodes("pgames/pgame"))) {
            PGameActivity pgame = pgameParser.parse(element);
            engineDAO.saveActivity(pgame);
        }//for
        
        pmethodParser.setPgames(engineDAO.getTemplatePGames());
        
        for (Element element : (List<Element>) (root.selectNodes("pmethods/pmethod"))) {
            PMethodActivity pmethod = pmethodParser.parse(element);
            engineDAO.saveActivity(pmethod);
        }//for
        
        meetingParser.setPmethods(engineDAO.getTemplatePMethods());
        
        for (Element element : (List<Element>) (root.selectNodes("meetings/meeting"))) {
            MeetingActivity meeting = meetingParser.parse(element);
            engineDAO.saveActivity(meeting);
        }//for
        
        situationParser.setMeetings(engineDAO.getTemplateMeetings());
        
        for (Element element : (List<Element>) (root.selectNodes("situations/situation"))) {
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
    
    
    public Workflow createWorkflow(Long situationId, String name, String description) throws Exception {
        Workflow workflow = engineDAO.createWorkflow(situationId, name, description);
        workflow.setEngine(this);
        
        return workflow;
    }//createWorkflow()
    
    
    public void startWorkflow(Long workflowId) throws Exception {
        /*
         * Load and lock the specified workflow object
         */
        Workflow workflow = engineDAO.lockWorkflowById(workflowId);
        
        workflow.setEngine(this);
        
        workflow.start();
        
        engineDAO.saveWorkflow(workflow);
    }//startWorkflow()
    
    
    public Workflow createStartWorkflow(Long situationId) throws Exception {
        /*
         * Load and lock the specified workflow object
         */
        Workflow workflow = engineDAO.lockWorkflowById(situationId);
        
        workflow.setEngine(this);
        
        workflow.start();
        
        engineDAO.saveWorkflow(workflow);
        
        return workflow;
    }//createStartWorkflow()
    
    
    public void executeWorkflow(Long workflowId, Long contextId, Long activityId) throws Exception {
        /*
         * Load and lock the specified workflow object
         */
        Workflow workflow = engineDAO.lockWorkflowById(workflowId);
        
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
    
    
    public Workflow getWorkflowById(Long workflowId) throws Exception {
        Workflow workflow = engineDAO.getWorkflowById(workflowId);
        workflow.setEngine(this);
        return workflow;
    }//getWorkflowById()


    public Map getURL(Long workflowId, Long contextId, Long activityId) throws Exception {
        Map results = new HashMap();
        
        /*
         * get workflow object
         */
        Workflow workflow = getWorkflowById(workflowId);
        
        if (workflow==null) throw new WorkflowException("cannot find workflow with id "+workflowId);
        
        workflow.setEngine(this);
        
        results.put("workflow", workflow);
        
        /*
         * get context object
         */
        RunningContext context = engineDAO.getContextById(contextId);
        
        if (context==null) throw new WorkflowException("cannot find context with id "+contextId);
        
        results.put("context", context);
        
        GroupActivity group = context.getParent().getGroup();
        //Here I have to preload group activity, or else the session will be unavalible!
        group.getDescription();
        results.put("meeting", group);
        results.put("histories", context.getHistories());
        
        List<PManualGameActivity> futures = context.getFutureActivities();
        results.put("futures", futures);
        
        /*
         * get activity object
         */
        Activity activity = engineDAO.getActivityById(activityId);
        
        if (activity==null) throw new WorkflowException("cannot find activity with id "+activityId);
        
        results.put("activity", activity);
        
        Activity.NextStepInfo nsi = new Activity.NextStepInfo(activity, "all");
        results.put("next", workflow.getSituation().getNextStep(nsi));
        
        Set parallel = new HashSet();
        parallel.addAll(context.getRunningActivities());
        parallel.remove(activity);
        results.put("parallel", parallel);
        
        /*
         * get serialized manual pgame activities
         */
        SortedSet<Activity> serial = context.getSerialActivities();
        results.put("serial", serial);
        
        /*
         * TODO: check validity
         */
        
        if (activity.getType()==Activity.TYPE_PMANUALGAME) {
            PManualGameActivity manual = (PManualGameActivity) Utils.narrow(activity);
            if (context.getHistories().contains(activity)) {
                //history activity
                results.put("link", manual.getHistoryLink()+"workflowId="+workflowId+"&contextId="+contextId+"&activityId="+activityId);
            } else {
                results.put("link", manual.getLink(context)+"workflowId="+workflowId+"&contextId="+contextId+"&activityId="+activityId);
            }
        }
        
        results.put("status", !context.getHistories().contains(activity));
        
        return results;
    }//getURL()


    public void setEnvVars(Long workflowId, Long contextId, Long activityId, EnvironmentHandler handler) throws Exception {
        if (handler==null) throw new Exception("environment handler is null");
        
        /*
         * get workflow object
         */
        Workflow workflow = getWorkflowById(workflowId);
        
        if (workflow==null) throw new WorkflowException("cannot find workflow with id "+workflowId);
        
        workflow.setEngine(this);
        
        /*
         * get context object
         */
        RunningContext context = engineDAO.getContextById(contextId);
        
        if (context==null) throw new WorkflowException("cannot find context with id "+contextId);
        
        /*
         * get activity object
         */
        Activity activity = engineDAO.getActivityById(activityId);
        
        if (activity==null) throw new WorkflowException("cannot find running activity with id "+activityId);
        
        /*
         * TODO: check validity
         */
        
        /*
         * handle environment variables
         */
        if (activity.getType()==Activity.TYPE_PMANUALGAME) {
            PManualGameActivity manual = (PManualGameActivity) Utils.narrow(activity);
            EnvironmentInOuts inouts = new EnvironmentInOuts(context, manual.getDeclaration());
            handler.handleEnvVars(inouts);
            context.merge(inouts);
            engineDAO.saveWorkflow(workflow);
        } else {
            throw new Exception("environment variable is not supported in this activity.");
        }
    }//setEnvVars()
    
    
    public List<List<Activity>> getAgenda(Long workflowId) throws Exception {
        /*
         * get workflow object
         */
        Workflow workflow = getWorkflowById(workflowId);
        
        if (workflow==null) throw new WorkflowException("cannot find workflow with id "+workflowId);
        
        workflow.setEngine(this);
        
        return workflow.getAgenda();
    }//getAgenda()


    public void updateAgenda(Long workflowId, Map<Long, Date> beginTimes, Map<Long, Date> endTimes) throws Exception {
        /*
         * get workflow object
         */
        Workflow workflow = getWorkflowById(workflowId);
        
        if (workflow==null) throw new WorkflowException("cannot find workflow with id "+workflowId);
        
        workflow.setEngine(this);
        
        for (Map.Entry<Long, Date> entry : beginTimes.entrySet()) {
            Long id = entry.getKey();
            Date begin = beginTimes.get(id);
            Date end = endTimes.get(id);
            
            engineDAO.updateAgenda(id, begin, end);
        }//for
    }//updateAgenda()


    public void setOpenAccess(Long workflowId, boolean openAccess) throws Exception {
        /*
         * get workflow object
         */
        Workflow workflow = getWorkflowById(workflowId);
        
        if (workflow==null) throw new WorkflowException("cannot find workflow with id "+workflowId);
        
        workflow.setEngine(this);
        
        workflow.setOpenAccess(openAccess);
        
        engineDAO.saveWorkflow(workflow);
    }//setOpenAccess()


    
    
    
    
    
    
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
