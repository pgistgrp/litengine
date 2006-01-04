package org.pgist.wfengine.test;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.WFProcess;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.WorkflowEngine;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * Test XML Parser for workflow definition
 * 
 * @author kenny
 *
 */
public class ParserTest {
    
    
    private ClassPathXmlApplicationContext appContext = null;
    
    protected SessionFactory sessionFactory = null; 
    
    private WorkflowEngine engine = null;
    
    
    /**
     * Execute the task
     */
    public static void main(String[] args) {
        ParserTest test = new ParserTest();
        test.runInit();
        
        //test.runParse();
        //test.runSpawn();
        test.runFlow();
        
        test.runDestroy();
    }//main()
    
    
    public void runInit() {
        appContext = new ClassPathXmlApplicationContext(
            new String[] {
                "/test/dataAccessContext-local.xml",
                "/test/applicationContext.xml"
            }
        );
        
        sessionFactory = (SessionFactory) appContext.getBean("sessionFactory");
        Session session = SessionFactoryUtils.getSession(sessionFactory, true);
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
        
        engine = (WorkflowEngine) appContext.getBean("litengine");
    }//runInit()
    

    private void runDestroy() {
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
        SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
    }//runDestroy()


    public void runParse() {
        try {
            List processes = engine.addProcess("/home/kenny/workdir/LITEngine/test/flow.xml");
            
            WFProcess process = (WFProcess) processes.get(0);
            Workflow workflow = engine.spawn(process);
            
            engine.saveWorkflow(workflow);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//runParse()
    
    
    public void runSpawn() {
        try {
            WFProcess process = engine.getProcess(new Long(1));
            Workflow workflow = engine.spawn(process);
            engine.saveWorkflow(workflow);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//runSpawn()


    public void runFlow() {
        try {
            WorkflowEngine engine = (WorkflowEngine) appContext.getBean("litengine");
            Workflow workflow = engine.getWorkflow(new Long(305));
            
            List activities = workflow.getWaitingList();
            workflow.proceed((Activity) (activities.get(0)));
            
            engine.saveWorkflow(workflow);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }//runFlow()
    
    
}//class ParserTest
