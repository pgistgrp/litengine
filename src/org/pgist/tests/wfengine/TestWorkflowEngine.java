package org.pgist.tests.wfengine;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.WorkflowEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * Test cases for WorkflowEngine.
 * 
 * @author kenny
 */
public class TestWorkflowEngine {
    
    
    static private ApplicationContext appContext = null;
    
    static private SessionFactory sessionFactory = null; 
    
    static private WorkflowEngine engine = null;
    
    
    @BeforeClass
    public static void setUp() throws Exception {
        appContext = new FileSystemXmlApplicationContext(
            new String[] {
                "test/dataAccessContext-local.xml",
                "test/applicationContext.xml"
            }
        );
        
        sessionFactory = (SessionFactory) appContext.getBean("sessionFactory");
        Session session = SessionFactoryUtils.getSession(sessionFactory, true);
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
        
        engine = (WorkflowEngine) appContext.getBean("litengine");
    }//setUp()
    
    
    @AfterClass
    public static void tearDown() throws Exception {
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
        sessionHolder.getSession().flush();
        sessionHolder.getSession().close();
        SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
    }//tearDown()
    
    
    @Test
    public void importTemplates() throws Exception {
        Document doc = new SAXReader().read(new File("test/workflow1.xml"));
        engine.importTemplates(doc);
    }//importTemplates()
    
    
    @Test
    public void createWorkflow() throws Exception {
        engine.createWorkflow(275L);
    }//createWorkflow()
    
    
    @Test
    public void startWorkflow() throws Exception {
        engine.startWorkflow(386L);
    }//startWorkflow()
    
    
    @Test
    public void getNewWorkflows() throws Exception {
        List<Workflow> workflows = engine.getNewWorkflows();
        
        for (Workflow workflow : workflows) {
            System.out.println("new workflow: "+workflow.getSituation().getName());
        }//for
    }//getNewWorkflows()
    
    
    @Test
    public void getRunningWorkflows() throws Exception {
        List<Workflow> workflows = engine.getRunningWorkflows();
        
        for (Workflow workflow : workflows) {
            System.out.println("running workflow: "+workflow.getSituation().getName());
        }//for
    }//getRunningWorkflows()
    
    
    @Test
    public void getFinishedWorkflows() throws Exception {
        List<Workflow> workflows = engine.getFinishedWorkflows();
        
        for (Workflow workflow : workflows) {
            System.out.println("finished workflow: "+workflow.getSituation().getName());
        }//for
    }//getFinishedWorkflows()
    
    
}//class TestWorkflowEngine
