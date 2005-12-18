package org.pgist.wfengine;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

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
    
    
    public WorkflowEngine() {
        CacheManager manager = CacheManager.create();
        workflowCache = new Cache("workflow", 50, true, false, 2*3600, 20*60);
        manager.addCache(workflowCache);
    }
    
    
    public void setWorkflowDAO(WorkflowDAO workflowDAO) {
        this.workflowDAO = workflowDAO;
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
     * Client programs use this method to add new definition of processes
     * 
     * @param stream
     * @return
     * @throws Exception
     */
    public List addProcess(WFDefinitionParser parser) throws Exception{
        parser.parse();
        List list = parser.getProcesses();
        if (list!=null && list.size()>0) {
            for (Iterator iter=list.iterator(); iter.hasNext(); ) {
                WFProcess process = (WFProcess) iter.next();
                workflowDAO.saveProcess(process);
            }//for iter
        }
        return list;
    }//addProcess()
    
    
    public List addProcess(InputStream stream) throws Exception {
        WFDefinitionParser parser = new WFDefinitionParser(stream);
        return addProcess(parser);
    }//addProcess()
    
    
    public List addProcess(File file) throws Exception {
        WFDefinitionParser parser = new WFDefinitionParser(file);
        return addProcess(parser);
    }//addProcess()


    public List addProcess(URL url) throws Exception {
        WFDefinitionParser parser = new WFDefinitionParser(url);
        return addProcess(parser);
    }//addProcess()


    public List addProcess(String xml) throws Exception {
        WFDefinitionParser parser = new WFDefinitionParser(new File(xml));
        return addProcess(parser);
    }//addProcess()


}//class WorkflowEngine
