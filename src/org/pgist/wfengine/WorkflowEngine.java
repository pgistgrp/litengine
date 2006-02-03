package org.pgist.wfengine;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.pgist.wfengine.activity.PActActivity;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;


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
    
    private WorkflowDAO workflowDAO;
    
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
    
    
    public void setWorkflowDAO(WorkflowDAO workflowDAO) {
        this.workflowDAO = workflowDAO;
    }
    
    
    public void setTemplateParser(TemplateParser templateParser) {
        this.templateParser = templateParser;
    }


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
            workflow = workflowDAO.getWorkflow(id, false, false);
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
                if (workflowDAO.getPActActivityByRefId(pact.getRefId())!=null) throw new Exception("Another PActActivity has this refid: "+pact.getRefId());
                workflowDAO.saveActivity(pact);
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
        return workflowDAO.getTemplate(id);
    }//getTemplate()
    
    
    /**
     * Spawn from the given process, generate a new workflow instance
     * @param process
     * @return
     */
    public Workflow spawn(Template template) {
        Workflow workflow = new Workflow();
        
        WorkflowTracker tracker = new WorkflowTracker();
        workflow.setTracker(tracker);
        tracker.setWorkflow(workflow);
        
        WorkflowEnvironment env = new WorkflowEnvironment();
        workflow.setEnv(env);
        env.setWorkflow(workflow);
        
        FlowPiece piece = template.spawn();
        
        workflow.setDefinition((Activity) piece.getHead());
        
        try {
            workflow.execute();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return workflow;
    }//spawn()
    
    
    public void saveWorkflow(Workflow workflow) {
        workflowDAO.saveWorkflow(workflow);
    }//saveWorkflow()


}//class WorkflowEngine
