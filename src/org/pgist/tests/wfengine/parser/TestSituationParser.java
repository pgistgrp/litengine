package org.pgist.tests.wfengine.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pgist.tests.wfengine.TestHelper;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.activity.*;
import org.pgist.wfengine.parser.*;


/**
 * Test for SituationParser.
 * 
 * @author kenny
 */
public class TestSituationParser {
    
    
    static SituationParser situationParser = new SituationParser();
    
    static Map<String, MeetingActivity> meetings = new HashMap<String, MeetingActivity>();
    
    
    @BeforeClass
    public static void setUp() {
        DeclarationParser declParser = new DeclarationParser();
        EnvironmentParser envParser = new EnvironmentParser();
        
        situationParser.setMeetings(meetings);
        situationParser.setDeclParser(declParser);
        situationParser.setEnvParser(envParser);
        
        MeetingActivity meeting1 = new MeetingActivity();
        meeting1.setName("meeting 1");
        meetings.put("meeting 1", meeting1);
        
        MeetingActivity meeting2 = new MeetingActivity();
        meeting2.setName("meeting 2");
        meetings.put("meeting 2", meeting2);
    }//setUp()
    
    
    /**
     * Test Case: situation, sequence
     * 
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        String xml =
            "<situation name=\" situation 1 \" description=\" situation 1 \">"
            + "    <environment>"
            + "        <var type=\" integer \" name=\" int_var_1 \" value=\" 100 \"/>"
            + "        <var type=\" string \" name=\" str_var_1 \" value=\" str_value_1 \"/>"
            + "    </environment>"
            + "    <declaration>"
            + "        <ins>"
            + "            <var name=\" ins_var_1 \" ref=\" parent.ins_var_1 \"/>"
            + "            <var name=\" ins_var_2 \" ref=\" parent.ins_var_2 \"/>"
            + "        </ins>"
            + "        <outs>"
            + "            <var name=\" outs_var_1 \" ref=\" parent.outs_var_1 \"/>"
            + "            <var name=\" outs_var_2 \" ref=\" parent.outs_var_2 \"/>"
            + "        </outs>"
            + "    </declaration>"
            + "    <sequence>"
            + "        <meeting name=\" meeting 1 \">"
            + "            <declaration>"
            + "                <ins>"
            + "                    <var name=\" ins_var_1 \" ref=\" parent.ins_var_1 \"/>"
            + "                    <var name=\" ins_var_2 \" ref=\" parent.ins_var_2 \"/>"
            + "                </ins>"
            + "                <outs>"
            + "                    <var name=\" outs_var_1 \" ref=\" parent.outs_var_1 \"/>"
            + "                    <var name=\" outs_var_2 \" ref=\" parent.outs_var_2 \"/>"
            + "                </outs>"
            + "            </declaration>"
            + "        </meeting>"
            + "        <meeting name=\" meeting 2 \">"
            + "            <declaration>"
            + "                <ins>"
            + "                    <var name=\" ins_var_1 \" ref=\" parent.ins_var_A \"/>"
            + "                    <var name=\" ins_var_2 \" ref=\" parent.ins_var_B \"/>"
            + "                </ins>"
            + "                <outs>"
            + "                    <var name=\" outs_var_1 \" ref=\" parent.outs_var_A \"/>"
            + "                    <var name=\" outs_var_2 \" ref=\" parent.outs_var_B \"/>"
            + "                </outs>"
            + "            </declaration>"
            + "        </meeting>"
            + "    </sequence>"
            + "</situation>";
        
        Document doc = TestHelper.getDocument(xml);
        
        SituationActivity situation = situationParser.parse(doc.getRootElement());
        
        assertNotNull("situation is null", situation);
        assertNotNull("situation.context is null", situation.getContext());
        assertNotNull("situation.declaration is null", situation.getContext().getDeclaration());
        assertNotNull("situation.context.environment is null", situation.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "situation 1", situation.getName());
        
        assertNotNull("situation's head activity is null", situation.getHeadActivity());
        assertNotNull("situation's tail activity is null", situation.getTailActivity());
        
        assertNotSame("tail is the same as head", situation.getHeadActivity(), situation.getTailActivity());
        
        assertEquals("head is not 'meeting 1'", "meeting 1", ((MeetingActivity) situation.getHeadActivity()).getName());
        assertEquals("tail is not 'meeting 2'", "meeting 2", ((MeetingActivity) situation.getTailActivity()).getName());
    }//test1()
    
    
    /**
     * Test Case: situation, while-loop
     * 
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        String xml =
            "<situation name=\" situation 1 \" description=\" situation 1 \">"
            + "    <sequence>"
            + "        <while>"
            + "            <sequence>"
            + "                <meeting name=\" meeting 1 \"/>"
            + "                <meeting name=\" meeting 2 \"/>"
            + "            </sequence>"
            + "        </while>"
            + "    </sequence>"
            + "</situation>";
        
        Document doc = TestHelper.getDocument(xml);
        
        SituationActivity situation = situationParser.parse(doc.getRootElement());
        
        assertNotNull("situation is null", situation);
        assertNotNull("situation.context is null", situation.getContext());
        assertNotNull("situation.declaration is null", situation.getContext().getDeclaration());
        assertNotNull("situation.context.environment is null", situation.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "situation 1", situation.getName());
        
        assertNotNull("situation's head activity is null", situation.getHeadActivity());
        assertNotNull("situation's tail activity is null", situation.getTailActivity());
        
        assertNotSame("tail is the same as head", situation.getHeadActivity(), situation.getTailActivity());
        
        assertTrue("head is not WhileActivity", situation.getHeadActivity() instanceof WhileActivity);
        assertTrue("tail is not LoopActivity", situation.getTailActivity() instanceof LoopActivity);
        
        WhileActivity whilst = (WhileActivity) situation.getHeadActivity();
        LoopActivity loop = (LoopActivity) situation.getTailActivity();
        
        MeetingActivity meeting1 = (MeetingActivity) whilst.getNext();
        MeetingActivity meeting2 = (MeetingActivity) meeting1.getNext();
        
        assertEquals("the first meeting in loop body is not 'meeting 1'", "meeting 1", meeting1.getName());
        assertEquals("the second meeting in loop body is not 'meeting 2'", "meeting 2", meeting2.getName());
        
        //check the node connections
        assertEquals("meeting1 is not successor of while", meeting1, whilst.getNext());
        assertEquals("while is not predecessor of meeting1", whilst, meeting1.getPrev());
        assertEquals("meeting1 is not predecessor of pgame2", meeting1, meeting2.getPrev());
        assertEquals("meeting2 is not successor of meeting1", meeting2, meeting1.getNext());
        assertEquals("meeting2 is not predecessor of loop", meeting2, loop.getPrev());
        assertEquals("loop is not successor of pgame2", loop, meeting2.getNext());
    }//test2()
    
    
    /**
     * Test Case: situation, repeat-until
     * 
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        String xml =
            "<situation name=\" situation 1 \" description=\" situation 1 \">"
            + "    <sequence>"
            + "        <repeat>"
            + "            <sequence>"
            + "                <meeting name=\" meeting 1 \"/>"
            + "                <meeting name=\" meeting 2 \"/>"
            + "            </sequence>"
            + "        </repeat>"
            + "    </sequence>"
            + "</situation>";
        
        Document doc = TestHelper.getDocument(xml);
        
        SituationActivity situation = situationParser.parse(doc.getRootElement());
        
        assertNotNull("situation is null", situation);
        assertNotNull("situation.context is null", situation.getContext());
        assertNotNull("situation.declaration is null", situation.getContext().getDeclaration());
        assertNotNull("situation.context.environment is null", situation.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "situation 1", situation.getName());
        
        assertNotNull("situation's head activity is null", situation.getHeadActivity());
        assertNotNull("situation's tail activity is null", situation.getTailActivity());
        
        assertNotSame("tail is the same as head", situation.getHeadActivity(), situation.getTailActivity());
        
        assertTrue("head is not RepeatActivity", situation.getHeadActivity() instanceof RepeatActivity);
        assertTrue("tail is not UntilActivity", situation.getTailActivity() instanceof UntilActivity);
        
        RepeatActivity repeat = (RepeatActivity) situation.getHeadActivity();
        UntilActivity until = (UntilActivity) situation.getTailActivity();
        
        MeetingActivity meeting1 = (MeetingActivity) repeat.getNext();
        MeetingActivity meeting2 = (MeetingActivity) meeting1.getNext();
        
        assertEquals("the first meeting in loop body is not 'meeting 1'", "meeting 1", meeting1.getName());
        assertEquals("the second meeting in loop body is not 'meeting 2'", "meeting 2", meeting2.getName());
        
        //check the node connections
        assertEquals("meeting1 is not successor of repeat", meeting1, repeat.getNext());
        assertEquals("repeat is not predecessor of meeting1", repeat, meeting1.getPrev());
        assertEquals("meeting1 is not predecessor of meeting2", meeting1, meeting2.getPrev());
        assertEquals("meeting2 is not successor of meeting1", meeting2, meeting1.getNext());
        assertEquals("meeting2 is not predecessor of until", meeting2, until.getPrev());
        assertEquals("until is not successor of meeting2", until, meeting2.getNext());
    }//test3()
    
    
    /**
     * Test Case: situation, branch-joib
     * 
     * @throws Exception
     */
    @Test
    public void test4() throws Exception {
        String xml =
            "<situation name=\" situation 1 \" description=\" meeting 1 \">"
            + "    <sequence>"
            + "        <branch>"
            + "            <sequence>"
            + "                <meeting name=\" meeting 1 \"/>"
            + "                <meeting name=\" meeting 2 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <meeting name=\" meeting 2 \"/>"
            + "                <meeting name=\" meeting 1 \"/>"
            + "            </sequence>"
            + "        </branch>"
            + "    </sequence>"
            + "</situation>";
        
        Document doc = TestHelper.getDocument(xml);
        
        SituationActivity situation = situationParser.parse(doc.getRootElement());
        
        assertNotNull("situation is null", situation);
        assertNotNull("situation.context is null", situation.getContext());
        assertNotNull("situation.declaration is null", situation.getContext().getDeclaration());
        assertNotNull("situation.context.environment is null", situation.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "situation 1", situation.getName());
        
        assertNotNull("situation's head meeting is null", situation.getHeadActivity());
        assertNotNull("situation's tail meeting is null", situation.getTailActivity());
        
        assertNotSame("tail is the same as head", situation.getHeadActivity(), situation.getTailActivity());
        
        assertTrue("head is not BranchActivity", situation.getHeadActivity() instanceof BranchActivity);
        assertTrue("tail is not JoinActivity", situation.getTailActivity() instanceof JoinActivity);
        
        BranchActivity branch = (BranchActivity) situation.getHeadActivity();
        JoinActivity join = (JoinActivity) situation.getTailActivity();
        
        List<Activity> branches = branch.getBranches();
        MeetingActivity meeting11 = (MeetingActivity) branches.get(0);
        MeetingActivity meeting12 = (MeetingActivity) meeting11.getNext();
        MeetingActivity meeting21 = (MeetingActivity) branches.get(1);
        MeetingActivity meeting22 = (MeetingActivity) meeting21.getNext();
        
        assertEquals("the first meeting in the first branch is not 'meeting 1'", "meeting 1", meeting11.getName());
        assertEquals("the second meeting in the first branch is not 'meeting 2'", "meeting 2", meeting12.getName());
        assertEquals("the first meeting in the second branch is not 'meeting 2'", "meeting 2", meeting21.getName());
        assertEquals("the second meeting in the second branch is not 'meeting 1'", "meeting 1", meeting22.getName());
        
        //check the node connections
        assertEquals("branch is not predecessor of meeting11", branch, meeting11.getPrev());
        assertEquals("branch is not predecessor of meeting21", branch, meeting21.getPrev());
        assertEquals("meeting11 is not predecessor of meeting12", meeting11, meeting12.getPrev());
        assertEquals("meeting12 is not successor of meeting11", meeting12, meeting11.getNext());
        assertEquals("meeting21 is not predecessor of meeting22", meeting21, meeting22.getPrev());
        assertEquals("meeting22 is not successor of meeting21", meeting22, meeting21.getNext());
        assertTrue("meeting12 is not predecessor of join", join.getJoins().contains(meeting12));
        assertEquals("join is not successor of meeting12", join, meeting12.getNext());
        assertTrue("meeting22 is not predecessor of join", join.getJoins().contains(meeting22));
        assertEquals("join is not successor of meeting22", join, meeting22.getNext());
    }//test4()
    
    
    /**
     * Test Case: situation, switch-endswitch
     * 
     * @throws Exception
     */
    @Test
    public void test5() throws Exception {
        String xml =
            "<situation name=\" situation 1 \" description=\" situation 1 \">"
            + "    <sequence>"
            + "        <switch>"
            + "            <sequence>"
            + "                <meeting name=\" meeting 1 \"/>"
            + "                <meeting name=\" meeting 2 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <meeting name=\" meeting 2 \"/>"
            + "                <meeting name=\" meeting 1 \"/>"
            + "            </sequence>"
            + "        </switch>"
            + "    </sequence>"
            + "</situation>";
        
        Document doc = TestHelper.getDocument(xml);
        
        SituationActivity situation = situationParser.parse(doc.getRootElement());
        
        assertNotNull("situation is null", situation);
        assertNotNull("situation.context is null", situation.getContext());
        assertNotNull("situation.declaration is null", situation.getContext().getDeclaration());
        assertNotNull("situation.context.environment is null", situation.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "situation 1", situation.getName());
        
        assertNotNull("situation's head meeting is null", situation.getHeadActivity());
        assertNotNull("situation's tail meeting is null", situation.getTailActivity());
        
        assertNotSame("tail is the same as head", situation.getHeadActivity(), situation.getTailActivity());
        
        assertTrue("head is not SwitchActivity", situation.getHeadActivity() instanceof SwitchActivity);
        assertTrue("tail is not EndSwitchActivity", situation.getTailActivity() instanceof EndSwitchActivity);
        
        SwitchActivity swt = (SwitchActivity) situation.getHeadActivity();
        EndSwitchActivity endswt = (EndSwitchActivity) situation.getTailActivity();
        
        List<Activity> swts = swt.getSwitches();
        MeetingActivity meeting11 = (MeetingActivity) swts.get(0);
        MeetingActivity meeting12 = (MeetingActivity) meeting11.getNext();
        MeetingActivity meeting21 = (MeetingActivity) swts.get(1);
        MeetingActivity meeting22 = (MeetingActivity) meeting21.getNext();
        
        assertEquals("the first meeting in the first switch is not 'meeting 1'", "meeting 1", meeting11.getName());
        assertEquals("the second meeting in the first switch is not 'meeting 2'", "meeting 2", meeting12.getName());
        assertEquals("the first meeting in the second switch is not 'meeting 2'", "meeting 2", meeting21.getName());
        assertEquals("the second meeting in the second switch is not 'meeting 1'", "meeting 1", meeting22.getName());
        
        //check the node connections
        assertEquals("switch is not predecessor of meeting11", swt, meeting11.getPrev());
        assertEquals("switch is not predecessor of meeting21", swt, meeting21.getPrev());
        assertEquals("meeting11 is not predecessor of meeting12", meeting11, meeting12.getPrev());
        assertEquals("meeting12 is not successor of meeting11", meeting12, meeting11.getNext());
        assertEquals("meeting21 is not predecessor of meeting22", meeting21, meeting22.getPrev());
        assertEquals("meeting22 is not successor of meeting21", meeting22, meeting21.getNext());
        assertTrue("meeting12 is not predecessor of end switch", endswt.getChoices().contains(meeting12));
        assertEquals("end switch is not successor of meeting12", endswt, meeting12.getNext());
        assertTrue("meeting22 is not predecessor of join", endswt.getChoices().contains(meeting22));
        assertEquals("end switch is not successor of meeting22", endswt, meeting22.getNext());
    }//test5()
    
    
    /**
     * Test Case: situation, composite
     * 
     * @throws Exception
     */
    @Test
    public void test6() throws Exception {
        String xml =
            "<situation name=\" situation 1 \" description=\" situation 1 \">"
            + "    <sequence>"
            + "        <meeting name=\" meeting 2 \"/>"
            + "        <meeting name=\" meeting 1 \"/>"
            + "        <switch>"
            + "            <sequence>"
            + "                <meeting name=\" meeting 1 \"/>"
            + "                <branch>"
            + "                    <sequence>"
            + "                        <meeting name=\" meeting 1 \"/>"
            + "                        <while>"
            + "                            <sequence>"
            + "                                <meeting name=\" meeting 1 \"/>"
            + "                                <meeting name=\" meeting 2 \"/>"
            + "                            </sequence>"
            + "                        </while>"
            + "                        <meeting name=\" meeting 2 \"/>"
            + "                    </sequence>"
            + "                    <sequence>"
            + "                        <meeting name=\" meeting 2 \"/>"
            + "                        <repeat>"
            + "                            <sequence>"
            + "                                <meeting name=\" meeting 2 \"/>"
            + "                                <meeting name=\" meeting 1 \"/>"
            + "                            </sequence>"
            + "                        </repeat>"
            + "                        <meeting name=\" meeting 1 \"/>"
            + "                    </sequence>"
            + "                </branch>"
            + "                <meeting name=\" meeting 2 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <meeting name=\" meeting 2 \"/>"
            + "                <meeting name=\" meeting 1 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <meeting name=\" meeting 2 \"/>"
            + "                <branch>"
            + "                    <sequence>"
            + "                        <meeting name=\" meeting 1 \"/>"
            + "                        <while>"
            + "                            <sequence>"
            + "                                <meeting name=\" meeting 1 \"/>"
            + "                                <meeting name=\" meeting 2 \"/>"
            + "                            </sequence>"
            + "                        </while>"
            + "                        <meeting name=\" meeting 2 \"/>"
            + "                    </sequence>"
            + "                    <sequence>"
            + "                        <meeting name=\" meeting 2 \"/>"
            + "                        <repeat>"
            + "                            <sequence>"
            + "                                <meeting name=\" meeting 2 \"/>"
            + "                                <meeting name=\" meeting 1 \"/>"
            + "                            </sequence>"
            + "                        </repeat>"
            + "                        <meeting name=\" meeting 1 \"/>"
            + "                    </sequence>"
            + "                </branch>"
            + "                <meeting name=\" meeting 1 \"/>"
            + "            </sequence>"
            + "        </switch>"
            + "        <meeting name=\" meeting 1 \"/>"
            + "        <meeting name=\" meeting 2 \"/>"
            + "    </sequence>"
            + "</situation>";
        
        Document doc = TestHelper.getDocument(xml);
        
        SituationActivity situation = situationParser.parse(doc.getRootElement());
        
        assertNotNull("situation is null", situation);
        assertNotNull("situation.context is null", situation.getContext());
        assertNotNull("situation.declaration is null", situation.getContext().getDeclaration());
        assertNotNull("situation.context.environment is null", situation.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "situation 1", situation.getName());
        
        assertNotNull("group's head activity is null", situation.getHeadActivity());
        assertNotNull("group's tail activity is null", situation.getTailActivity());
        
        assertNotSame("tail is the same as head", situation.getHeadActivity(), situation.getTailActivity());
        
        //First level, meeting 2 - meeting 1 - switch - meeting 1 - meeting 2
        assertTrue("first level, first activity is not a MeetingActivity", situation.getHeadActivity() instanceof MeetingActivity);
        MeetingActivity meeting = (MeetingActivity) situation.getHeadActivity();
        assertEquals("first level, first acitivity is not 'meeting 2'", "meeting 2", meeting.getName());
        assertTrue("first level, second activity is not a MeetingActivity", meeting.getNext() instanceof MeetingActivity);
        meeting = (MeetingActivity) meeting.getNext();
        assertEquals("first level, second acitivity is not 'meeting 1'", "meeting 1", meeting.getName());
        assertTrue("first level, third activity is not a switch", meeting.getNext() instanceof SwitchActivity);
        SwitchActivity swt = (SwitchActivity) meeting.getNext();
        assertNotNull("switch has not matched endswitch", swt.getEndSwitchActivity());
        EndSwitchActivity endswt = (EndSwitchActivity) swt.getEndSwitchActivity();
        assertTrue("first level, fourth activity is not a MeetingActivity", endswt.getNext() instanceof MeetingActivity);
        meeting = (MeetingActivity) endswt.getNext();
        assertEquals("first level, first acitivity is not 'meeting 1'", "meeting 1", meeting.getName());
        assertTrue("first level, fifth activity is not a MeetingActivity", meeting.getNext() instanceof MeetingActivity);
        meeting = (MeetingActivity) meeting.getNext();
        assertEquals("first level, second acitivity is not 'meeting 2'", "meeting 2", meeting.getName());
        
        //second level, in switch
        assertTrue("switch should have 3 switches", swt.getSwitches().size()==3);
        assertTrue("endswitch should have 3 choices", endswt.getChoices().size()==3);
        
        //first switch, meeting 1 - branch - meeting 2
        meeting = (MeetingActivity) swt.getSwitches().get(0);
        assertEquals("second level, first acitivity is not 'meeting 1'", "meeting 1", meeting.getName());
        assertTrue("second level, second activity is not a BranchActivity", meeting.getNext() instanceof BranchActivity);
        BranchActivity branch = (BranchActivity) meeting.getNext();
        assertNotNull("", branch.getJoinActivity());
        JoinActivity join = (JoinActivity) branch.getJoinActivity();
        assertTrue("second level, third activity is not a MeetingActivity", join.getNext() instanceof MeetingActivity);
        meeting = (MeetingActivity) join.getNext();
        assertEquals("second level, third acitivity is not 'meeting 2'", "meeting 2", meeting.getName());
        
        //first switch - first branch
        assertEquals("first switch, first branch should have 2 branches", 2, branch.getBranches().size());
        
        //first switch - first branch, meeting 1 - while - meeting 2
        meeting = (MeetingActivity) branch.getBranches().get(0);
        assertEquals("first switch, first branch, first acitivity is not 'meeting 1'", "meeting 1", meeting.getName());
        assertTrue("first switch, first branch, second acitivity is not WhileActivity", meeting.getNext() instanceof WhileActivity);
        WhileActivity whilst = (WhileActivity) meeting.getNext();
        assertTrue("first switch, first branch, first body acitivity is not MeetingActivity", whilst.getNext() instanceof MeetingActivity);
        MeetingActivity meeting1 = (MeetingActivity) whilst.getNext();
        assertEquals("first switch, first branch, first body acitivity is not 'meeting 1'", "meeting 1", meeting1.getName());
        assertNotNull("first switch, first branch, second body acitivity is not MeetingActivity", meeting1.getNext());
        meeting1 = (MeetingActivity) meeting1.getNext();
        assertEquals("first switch, first branch, second body acitivity is not 'meeting 2'", "meeting 2", meeting1.getName());
        assertTrue("first switch, first branch, second body acitivity is not connected to loop", meeting1.getNext() instanceof LoopActivity);
        LoopActivity loop = (LoopActivity) meeting1.getNext();
        assertTrue("loop is not matched for while", loop==whilst.getLoop());
        assertTrue("while is not matched for loop", whilst==loop.getWhilst());
        assertTrue("first switch, first branch, third acitivity is not MeetingActivity", loop.getNext() instanceof MeetingActivity);
        meeting1 = (MeetingActivity) loop.getNext();
        assertEquals("first switch, first branch, third acitivity is not 'meeting 2'", "meeting 2", meeting1.getName());
        assertTrue("meeting is not successor of loop", loop==meeting1.getPrev());
        assertTrue("meeting is not connected to join", meeting1.getNext()==join);
        
        //first switch - second branch, meeting 2 - repeat - meeting 1
        meeting = (MeetingActivity) branch.getBranches().get(1);
        assertEquals("first switch, second branch, first acitivity is not 'meeting 2'", "meeting 2", meeting.getName());
        assertTrue("first switch, second branch, second acitivity is not RepeatActivity", meeting.getNext() instanceof RepeatActivity);
        RepeatActivity repeat = (RepeatActivity) meeting.getNext();
        assertTrue("first switch, second branch, first body acitivity is not MeetingActivity", repeat.getNext() instanceof MeetingActivity);
        meeting1 = (MeetingActivity) repeat.getNext();
        assertEquals("first switch, second branch, first body acitivity is not 'meeting 2'", "meeting 2", meeting1.getName());
        assertNotNull("first switch, second branch, second body acitivity is not MeetingActivity", meeting1.getNext());
        meeting1 = (MeetingActivity) meeting1.getNext();
        assertEquals("first switch, second branch, second body acitivity is not 'meeting 1'", "meeting 1", meeting1.getName());
        assertTrue("first switch, second branch, second body acitivity is not connected to until", meeting1.getNext() instanceof UntilActivity);
        UntilActivity until = (UntilActivity) meeting1.getNext();
        assertTrue("until is not matched for repeat", until==repeat.getUntil());
        assertTrue("repeat is not matched for until", repeat==until.getRepeat());
        assertTrue("first switch, second branch, third acitivity is not MeetingActivity", repeat.getNext() instanceof MeetingActivity);
        meeting1 = (MeetingActivity) until.getNext();
        assertEquals("first switch, second branch, third acitivity is not 'meeting 1'", "meeting 1", meeting1.getName());
        assertTrue("meeting is not successor of until", until==meeting1.getPrev());
        assertTrue("meeting is not connected to join", meeting1.getNext()==join);
        
        //second switch - meeting 2 - meeting 1
        meeting = (MeetingActivity) swt.getSwitches().get(1);
        assertTrue("second switch, first acitivity is not MeetingActivity", meeting instanceof MeetingActivity);
        assertEquals("second switch, first acitivity is not 'meeting 2'", "meeting 2", meeting.getName());
        assertTrue("second switch, second acitivity is not MeetingActivity", meeting.getNext() instanceof MeetingActivity);
        meeting = (MeetingActivity) meeting.getNext();
        assertEquals("second switch, second acitivity is not 'meeting 1'", "meeting 1", meeting.getName());
        assertTrue("meeting is not connected to endswitch", meeting.getNext()==endswt);
        
        //third switch - similar to the first switch, omitted
    }//test6()
    
    
}//class TestSituationParser
