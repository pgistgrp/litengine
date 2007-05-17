package org.pgist.wfengine.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.WebContextFactory;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.WorkflowEngine;
import org.pgist.wfengine.activity.SituationActivity;


/**
 * DWR Agent class for Workflow Management.
 * 
 * @author kenny
 */
public class WorkflowAgent {
    
    
    private WorkflowEngine engine;
    
    
    public void setEngine(WorkflowEngine engine) {
        this.engine = engine;
    }
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    /**
     * Get template list. In LitEngine, a template is actually an instance of SituationActivity.
     * 
     * @return A map contains:
     *     <ul>
     *         <li>successful - boolean, whether the operation succeed</li>
     *         <li>reason - string, the reason why it fails</li>
     *         <li>
     *           html - string, html source segment generated by "/WEB-INF/jsp/workflow/wf_templates.jsp". In this page the following variables are avaiable:<br>
     *             <ul>
     *               <li>tempaltes - a list of SituationActivity objects</li>
     *             </ul>
     *         </li>
     *     </ul>
     */
    public Map getTemplates(HttpServletRequest request) {
        Map results = new HashMap();
        results.put("successful", false);
        
        try {
            /*
             * Get all the templates
             */
            Collection<SituationActivity> templates = engine.getTemplateSituations();
            
            request.setAttribute("templates", templates);
            
            results.put("html", WebContextFactory.get().forwardToString("/WEB-INF/jsp/workflow/wf_templates.jsp"));
            
            results.put("successful", true);
        } catch (Exception e) {
            e.printStackTrace();
            results.put("reason", e.getMessage());
        }
        
        return results;
    }//getTemplates()
    
    
    /**
     * Create a workflow instance from the given template.
     * 
     * @param params A map contains:
     *     <ul>
     *         <li>situationId - int, id of a SituationActivity object</li>
     *     </ul>
     * 
     * @return A map contains:
     *     <ul>
     *         <li>successful - boolean, whether the operation succeed</li>
     *         <li>reason - string, the reason why it fails</li>
     *         <li>workflowId - int, id of the newly created Workflow object</li>
     *     </ul>
     */
    public Map createInstance(Map params) {
        Map results = new HashMap();
        results.put("successful", false);
        
        try {
            Long situationId = new Long((String) params.get("situationId"));
            
            Workflow workflow = engine.createWorkflow(situationId);
            
            results.put("workflowId", workflow.getId());
            results.put("successful", true);
        } catch (Exception e) {
            results.put("reason", e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }//createInstance()
    
    
    /**
     * Get situation list.
     * 
     * @param params A map contains:
     *     <ul>
     *         <li>type - string, type of workflows. ["running" | "all"]. Default is "all"</li>
     *     </ul>
     * 
     * @return A map contains:
     *     <ul>
     *         <li>successful - boolean, whether the operation succeed</li>
     *         <li>reason - string, the reason why it fails</li>
     *         <li>instanceTotal - total number of instances (runningWorkflows + finishedWorkflows)</li>
     *         <li>runningTotal - total number of running instances</li>
     *         <li>finishedTotal - total number of running instances</li>
     *         <li>instanceId - if instanceTotal=1, this is the id of that instance</li>
     *         <li>
     *           html - string, html source segment generated by "/WEB-INF/jsp/workflow/wf_workflows.jsp". In this page the following variables are avaiable:<br>
     *             <ul>
     *               <li>runningWorkflows - a list of running Workflow objects</li>
     *               <li>finishedWorkflows - a list of finished Workflow objects</li>
     *               <li>newWorkflows - a list of new Workflow objects</li>
     *             </ul>
     *         </li>
     *     </ul>
     */
    public Map getWorkflows(HttpServletRequest request, Map params) {
        Map results = new HashMap();
        results.put("successful", false);
        
        try {
            Collection runningWorkflows = engine.getRunningWorkflows();
            request.setAttribute("runningWorkflows", runningWorkflows);
            results.put("instanceTotal", runningWorkflows.size());
            
            Collection finishedWorkflows = engine.getFinishedWorkflows();
            request.setAttribute("finishedWorkflows", finishedWorkflows);
            results.put("instanceTotal", finishedWorkflows.size());
            
            if ("all".equalsIgnoreCase((String) params.get("type"))) {
                /*
                 * Get new workflows
                 */
                Collection newWorkflows = engine.getNewWorkflows();
                request.setAttribute("newWorkflows", newWorkflows);
            }
            
            int instanceTotal = runningWorkflows.size() + finishedWorkflows.size();
            results.put("instanceTotal", instanceTotal);
            
            if (instanceTotal==1) {
                Workflow workflow = null;
                if (runningWorkflows.size()>0) {
                    workflow = (Workflow) runningWorkflows.iterator().next();
                } else {
                    workflow = (Workflow) finishedWorkflows.iterator().next();
                }
                results.put("instanceId", workflow.getId());
            }
            
            results.put("html", WebContextFactory.get().forwardToString("/WEB-INF/jsp/workflow/wf_workflows.jsp"));
            
            results.put("successful", true);
        } catch (Exception e) {
            e.printStackTrace();
            results.put("reason", e.getMessage());
        }
        
        return results;
    }//getWorkflows()
    
    
    /**
     * Start the given workflow instance.
     * 
     * @param params A map contains:
     *     <ul>
     *         <li>workflowId - int, id of a Workflow object</li>
     *     </ul>
     * 
     * @return A map contains:
     *     <ul>
     *         <li>successful - boolean, whether the operation succeed</li>
     *         <li>reason - string, the reason why it fails</li>
     *     </ul>
     */
    public Map startWorkflow(Map params) {
        Map results = new HashMap();
        results.put("successful", false);
        
        try {
            Long workflowId = new Long((String) params.get("workflowId"));
            
            engine.startWorkflow(workflowId);
            
            results.put("successful", true);
        } catch (Exception e) {
            e.printStackTrace();
            results.put("reason", e.getMessage());
        }
        
        return results;
    }//startWorkflow()
    
    
    /**
     * Get the entry page for the given workflow instance.
     * 
     * @param params A map contains:
     *     <ul>
     *         <li>workflowId - int, id of a Workflow object</li>
     *     </ul>
     * 
     * @return A map contains:
     *     <ul>
     *         <li>successful - boolean, whether the operation succeed</li>
     *         <li>reason - string, the reason why it fails</li>
     *         <li>
     *           html - string, html source segment generated by "/WEB-INF/jsp/workflow/wf_workflow.jsp". In this page the following variables are avaiable:<br>
     *             <ul>
     *               <li>workflows - a list of Workflow objects</li>
     *             </ul>
     *         </li>
     *     </ul>
     */
    public Map getWorkflow(HttpServletRequest request, Map params) {
        Map results = new HashMap();
        results.put("successful", false);
        
        try {
            Long workflowId = new Long((String) params.get("workflowId"));
            
            Workflow workflow = engine.getWorkflowById(workflowId);
            
            System.out.println("workflowId ---> "+workflowId);
            System.out.println("workflow ---> "+workflow);
            
            request.setAttribute("workflow", workflow);
            
            results.put("html", WebContextFactory.get().forwardToString("/WEB-INF/jsp/workflow/wf_workflow.jsp"));
            
            results.put("successful", true);
        } catch (Exception e) {
            e.printStackTrace();
            results.put("reason", e.getMessage());
        }
        
        return results;
    }//getWorkflow()
    
    
    /**
     * Execute the workflow to go over one specific activity.
     * 
     * @param params A map contains:
     *     <ul>
     *         <li>workflowId - int, id of a Workflow object</li>
     *         <li>contextId - int, id of a RunningContext object</li>
     *         <li>activityId - int, id of an Activity object</li>
     *         <li>nextId - int, id of an Activity object. (Required for some activities.)</li>
     *     </ul>
     * 
     * @return A map contains:
     *     <ul>
     *         <li>successful - boolean, whether the operation succeed</li>
     *         <li>reason - string, the reason why it fails</li>
     *         <li>contextId - int, id of a RunningContext object</li>
     *         <li>activityId - int, id of a Activity object</li>
     *     </ul>
     */
    public Map nextStep(Map params) {
        Map results = new HashMap();
        results.put("successful", false);
        
        try {
            Long workflowId = new Long((String) results.get("workflowId"));
            Long contextId = new Long((String) results.get("contextId"));
            Long activityId = new Long((String) results.get("activityId"));
            
            String nextIdStr = (String) results.get("nextId");
            if (nextIdStr==null || nextIdStr.length()==0) {
                engine.executeWorkflow(workflowId, contextId, activityId);
            } else {
                Long nextId = new Long((String) results.get("nextIdStr"));
                //engine.executeWorkflow(workflowId, contextId, activityId, nextId);
            }
        } catch (Exception e) {
            results.put("reason", e.getMessage());
        }
        
        return results;
    }//nextStep()
    
    
}//class WorkflowAgent
