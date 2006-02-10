package org.pgist.wfengine;

import java.io.InputStream;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.pgist.wfengine.activity.PActActivity;
import org.pgist.wfengine.activity.TerminateActivity;


/**
 * The Workflow Engine.
 * 
 * Workflow Engine is the helper class for clients to operate workflow.
 * 
 * @author kenny
 *
 */
public class WorkflowEngine {
    
    
    private Cache workflowCache = null;
    
    private WorkflowEngineDAO engineDAO;
    
    private TemplateParser templateParser;
    
    
    public WorkflowEngine() {
        try {
            CacheManager manager = CacheManager.getInstance();
            workflowCache = manager.getCache("workflow");
            if (workflowCache==null) {
                workflowCache = new Cache("workflow", 50, true, false, 2*3600, 20*60);
                manager.addCache(workflowCache);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void setEngineDAO(WorkflowEngineDAO engineDAO) {
        this.engineDAO = engineDAO;
    }
    
    
    public void setTemplateParser(TemplateParser templateParser) {
        this.templateParser = templateParser;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    /**
     * Client programs use this method to get the workflow instance.
     * 
     * @param id
     * @return
     */
    public Workflow getWorkflow(Long id) throws Exception {
        Workflow workflow = null;
        
        //Check if the requested flow is already in the cache
        Element element = workflowCache.get(id);
        if (element!=null) {
            workflow = (Workflow) element.getValue();
        } else {
            workflow = engineDAO.getWorkflow(id, false, false);
            workflowCache.put(new Element(id, workflow));
        }
        
        return workflow;
    }//getWorkflow()
    
    
    /**
     * Client programs use this method to get the running activities in specified workflow instance.
     * 
     * @param id
     * @return
     */
    public List getRunningActivities(Long workflowId) {
        return null;
    }//getRunningActivities()
    
    
    /**
     * Client programs use this method to add new definition of pActs
     * 
     * @param stream
     * @return
     * @throws Exception
     */
    public List addPActs(InputStream stream) throws Exception {
        PActParser parser = new PActParser(stream);
        parser.parse();
        List list = parser.getPActs();
        if (list!=null && list.size()>0) {
            for (int i=0,n=list.size(); i<n; i++) {
                PActActivity pact = (PActActivity) list.get(i);
                if (engineDAO.getPActActivityByRefId(pact.getRefId())!=null) throw new Exception("Another PActActivity has this refid: "+pact.getRefId());
                engineDAO.saveActivity(pact);
            }//for iter
        }
        return list;
    }//addPActs()
    
    
    /**
     * Client programs use this method to add new definition of templates
     * 
     * @param stream
     * @return
     * @throws
     */
    public List addTemplates(InputStream stream) throws Exception{
        return templateParser.parse(stream);
    }//addTemplates()
    
    
    public Template getTemplate(Long id)throws Exception {
        return engineDAO.getTemplate(id);
    }//getTemplate()
    
    
    public List getSituationTemplates() throws Exception {
        return engineDAO.getTemplates(Template.TYPE_SITUATION);
    }//getSituationTemplates()
    
    
    /**
     * Spawn from the given process, generate a new workflow instance
     * @param process
     * @return
     */
    public Workflow spawn(Long id) throws Exception {
        Template template = getTemplate(id);
        
        Workflow workflow = new Workflow();
        
        RunningContext context = new RunningContext();
        workflow.setContext(context);
        
        WorkflowEnvironment env = new WorkflowEnvironment();
        workflow.setEnv(env);
        env.setWorkflow(workflow);
        
        FlowPiece piece = template.spawn();
        SingleOut tail = piece.getTail();
        TerminateActivity terminateActivity = new TerminateActivity();
        terminateActivity.setCount(0);
        terminateActivity.setExpression(0);
        terminateActivity.setPrev((Activity) tail);
        tail.setNext(terminateActivity);
        
        workflow.setDefinition((Activity) piece.getHead());
        
        workflow.initialize();
        
        engineDAO.saveWorkflow(workflow);
        
        return workflow;
    }//spawn()
    
    
    public void saveWorkflow(Workflow workflow) throws Exception {
        engineDAO.saveWorkflow(workflow);
    }//saveWorkflow()
    
    
    public void executeWorkflow(Workflow workflow) throws Exception {
        workflow.execute();
        engineDAO.saveWorkflow(workflow);
    }//executeWorkflow()
    
    
}//class WorkflowEngine
