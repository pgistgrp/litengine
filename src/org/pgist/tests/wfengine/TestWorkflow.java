package org.pgist.tests.wfengine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.pgist.tests.wfengine.tasks.TestAutoTask1;
import org.pgist.tests.wfengine.tasks.TestAutoTask2;
import org.pgist.tests.wfengine.tasks.TestAutoTask3;
import org.pgist.wfengine.Workflow;
import org.pgist.wfengine.WorkflowTask;
import org.pgist.wfengine.WorkflowTaskRegistry;
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


/**
 * Test cases for Workflow.
 * 
 * @author kenny
 */
public class TestWorkflow {
    
    
    SituationActivity situationTemplate;
    
    
    @Before
    public void setUp() throws Exception {
        Document doc = new SAXReader().read(new File("test/workflow1.xml"));
        
        DeclarationParser declParser = new DeclarationParser();
        EnvironmentParser envParser = new EnvironmentParser();
        
        PGameParser pgameParser = new PGameParser();
        pgameParser.setDeclParser(declParser);
        
        Map<String, PGameActivity> pgames = new HashMap<String, PGameActivity>();
        
        for (Element element : (List<Element>) (doc.selectNodes("//templates/pgames/pgame"))) {
            PGameActivity pgame = pgameParser.parse(element);
            pgames.put(pgame.getName(), pgame);
        }//for
        
        PMethodParser pmethodParser = new PMethodParser();
        pmethodParser.setDeclParser(declParser);
        pmethodParser.setEnvParser(envParser);
        pmethodParser.setPgames(pgames);
        
        Map<String, PMethodActivity> pmethods = new HashMap<String, PMethodActivity>();
        
        for (Element element : (List<Element>) (doc.selectNodes("//templates/pmethods/pmethod"))) {
            PMethodActivity pmethod = pmethodParser.parse(element);
            pmethods.put(pmethod.getName(), pmethod);
        }//for
        
        MeetingParser meetingParser = new MeetingParser();
        meetingParser.setPmethods(pmethods);
        meetingParser.setDeclParser(declParser);
        meetingParser.setEnvParser(envParser);
        
        Map<String, MeetingActivity> meetings = new HashMap<String, MeetingActivity>();
        
        for (Element element : (List<Element>) (doc.selectNodes("//templates/meetings/meeting"))) {
            MeetingActivity meeting = meetingParser.parse(element);
            meetings.put(meeting.getName(), meeting);
        }//for
        
        SituationParser situationParser = new SituationParser();
        situationParser.setMeetings(meetings);
        situationParser.setDeclParser(declParser);
        situationParser.setEnvParser(envParser);
        
        situationTemplate = situationParser.parse((Element) doc.selectSingleNode("//templates/situations/situation"));
    }//setUp()
    
    
    /**
     * Test Case: 
     *
     */
    @Test
    public void test1() throws Exception {
        Map<String, WorkflowTask> tasks = new HashMap<String, WorkflowTask>();
        tasks.put("pgame1", new TestAutoTask1());
        tasks.put("pgame2", new TestAutoTask2());
        tasks.put("pgame3", new TestAutoTask3());
        
        WorkflowTaskRegistry registry = new WorkflowTaskRegistry();
        registry.setTasks(tasks);
        
        SituationActivity situation = new SituationActivity();
        situation.setDefinition(situationTemplate);
        
        Workflow flow = new Workflow();
        flow.setSituation(situation);
        situation.setWorkflow(flow);
        //flow.setEngine(engine);
        
        flow.start();
    }//test1()
    
    
}//class TestWorkflow
