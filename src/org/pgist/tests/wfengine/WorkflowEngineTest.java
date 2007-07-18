package org.pgist.tests.wfengine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pgist.wfengine.WorkflowEngine;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * 
 * @author kenny
 *
 */
public class WorkflowEngineTest {
    
    
    private ApplicationContext appContext = null;
    
    private SessionFactory sessionFactory = null; 
    
    private WorkflowEngine engine = null;
    
    
    @Before
    public void setUp() throws Exception {
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
    
    
    @After
    public void tearDown() throws Exception {
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
        sessionHolder.getSession().close();
        SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
    }//tearDown()
    
    
    @Test
    public void test1() throws Exception {
        DeclarationParser declParser = new DeclarationParser();
        EnvironmentParser envParser = new EnvironmentParser();
        
        Map<String, PGameActivity> pgames = new HashMap<String, PGameActivity>();
        
        PGameParser pgameParser = new PGameParser();
        pgameParser.setDeclParser(declParser);
        
        Document doc = new SAXReader().read(new File("test/pgames.xml"));
        
        for (Element element : (List<Element>) doc.getRootElement().elements()) {
            PGameActivity activity = pgameParser.parse(element);
            pgames.put(activity.getName(), activity);
            engine.saveActivity(activity);
        }//for
        
        Map<String, PMethodActivity> pmethods = new HashMap<String, PMethodActivity>();
        
        PMethodParser pmethodParser = new PMethodParser();
        pmethodParser.setDeclParser(declParser);
        pmethodParser.setEnvParser(envParser);
        pmethodParser.setPgames(pgames);
        
        doc = new SAXReader().read(new File("test/pmethods.xml"));
        
        for (Element element : (List<Element>) doc.getRootElement().elements()) {
            PMethodActivity activity = pmethodParser.parse(element);
            pmethods.put(activity.getName(), activity);
            engine.saveActivity(activity);
        }//for
        
        Map<String, MeetingActivity> meetings = new HashMap<String, MeetingActivity>();
        
        MeetingParser meetingParser = new MeetingParser();
        meetingParser.setDeclParser(declParser);
        meetingParser.setEnvParser(envParser);
        meetingParser.setPmethods(pmethods);
        
        doc = new SAXReader().read(new File("test/meetings.xml"));
        
        for (Element element : (List<Element>) doc.getRootElement().elements()) {
            MeetingActivity activity = meetingParser.parse(element);
            meetings.put(activity.getName(), activity);
            engine.saveActivity(activity);
        }//for
        
        SituationParser situationParser = new SituationParser();
        situationParser.setDeclParser(declParser);
        situationParser.setEnvParser(envParser);
        situationParser.setMeetings(meetings);
        
        doc = new SAXReader().read(new File("test/situations.xml"));
        
        for (Element element : (List<Element>) doc.getRootElement().elements()) {
            SituationActivity activity = situationParser.parse(element);
            engine.saveActivity(activity);
        }//for
    }//test1()
    
    
}//class WorkflowEngineTest
