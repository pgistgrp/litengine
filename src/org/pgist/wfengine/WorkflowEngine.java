package org.pgist.wfengine;

import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;


/**
 * The Workflow Engine.
 * 
 * Workflow Engine is the helper class for clients to operate workflow.
 * 
 * @author kenny
 *
 */
public class WorkflowEngine implements BeanFactoryAware {
    
    
    private BeanFactory beanFactory;
    
    
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }//setBeanFactory()
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    private Cache workflowCache = null;
    
    private WorkflowEngineDAO engineDAO;
    
    
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
    
    
    public void saveWorkflow(Workflow workflow) throws Exception {
        engineDAO.saveWorkflow(workflow);
    }//saveWorkflow()
    
    
    public void executeWorkflow(Workflow workflow) throws Exception {
        workflow.execute(beanFactory);
        engineDAO.saveWorkflow(workflow);
    }//executeWorkflow()


}//class WorkflowEngine
