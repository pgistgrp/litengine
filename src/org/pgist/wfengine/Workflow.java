package org.pgist.wfengine;

import java.util.List;
import java.util.Stack;

import org.hibernate.Session;


/**
 * The Workflow is the working instance for a workflow.
 * 
 * @author kenny
 *
 * @hibernate.class table="litwf_workflow"
 */
public class Workflow {
    
    
    protected Long id = null;
    
    //The definition activity of a workflow instance
    private Activity definition = null;
    
    //A workflow can't be definitioned more than once
    private boolean born = false;
    
    //
    private WorkflowEnvironment env = new WorkflowEnvironment();
    
    
    public Workflow() {
    }
    
    
    /**
     * @return
     * Notes: the id comes from one-to-one mapped Content object.
     * 
     * @hibernate.id generator-class="foreign"
     * @hibernate.generator-param name="property" value="env"
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
     * @hibernate.property unique="false" not-null="true"
     */
    public boolean isBorn() {
        return born;
    }
    
    
    public void setBorn(boolean born) {
        this.born = born;
    }
    
    
    /**
     * 
     * @return
     * 
     * @hibernate.one-to-one class="org.pgist.wfengine.WorkflowEnvironment" cascade="all"
     */
    public WorkflowEnvironment getEnv() {
        return env;
    }
    
    
    public void setEnv(WorkflowEnvironment env) {
        this.env = env;
    }
    
    
    /**
     * @return
     * @hibernate.many-to-one column="definition_id" class="org.pgist.wfengine.Activity" casecad="all"
     */
    public Activity getDefinition() {
        return definition;
    }
    
    
    public Activity setDefinition(Activity definition) {
        this.definition = definition;
        return this.definition;
    }//setDefinition()
    
    
    public boolean isFinished() {
        if (!born) return false;
        
        Stack stack = env.getExecuteStack();
        List waitingList = env.getWaitingList();
        return (stack.empty() && waitingList.size()==0);
    }
    
    
    public void execute() {
        Stack stack = env.getExecuteStack();
        List waitingList = env.getWaitingList();
        
        if (born==false) {
            born = true;
            stack.push(definition);
        } else {
            stack.addAll(waitingList);
            waitingList.clear();
        }
        
        while (!stack.empty()) {
            Activity activity = (Activity) stack.pop();
            System.out.println("---> active activity: "+activity.getId());
            activity.activate(env);
        }//while
        
        
    }//execute()
    
    
    public void saveState(Session session) {
        session.saveOrUpdate(env);
        session.saveOrUpdate(this);
        definition.saveState(session);
    }//saveState()
    
    
}//class Workflow
