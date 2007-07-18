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
import org.pgist.wfengine.activity.BranchActivity;
import org.pgist.wfengine.activity.EndSwitchActivity;
import org.pgist.wfengine.activity.JoinActivity;
import org.pgist.wfengine.activity.LoopActivity;
import org.pgist.wfengine.activity.MeetingActivity;
import org.pgist.wfengine.activity.PMethodActivity;
import org.pgist.wfengine.activity.RepeatActivity;
import org.pgist.wfengine.activity.SwitchActivity;
import org.pgist.wfengine.activity.UntilActivity;
import org.pgist.wfengine.activity.WhileActivity;
import org.pgist.wfengine.parser.DeclarationParser;
import org.pgist.wfengine.parser.EnvironmentParser;
import org.pgist.wfengine.parser.MeetingParser;


/**
 * Test for MeetingParser.
 * 
 * @author kenny
 */
public class TestMeetingParser {
    
    
    static MeetingParser meetingParser = new MeetingParser();
    
    static Map<String, PMethodActivity> pmethods = new HashMap<String, PMethodActivity>();
    
    
    @BeforeClass
    public static void setUp() {
        DeclarationParser declParser = new DeclarationParser();
        EnvironmentParser envParser = new EnvironmentParser();
        
        meetingParser.setPmethods(pmethods);
        meetingParser.setDeclParser(declParser);
        meetingParser.setEnvParser(envParser);
        
        PMethodActivity pmethod1 = new PMethodActivity();
        pmethod1.setName("pmethod 1");
        pmethods.put("pmethod 1", pmethod1);
        
        PMethodActivity pmethod2 = new PMethodActivity();
        pmethod2.setName("pmethod 2");
        pmethods.put("pmethod 2", pmethod2);
    }//setUp()
    
    
    /**
     * Test Case: meeting, sequence
     * 
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        String xml =
            "<meeting name=\" meeting 1 \" description=\" meeting 1 \">"
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
            + "        <pmethod name=\" pmethod 1 \">"
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
            + "        </pmethod>"
            + "        <pmethod name=\" pmethod 2 \">"
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
            + "        </pmethod>"
            + "    </sequence>"
            + "</meeting>";
        
        Document doc = TestHelper.getDocument(xml);
        
        MeetingActivity meeting = meetingParser.parse(doc.getRootElement());
        
        assertNotNull("meeting is null", meeting);
        assertNotNull("meeting.context is null", meeting.getContext());
        assertNotNull("meeting.declaration is null", meeting.getContext().getDeclaration());
        assertNotNull("meeting.context.environment is null", meeting.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "meeting 1", meeting.getName());
        
        assertNotNull("group's head activity is null", meeting.getHeadActivity());
        assertNotNull("group's tail activity is null", meeting.getTailActivity());
        
        assertNotSame("tail is the same as head", meeting.getHeadActivity(), meeting.getTailActivity());
        
        assertEquals("head is not 'pmethod 1'", "pmethod 1", ((PMethodActivity) meeting.getHeadActivity()).getName());
        assertEquals("tail is not 'pmethod 2'", "pmethod 2", ((PMethodActivity) meeting.getTailActivity()).getName());
    }//test1()
    
    
    /**
     * Test Case: meeting, while-loop
     * 
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        String xml =
            "<meeting name=\" meeting 1 \" description=\" meeting 1 \">"
            + "    <sequence>"
            + "        <while>"
            + "            <sequence>"
            + "                <pmethod name=\" pmethod 1 \"/>"
            + "                <pmethod name=\" pmethod 2 \"/>"
            + "            </sequence>"
            + "        </while>"
            + "    </sequence>"
            + "</meeting>";
        
        Document doc = TestHelper.getDocument(xml);
        
        MeetingActivity meeting = meetingParser.parse(doc.getRootElement());
        
        assertNotNull("meeting is null", meeting);
        assertNotNull("meeting.context is null", meeting.getContext());
        assertNotNull("meeting.declaration is null", meeting.getContext().getDeclaration());
        assertNotNull("meeting.context.environment is null", meeting.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "meeting 1", meeting.getName());
        
        assertNotNull("group's head activity is null", meeting.getHeadActivity());
        assertNotNull("group's tail activity is null", meeting.getTailActivity());
        
        assertNotSame("tail is the same as head", meeting.getHeadActivity(), meeting.getTailActivity());
        
        assertTrue("head is not WhileActivity", meeting.getHeadActivity() instanceof WhileActivity);
        assertTrue("tail is not LoopActivity", meeting.getTailActivity() instanceof LoopActivity);
        
        WhileActivity whilst = (WhileActivity) meeting.getHeadActivity();
        LoopActivity loop = (LoopActivity) meeting.getTailActivity();
        
        PMethodActivity pmethod1 = (PMethodActivity) whilst.getNext();
        PMethodActivity pmethod2 = (PMethodActivity) pmethod1.getNext();
        
        assertEquals("the first pmethod in loop body is not 'pmethod 1'", "pmethod 1", pmethod1.getName());
        assertEquals("the second pmethod in loop body is not 'pmethod 2'", "pmethod 2", pmethod2.getName());
        
        //check the node connections
        assertEquals("pmethod1 is not successor of while", pmethod1, whilst.getNext());
        assertEquals("while is not predecessor of pmethod1", whilst, pmethod1.getPrev());
        assertEquals("pmethod1 is not predecessor of pmethod2", pmethod1, pmethod2.getPrev());
        assertEquals("pmethod2 is not successor of pmethod1", pmethod2, pmethod1.getNext());
        assertEquals("pmethod2 is not predecessor of loop", pmethod2, loop.getPrev());
        assertEquals("loop is not successor of pmethod2", loop, pmethod2.getNext());
    }//test2()
    
    
    /**
     * Test Case: meeting, repeat-until
     * 
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        String xml =
            "<meeting name=\" meeting 1 \" description=\" meeting 1 \">"
            + "    <sequence>"
            + "        <repeat>"
            + "            <sequence>"
            + "                <pmethod name=\" pmethod 1 \"/>"
            + "                <pmethod name=\" pmethod 2 \"/>"
            + "            </sequence>"
            + "        </repeat>"
            + "    </sequence>"
            + "</meeting>";
        
        Document doc = TestHelper.getDocument(xml);
        
        MeetingActivity meeting = meetingParser.parse(doc.getRootElement());
        
        assertNotNull("meeting is null", meeting);
        assertNotNull("meeting.context is null", meeting.getContext());
        assertNotNull("meeting.declaration is null", meeting.getContext().getDeclaration());
        assertNotNull("meeting.context.environment is null", meeting.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "meeting 1", meeting.getName());
        
        assertNotNull("group's head activity is null", meeting.getHeadActivity());
        assertNotNull("group's tail activity is null", meeting.getTailActivity());
        
        assertNotSame("tail is the same as head", meeting.getHeadActivity(), meeting.getTailActivity());
        
        assertTrue("head is not RepeatActivity", meeting.getHeadActivity() instanceof RepeatActivity);
        assertTrue("tail is not UntilActivity", meeting.getTailActivity() instanceof UntilActivity);
        
        RepeatActivity repeat = (RepeatActivity) meeting.getHeadActivity();
        UntilActivity until = (UntilActivity) meeting.getTailActivity();
        
        PMethodActivity pmethod1 = (PMethodActivity) repeat.getNext();
        PMethodActivity pmethod2 = (PMethodActivity) pmethod1.getNext();
        
        assertEquals("the first pmethod in loop body is not 'pmethod 1'", "pmethod 1", pmethod1.getName());
        assertEquals("the second pmethod in loop body is not 'pmethod 2'", "pmethod 2", pmethod2.getName());
        
        //check the node connections
        assertEquals("pmethod1 is not successor of repeat", pmethod1, repeat.getNext());
        assertEquals("repeat is not predecessor of pmethod1", repeat, pmethod1.getPrev());
        assertEquals("pmethod1 is not predecessor of pmethod2", pmethod1, pmethod2.getPrev());
        assertEquals("pmethod2 is not successor of pmethod1", pmethod2, pmethod1.getNext());
        assertEquals("pmethod2 is not predecessor of until", pmethod2, until.getPrev());
        assertEquals("until is not successor of pmethod2", until, pmethod2.getNext());
    }//test3()
    
    
    /**
     * Test Case: meeting, branch-joib
     * 
     * @throws Exception
     */
    @Test
    public void test4() throws Exception {
        String xml =
            "<meeting name=\" meeting 1 \" description=\" pmethod 1 \">"
            + "    <sequence>"
            + "        <branch>"
            + "            <sequence>"
            + "                <pmethod name=\" pmethod 1 \"/>"
            + "                <pmethod name=\" pmethod 2 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <pmethod name=\" pmethod 2 \"/>"
            + "                <pmethod name=\" pmethod 1 \"/>"
            + "            </sequence>"
            + "        </branch>"
            + "    </sequence>"
            + "</meeting>";
        
        Document doc = TestHelper.getDocument(xml);
        
        MeetingActivity meeting = meetingParser.parse(doc.getRootElement());
        
        assertNotNull("meeting is null", meeting);
        assertNotNull("meeting.context is null", meeting.getContext());
        assertNotNull("meeting.declaration is null", meeting.getContext().getDeclaration());
        assertNotNull("meeting.context.environment is null", meeting.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "meeting 1", meeting.getName());
        
        assertNotNull("group's head meeting is null", meeting.getHeadActivity());
        assertNotNull("group's tail meeting is null", meeting.getTailActivity());
        
        assertNotSame("tail is the same as head", meeting.getHeadActivity(), meeting.getTailActivity());
        
        assertTrue("head is not BranchActivity", meeting.getHeadActivity() instanceof BranchActivity);
        assertTrue("tail is not JoinActivity", meeting.getTailActivity() instanceof JoinActivity);
        
        BranchActivity branch = (BranchActivity) meeting.getHeadActivity();
        JoinActivity join = (JoinActivity) meeting.getTailActivity();
        
        List<Activity> branches = branch.getBranches();
        PMethodActivity pmethod11 = (PMethodActivity) branches.get(0);
        PMethodActivity pmethod12 = (PMethodActivity) pmethod11.getNext();
        PMethodActivity pmethod21 = (PMethodActivity) branches.get(1);
        PMethodActivity pmethod22 = (PMethodActivity) pmethod21.getNext();
        
        assertEquals("the first pmethod in the first branch is not 'pmethod 1'", "pmethod 1", pmethod11.getName());
        assertEquals("the second pmethod in the first branch is not 'pmethod 2'", "pmethod 2", pmethod12.getName());
        assertEquals("the first pmethod in the second branch is not 'pmethod 2'", "pmethod 2", pmethod21.getName());
        assertEquals("the second pmethod in the second branch is not 'pmethod 1'", "pmethod 1", pmethod22.getName());
        
        //check the node connections
        assertEquals("branch is not predecessor of pmethod11", branch, pmethod11.getPrev());
        assertEquals("branch is not predecessor of pmethod21", branch, pmethod21.getPrev());
        assertEquals("pmethod11 is not predecessor of pmethod12", pmethod11, pmethod12.getPrev());
        assertEquals("pmethod12 is not successor of pmethod11", pmethod12, pmethod11.getNext());
        assertEquals("pmethod21 is not predecessor of pmethod22", pmethod21, pmethod22.getPrev());
        assertEquals("pmethod22 is not successor of pmethod21", pmethod22, pmethod21.getNext());
        assertTrue("pmethod12 is not predecessor of join", join.getJoins().contains(pmethod12));
        assertEquals("join is not successor of pmethod12", join, pmethod12.getNext());
        assertTrue("pmethod22 is not predecessor of join", join.getJoins().contains(pmethod22));
        assertEquals("join is not successor of pmethod22", join, pmethod22.getNext());
    }//test4()
    
    
    /**
     * Test Case: meeting, switch-endswitch
     * 
     * @throws Exception
     */
    @Test
    public void test5() throws Exception {
        String xml =
            "<meeting name=\" meeting 1 \" description=\" meeting 1 \">"
            + "    <sequence>"
            + "        <switch>"
            + "            <sequence>"
            + "                <pmethod name=\" pmethod 1 \"/>"
            + "                <pmethod name=\" pmethod 2 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <pmethod name=\" pmethod 2 \"/>"
            + "                <pmethod name=\" pmethod 1 \"/>"
            + "            </sequence>"
            + "        </switch>"
            + "    </sequence>"
            + "</meeting>";
        
        Document doc = TestHelper.getDocument(xml);
        
        MeetingActivity meeting = meetingParser.parse(doc.getRootElement());
        
        assertNotNull("meeting is null", meeting);
        assertNotNull("meeting.context is null", meeting.getContext());
        assertNotNull("meeting.declaration is null", meeting.getContext().getDeclaration());
        assertNotNull("meeting.context.environment is null", meeting.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "meeting 1", meeting.getName());
        
        assertNotNull("group's head meeting is null", meeting.getHeadActivity());
        assertNotNull("group's tail meeting is null", meeting.getTailActivity());
        
        assertNotSame("tail is the same as head", meeting.getHeadActivity(), meeting.getTailActivity());
        
        assertTrue("head is not SwitchActivity", meeting.getHeadActivity() instanceof SwitchActivity);
        assertTrue("tail is not EndSwitchActivity", meeting.getTailActivity() instanceof EndSwitchActivity);
        
        SwitchActivity swt = (SwitchActivity) meeting.getHeadActivity();
        EndSwitchActivity endswt = (EndSwitchActivity) meeting.getTailActivity();
        
        List<Activity> swts = swt.getSwitches();
        PMethodActivity pmethod11 = (PMethodActivity) swts.get(0);
        PMethodActivity pmethod12 = (PMethodActivity) pmethod11.getNext();
        PMethodActivity pmethod21 = (PMethodActivity) swts.get(1);
        PMethodActivity pmethod22 = (PMethodActivity) pmethod21.getNext();
        
        assertEquals("the first pmethod in the first switch is not 'pmethod 1'", "pmethod 1", pmethod11.getName());
        assertEquals("the second pmethod in the first switch is not 'pmethod 2'", "pmethod 2", pmethod12.getName());
        assertEquals("the first pmethod in the second switch is not 'pmethod 2'", "pmethod 2", pmethod21.getName());
        assertEquals("the second pmethod in the second switch is not 'pmethod 1'", "pmethod 1", pmethod22.getName());
        
        //check the node connections
        assertEquals("switch is not predecessor of pmethod11", swt, pmethod11.getPrev());
        assertEquals("switch is not predecessor of pmethod21", swt, pmethod21.getPrev());
        assertEquals("pmethod11 is not predecessor of pmethod12", pmethod11, pmethod12.getPrev());
        assertEquals("pmethod12 is not successor of pmethod11", pmethod12, pmethod11.getNext());
        assertEquals("pmethod21 is not predecessor of pmethod22", pmethod21, pmethod22.getPrev());
        assertEquals("pmethod22 is not successor of pmethod21", pmethod22, pmethod21.getNext());
        assertTrue("pmethod12 is not predecessor of end switch", endswt.getChoices().contains(pmethod12));
        assertEquals("end switch is not successor of pmethod12", endswt, pmethod12.getNext());
        assertTrue("pmethod22 is not predecessor of join", endswt.getChoices().contains(pmethod22));
        assertEquals("end switch is not successor of pmethod22", endswt, pmethod22.getNext());
    }//test5()
    
    
    /**
     * Test Case: meeting, composite
     * 
     * @throws Exception
     */
    @Test
    public void test6() throws Exception {
        String xml =
            "<meeting name=\" meeting 1 \" description=\" meeting 1 \">"
            + "    <sequence>"
            + "        <pmethod name=\" pmethod 2 \"/>"
            + "        <pmethod name=\" pmethod 1 \"/>"
            + "        <switch>"
            + "            <sequence>"
            + "                <pmethod name=\" pmethod 1 \"/>"
            + "                <branch>"
            + "                    <sequence>"
            + "                        <pmethod name=\" pmethod 1 \"/>"
            + "                        <while>"
            + "                            <sequence>"
            + "                                <pmethod name=\" pmethod 1 \"/>"
            + "                                <pmethod name=\" pmethod 2 \"/>"
            + "                            </sequence>"
            + "                        </while>"
            + "                        <pmethod name=\" pmethod 2 \"/>"
            + "                    </sequence>"
            + "                    <sequence>"
            + "                        <pmethod name=\" pmethod 2 \"/>"
            + "                        <repeat>"
            + "                            <sequence>"
            + "                                <pmethod name=\" pmethod 2 \"/>"
            + "                                <pmethod name=\" pmethod 1 \"/>"
            + "                            </sequence>"
            + "                        </repeat>"
            + "                        <pmethod name=\" pmethod 1 \"/>"
            + "                    </sequence>"
            + "                </branch>"
            + "                <pmethod name=\" pmethod 2 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <pmethod name=\" pmethod 2 \"/>"
            + "                <pmethod name=\" pmethod 1 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <pmethod name=\" pmethod 2 \"/>"
            + "                <branch>"
            + "                    <sequence>"
            + "                        <pmethod name=\" pmethod 1 \"/>"
            + "                        <while>"
            + "                            <sequence>"
            + "                                <pmethod name=\" pmethod 1 \"/>"
            + "                                <pmethod name=\" pmethod 2 \"/>"
            + "                            </sequence>"
            + "                        </while>"
            + "                        <pmethod name=\" pmethod 2 \"/>"
            + "                    </sequence>"
            + "                    <sequence>"
            + "                        <pmethod name=\" pmethod 2 \"/>"
            + "                        <repeat>"
            + "                            <sequence>"
            + "                                <pmethod name=\" pmethod 2 \"/>"
            + "                                <pmethod name=\" pmethod 1 \"/>"
            + "                            </sequence>"
            + "                        </repeat>"
            + "                        <pmethod name=\" pmethod 1 \"/>"
            + "                    </sequence>"
            + "                </branch>"
            + "                <pmethod name=\" pmethod 1 \"/>"
            + "            </sequence>"
            + "        </switch>"
            + "        <pmethod name=\" pmethod 1 \"/>"
            + "        <pmethod name=\" pmethod 2 \"/>"
            + "    </sequence>"
            + "</meeting>";
        
        Document doc = TestHelper.getDocument(xml);
        
        MeetingActivity meeting = meetingParser.parse(doc.getRootElement());
        
        assertNotNull("meeting is null", meeting);
        assertNotNull("meeting.context is null", meeting.getContext());
        assertNotNull("meeting.declaration is null", meeting.getContext().getDeclaration());
        assertNotNull("meeting.context.environment is null", meeting.getContext().getEnvironment());
        
        assertEquals("'name' incorrect", "meeting 1", meeting.getName());
        
        assertNotNull("group's head activity is null", meeting.getHeadActivity());
        assertNotNull("group's tail activity is null", meeting.getTailActivity());
        
        assertNotSame("tail is the same as head", meeting.getHeadActivity(), meeting.getTailActivity());
        
        //First level, pmethod 2 - pmethod 1 - switch - pmethod 1 - pmethod 2
        assertTrue("first level, first activity is not a PMethodActivity", meeting.getHeadActivity() instanceof PMethodActivity);
        PMethodActivity pmethod = (PMethodActivity) meeting.getHeadActivity();
        assertEquals("first level, first acitivity is not 'pmethod 2'", "pmethod 2", pmethod.getName());
        assertTrue("first level, second activity is not a PMethodActivity", pmethod.getNext() instanceof PMethodActivity);
        pmethod = (PMethodActivity) pmethod.getNext();
        assertEquals("first level, second acitivity is not 'pmethod 1'", "pmethod 1", pmethod.getName());
        assertTrue("first level, third activity is not a switch", pmethod.getNext() instanceof SwitchActivity);
        SwitchActivity swt = (SwitchActivity) pmethod.getNext();
        assertNotNull("switch has not matched endswitch", swt.getEndSwitchActivity());
        EndSwitchActivity endswt = (EndSwitchActivity) swt.getEndSwitchActivity();
        assertTrue("first level, fourth activity is not a PMethodActivity", endswt.getNext() instanceof PMethodActivity);
        pmethod = (PMethodActivity) endswt.getNext();
        assertEquals("first level, first acitivity is not 'pmethod 1'", "pmethod 1", pmethod.getName());
        assertTrue("first level, fifth activity is not a PMethodActivity", pmethod.getNext() instanceof PMethodActivity);
        pmethod = (PMethodActivity) pmethod.getNext();
        assertEquals("first level, second acitivity is not 'pmethod 2'", "pmethod 2", pmethod.getName());
        
        //second level, in switch
        assertTrue("switch should have 3 switches", swt.getSwitches().size()==3);
        assertTrue("endswitch should have 3 choices", endswt.getChoices().size()==3);
        
        //first switch, pmethod 1 - branch - pmethod 2
        pmethod = (PMethodActivity) swt.getSwitches().get(0);
        assertEquals("second level, first acitivity is not 'pmethod 1'", "pmethod 1", pmethod.getName());
        assertTrue("second level, second activity is not a BranchActivity", pmethod.getNext() instanceof BranchActivity);
        BranchActivity branch = (BranchActivity) pmethod.getNext();
        assertNotNull("", branch.getJoinActivity());
        JoinActivity join = (JoinActivity) branch.getJoinActivity();
        assertTrue("second level, third activity is not a PMethodActivity", join.getNext() instanceof PMethodActivity);
        pmethod = (PMethodActivity) join.getNext();
        assertEquals("second level, third acitivity is not 'pmethod 2'", "pmethod 2", pmethod.getName());
        
        //first switch - first branch
        assertEquals("first switch, first branch should have 2 branches", 2, branch.getBranches().size());
        
        //first switch - first branch, pmethod 1 - while - pmethod 2
        pmethod = (PMethodActivity) branch.getBranches().get(0);
        assertEquals("first switch, first branch, first acitivity is not 'pmethod 1'", "pmethod 1", pmethod.getName());
        assertTrue("first switch, first branch, second acitivity is not WhileActivity", pmethod.getNext() instanceof WhileActivity);
        WhileActivity whilst = (WhileActivity) pmethod.getNext();
        assertTrue("first switch, first branch, first body acitivity is not PMethodActivity", whilst.getNext() instanceof PMethodActivity);
        PMethodActivity pmethod1 = (PMethodActivity) whilst.getNext();
        assertEquals("first switch, first branch, first body acitivity is not 'pmethod 1'", "pmethod 1", pmethod1.getName());
        assertNotNull("first switch, first branch, second body acitivity is not PMethodActivity", pmethod1.getNext());
        pmethod1 = (PMethodActivity) pmethod1.getNext();
        assertEquals("first switch, first branch, second body acitivity is not 'pmethod 2'", "pmethod 2", pmethod1.getName());
        assertTrue("first switch, first branch, second body acitivity is not connected to loop", pmethod1.getNext() instanceof LoopActivity);
        LoopActivity loop = (LoopActivity) pmethod1.getNext();
        assertTrue("loop is not matched for while", loop==whilst.getLoop());
        assertTrue("while is not matched for loop", whilst==loop.getWhilst());
        assertTrue("first switch, first branch, third acitivity is not PMethodActivity", loop.getNext() instanceof PMethodActivity);
        pmethod1 = (PMethodActivity) loop.getNext();
        assertEquals("first switch, first branch, third acitivity is not 'pmethod 2'", "pmethod 2", pmethod1.getName());
        assertTrue("pmethod is not successor of loop", loop==pmethod1.getPrev());
        assertTrue("pmethod is not connected to join", pmethod1.getNext()==join);
        
        //first switch - second branch, pmethod 2 - repeat - pmethod 1
        pmethod = (PMethodActivity) branch.getBranches().get(1);
        assertEquals("first switch, second branch, first acitivity is not 'pmethod 2'", "pmethod 2", pmethod.getName());
        assertTrue("first switch, second branch, second acitivity is not RepeatActivity", pmethod.getNext() instanceof RepeatActivity);
        RepeatActivity repeat = (RepeatActivity) pmethod.getNext();
        assertTrue("first switch, second branch, first body acitivity is not PMethodActivity", repeat.getNext() instanceof PMethodActivity);
        pmethod1 = (PMethodActivity) repeat.getNext();
        assertEquals("first switch, second branch, first body acitivity is not 'pmethod 2'", "pmethod 2", pmethod1.getName());
        assertNotNull("first switch, second branch, second body acitivity is not PMethodActivity", pmethod1.getNext());
        pmethod1 = (PMethodActivity) pmethod1.getNext();
        assertEquals("first switch, second branch, second body acitivity is not 'pmethod 1'", "pmethod 1", pmethod1.getName());
        assertTrue("first switch, second branch, second body acitivity is not connected to until", pmethod1.getNext() instanceof UntilActivity);
        UntilActivity until = (UntilActivity) pmethod1.getNext();
        assertTrue("until is not matched for repeat", until==repeat.getUntil());
        assertTrue("repeat is not matched for until", repeat==until.getRepeat());
        assertTrue("first switch, second branch, third acitivity is not PMethodActivity", repeat.getNext() instanceof PMethodActivity);
        pmethod1 = (PMethodActivity) until.getNext();
        assertEquals("first switch, second branch, third acitivity is not 'pmethod 1'", "pmethod 1", pmethod1.getName());
        assertTrue("pmethod is not successor of until", until==pmethod1.getPrev());
        assertTrue("pmethod is not connected to join", pmethod1.getNext()==join);
        
        //second switch - pmethod 2 - pmethod 1
        pmethod = (PMethodActivity) swt.getSwitches().get(1);
        assertTrue("second switch, first acitivity is not PMethodActivity", pmethod instanceof PMethodActivity);
        assertEquals("second switch, first acitivity is not 'pmethod 2'", "pmethod 2", pmethod.getName());
        assertTrue("second switch, second acitivity is not PMethodActivity", pmethod.getNext() instanceof PMethodActivity);
        pmethod = (PMethodActivity) pmethod.getNext();
        assertEquals("second switch, second acitivity is not 'pmethod 1'", "pmethod 1", pmethod.getName());
        assertTrue("pmethod is not connected to endswitch", pmethod.getNext()==endswt);
        
        //third switch - similar to the first switch, omitted
    }//test6()
    
    
}//class TestMeetingParser
