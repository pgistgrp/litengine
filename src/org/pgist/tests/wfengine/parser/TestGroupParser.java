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
import org.pgist.wfengine.activity.GroupActivity;
import org.pgist.wfengine.activity.JoinActivity;
import org.pgist.wfengine.activity.LoopActivity;
import org.pgist.wfengine.activity.PAutoGameActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.PManualGameActivity;
import org.pgist.wfengine.activity.RepeatActivity;
import org.pgist.wfengine.activity.SwitchActivity;
import org.pgist.wfengine.activity.UntilActivity;
import org.pgist.wfengine.activity.WhileActivity;
import org.pgist.wfengine.parser.GroupParser;


/**
 * Test for GroupParser.
 * 
 * @author kenny
 */
public class TestGroupParser {
    
    
    private static GroupParser parser = TestHelper.getParserSuite().getGroupParser();
    
    
    @BeforeClass
    public static void setUp() {
        Map<String, PGameActivity> pgames = new HashMap<String, PGameActivity>();
        PAutoGameActivity game1 = new PAutoGameActivity();
        game1.setName("pgame 1");
        pgames.put("pgame 1", game1);
        
        PManualGameActivity game2 = new PManualGameActivity();
        game2.setName("pgame 2");
        pgames.put("pgame 2", game2);
        
        TestHelper.getParserSuite().getSequenceParser().setPgames(pgames);
    }//setUp()
    
    
    /**
     * Test Case: pmethod, sequence
     * 
     * @throws Exception
     */
    @Test
    public void testPMethod1() throws Exception {
        String xml =
            "<pmethod name=\" pmethod 1 \" description=\" pmethod 1 \">"
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
            + "        <pgame name=\" pgame 1 \">"
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
            + "        </pgame>"
            + "        <pgame name=\" pgame 1 \">"
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
            + "        </pgame>"
            + "    </sequence>"
            + "</pmethod>";
        
        Document doc = TestHelper.getDocument(xml);
        
        GroupActivity activity = parser.parse(doc.getRootElement());
        
        assertNotNull("activity is null", activity);
        assertNotNull("activity.context is null", activity.getContext());
        assertNotNull("activity.declaration is null", activity.getDeclaration());
        assertNotNull("activity.context.environment is null", activity.getContext().getEnvironment());
        
        assertTrue("activity is not 'pmethod'", activity.getLevel()==GroupActivity.LEVEL_PMETHOD);
        
        assertEquals("'name' incorrect", "pmethod 1", activity.getName());
        
        assertNotNull("group's head activity is null", activity.getHeadActivity());
        assertNotNull("group's tail activity is null", activity.getTailActivity());
        
        assertNotSame("tail is the same as head", activity.getHeadActivity(), activity.getTailActivity());
        
        assertEquals("head is not 'pgame 1'", "pgame 1", ((PGameActivity) activity.getHeadActivity()).getName());
        assertEquals("tail is not 'pgame 1'", "pgame 1", ((PGameActivity) activity.getTailActivity()).getName());
    }//testPMethod1()
    
    
    /**
     * Test Case: pmethod, while-loop
     * 
     * @throws Exception
     */
    @Test
    public void testPMethod2() throws Exception {
        String xml =
            "<pmethod name=\" pmethod 1 \" description=\" pmethod 1 \">"
            + "    <sequence>"
            + "        <while>"
            + "            <sequence>"
            + "                <pgame name=\" pgame 1 \"/>"
            + "                <pgame name=\" pgame 2 \"/>"
            + "            </sequence>"
            + "        </while>"
            + "    </sequence>"
            + "</pmethod>";
        
        Document doc = TestHelper.getDocument(xml);
        
        GroupActivity activity = parser.parse(doc.getRootElement());
        
        assertNotNull("activity is null", activity);
        assertNotNull("activity.context is null", activity.getContext());
        assertNotNull("activity.declaration is null", activity.getDeclaration());
        assertNotNull("activity.context.environment is null", activity.getContext().getEnvironment());
        
        assertTrue("activity is not 'pmethod'", activity.getLevel()==GroupActivity.LEVEL_PMETHOD);
        
        assertEquals("'name' incorrect", "pmethod 1", activity.getName());
        
        assertNotNull("group's head activity is null", activity.getHeadActivity());
        assertNotNull("group's tail activity is null", activity.getTailActivity());
        
        assertNotSame("tail is the same as head", activity.getHeadActivity(), activity.getTailActivity());
        
        assertTrue("head is not WhileActivity", activity.getHeadActivity() instanceof WhileActivity);
        assertTrue("tail is not LoopActivity", activity.getTailActivity() instanceof LoopActivity);
        
        WhileActivity whilst = (WhileActivity) activity.getHeadActivity();
        LoopActivity loop = (LoopActivity) activity.getTailActivity();
        
        PGameActivity pgame1 = (PGameActivity) whilst.getNext();
        PGameActivity pgame2 = (PGameActivity) pgame1.getNext();
        
        assertEquals("the first pgame in loop body is not 'pgame 1'", "pgame 1", pgame1.getName());
        assertEquals("the second pgame in loop body is not 'pgame 2'", "pgame 2", pgame2.getName());
        
        //check the node connections
        assertEquals("pgame1 is not successor of while", pgame1, whilst.getNext());
        assertEquals("while is not predecessor of pgame1", whilst, pgame1.getPrev());
        assertEquals("pgame1 is not predecessor of pgame2", pgame1, pgame2.getPrev());
        assertEquals("pgame2 is not successor of pgame1", pgame2, pgame1.getNext());
        assertEquals("pgame2 is not predecessor of loop", pgame2, loop.getPrev());
        assertEquals("loop is not successor of pgame2", loop, pgame2.getNext());
    }//testPMethod2()
    
    
    /**
     * Test Case: pmethod, repeat-until
     * 
     * @throws Exception
     */
    @Test
    public void testPMethod3() throws Exception {
        String xml =
            "<pmethod name=\" pmethod 1 \" description=\" pmethod 1 \">"
            + "    <sequence>"
            + "        <repeat>"
            + "            <sequence>"
            + "                <pgame name=\" pgame 1 \"/>"
            + "                <pgame name=\" pgame 2 \"/>"
            + "            </sequence>"
            + "        </repeat>"
            + "    </sequence>"
            + "</pmethod>";
        
        Document doc = TestHelper.getDocument(xml);
        
        GroupActivity activity = parser.parse(doc.getRootElement());
        
        assertNotNull("activity is null", activity);
        assertNotNull("activity.context is null", activity.getContext());
        assertNotNull("activity.declaration is null", activity.getDeclaration());
        assertNotNull("activity.context.environment is null", activity.getContext().getEnvironment());
        
        assertTrue("activity is not 'pmethod'", activity.getLevel()==GroupActivity.LEVEL_PMETHOD);
        
        assertEquals("'name' incorrect", "pmethod 1", activity.getName());
        
        assertNotNull("group's head activity is null", activity.getHeadActivity());
        assertNotNull("group's tail activity is null", activity.getTailActivity());
        
        assertNotSame("tail is the same as head", activity.getHeadActivity(), activity.getTailActivity());
        
        assertTrue("head is not RepeatActivity", activity.getHeadActivity() instanceof RepeatActivity);
        assertTrue("tail is not UntilActivity", activity.getTailActivity() instanceof UntilActivity);
        
        RepeatActivity repeat = (RepeatActivity) activity.getHeadActivity();
        UntilActivity until = (UntilActivity) activity.getTailActivity();
        
        PGameActivity pgame1 = (PGameActivity) repeat.getNext();
        PGameActivity pgame2 = (PGameActivity) pgame1.getNext();
        
        assertEquals("the first pgame in loop body is not 'pgame 1'", "pgame 1", pgame1.getName());
        assertEquals("the second pgame in loop body is not 'pgame 2'", "pgame 2", pgame2.getName());
        
        //check the node connections
        assertEquals("pgame1 is not successor of repeat", pgame1, repeat.getNext());
        assertEquals("repeat is not predecessor of pgame1", repeat, pgame1.getPrev());
        assertEquals("pgame1 is not predecessor of pgame2", pgame1, pgame2.getPrev());
        assertEquals("pgame2 is not successor of pgame1", pgame2, pgame1.getNext());
        assertEquals("pgame2 is not predecessor of until", pgame2, until.getPrev());
        assertEquals("until is not successor of pgame2", until, pgame2.getNext());
    }//testPMethod3()
    
    
    /**
     * Test Case: pmethod, branch-joib
     * 
     * @throws Exception
     */
    @Test
    public void testPMethod4() throws Exception {
        String xml =
            "<pmethod name=\" pmethod 1 \" description=\" pmethod 1 \">"
            + "    <sequence>"
            + "        <branch>"
            + "            <sequence>"
            + "                <pgame name=\" pgame 1 \"/>"
            + "                <pgame name=\" pgame 2 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <pgame name=\" pgame 2 \"/>"
            + "                <pgame name=\" pgame 1 \"/>"
            + "            </sequence>"
            + "        </branch>"
            + "    </sequence>"
            + "</pmethod>";
        
        Document doc = TestHelper.getDocument(xml);
        
        GroupActivity activity = parser.parse(doc.getRootElement());
        
        assertNotNull("activity is null", activity);
        assertNotNull("activity.context is null", activity.getContext());
        assertNotNull("activity.declaration is null", activity.getDeclaration());
        assertNotNull("activity.context.environment is null", activity.getContext().getEnvironment());
        
        assertTrue("activity is not 'pmethod'", activity.getLevel()==GroupActivity.LEVEL_PMETHOD);
        
        assertEquals("'name' incorrect", "pmethod 1", activity.getName());
        
        assertNotNull("group's head activity is null", activity.getHeadActivity());
        assertNotNull("group's tail activity is null", activity.getTailActivity());
        
        assertNotSame("tail is the same as head", activity.getHeadActivity(), activity.getTailActivity());
        
        assertTrue("head is not BranchActivity", activity.getHeadActivity() instanceof BranchActivity);
        assertTrue("tail is not JoinActivity", activity.getTailActivity() instanceof JoinActivity);
        
        BranchActivity branch = (BranchActivity) activity.getHeadActivity();
        JoinActivity join = (JoinActivity) activity.getTailActivity();
        
        List<Activity> branches = branch.getBranches();
        PGameActivity pgame11 = (PGameActivity) branches.get(0);
        PGameActivity pgame12 = (PGameActivity) pgame11.getNext();
        PGameActivity pgame21 = (PGameActivity) branches.get(1);
        PGameActivity pgame22 = (PGameActivity) pgame21.getNext();
        
        assertEquals("the first pgame in the first branch is not 'pgame 1'", "pgame 1", pgame11.getName());
        assertEquals("the second pgame in the first branch is not 'pgame 2'", "pgame 2", pgame12.getName());
        assertEquals("the first pgame in the second branch is not 'pgame 2'", "pgame 2", pgame21.getName());
        assertEquals("the second pgame in the second branch is not 'pgame 1'", "pgame 1", pgame22.getName());
        
        //check the node connections
        assertEquals("branch is not predecessor of pgame11", branch, pgame11.getPrev());
        assertEquals("branch is not predecessor of pgame21", branch, pgame21.getPrev());
        assertEquals("pgame11 is not predecessor of pgame12", pgame11, pgame12.getPrev());
        assertEquals("pgame12 is not successor of pgame11", pgame12, pgame11.getNext());
        assertEquals("pgame21 is not predecessor of pgame22", pgame21, pgame22.getPrev());
        assertEquals("pgame22 is not successor of pgame21", pgame22, pgame21.getNext());
        assertTrue("pgame12 is not predecessor of join", join.getJoins().contains(pgame12));
        assertEquals("join is not successor of pgame12", join, pgame12.getNext());
        assertTrue("pgame22 is not predecessor of join", join.getJoins().contains(pgame22));
        assertEquals("join is not successor of pgame22", join, pgame22.getNext());
    }//testPMethod4()
    
    
    /**
     * Test Case: pmethod, switch-endswitch
     * 
     * @throws Exception
     */
    @Test
    public void testPMethod5() throws Exception {
        String xml =
            "<pmethod name=\" pmethod 1 \" description=\" pmethod 1 \">"
            + "    <sequence>"
            + "        <switch>"
            + "            <sequence>"
            + "                <pgame name=\" pgame 1 \"/>"
            + "                <pgame name=\" pgame 2 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <pgame name=\" pgame 2 \"/>"
            + "                <pgame name=\" pgame 1 \"/>"
            + "            </sequence>"
            + "        </switch>"
            + "    </sequence>"
            + "</pmethod>";
        
        Document doc = TestHelper.getDocument(xml);
        
        GroupActivity activity = parser.parse(doc.getRootElement());
        
        assertNotNull("activity is null", activity);
        assertNotNull("activity.context is null", activity.getContext());
        assertNotNull("activity.declaration is null", activity.getDeclaration());
        assertNotNull("activity.context.environment is null", activity.getContext().getEnvironment());
        
        assertTrue("activity is not 'pmethod'", activity.getLevel()==GroupActivity.LEVEL_PMETHOD);
        
        assertEquals("'name' incorrect", "pmethod 1", activity.getName());
        
        assertNotNull("group's head activity is null", activity.getHeadActivity());
        assertNotNull("group's tail activity is null", activity.getTailActivity());
        
        assertNotSame("tail is the same as head", activity.getHeadActivity(), activity.getTailActivity());
        
        assertTrue("head is not SwitchActivity", activity.getHeadActivity() instanceof SwitchActivity);
        assertTrue("tail is not EndSwitchActivity", activity.getTailActivity() instanceof EndSwitchActivity);
        
        SwitchActivity swt = (SwitchActivity) activity.getHeadActivity();
        EndSwitchActivity endswt = (EndSwitchActivity) activity.getTailActivity();
        
        List<Activity> swts = swt.getSwitches();
        PGameActivity pgame11 = (PGameActivity) swts.get(0);
        PGameActivity pgame12 = (PGameActivity) pgame11.getNext();
        PGameActivity pgame21 = (PGameActivity) swts.get(1);
        PGameActivity pgame22 = (PGameActivity) pgame21.getNext();
        
        assertEquals("the first pgame in the first switch is not 'pgame 1'", "pgame 1", pgame11.getName());
        assertEquals("the second pgame in the first switch is not 'pgame 2'", "pgame 2", pgame12.getName());
        assertEquals("the first pgame in the second switch is not 'pgame 2'", "pgame 2", pgame21.getName());
        assertEquals("the second pgame in the second switch is not 'pgame 1'", "pgame 1", pgame22.getName());
        
        //check the node connections
        assertEquals("switch is not predecessor of pgame11", swt, pgame11.getPrev());
        assertEquals("switch is not predecessor of pgame21", swt, pgame21.getPrev());
        assertEquals("pgame11 is not predecessor of pgame12", pgame11, pgame12.getPrev());
        assertEquals("pgame12 is not successor of pgame11", pgame12, pgame11.getNext());
        assertEquals("pgame21 is not predecessor of pgame22", pgame21, pgame22.getPrev());
        assertEquals("pgame22 is not successor of pgame21", pgame22, pgame21.getNext());
        assertTrue("pgame12 is not predecessor of end switch", endswt.getChoices().contains(pgame12));
        assertEquals("end switch is not successor of pgame12", endswt, pgame12.getNext());
        assertTrue("pgame22 is not predecessor of join", endswt.getChoices().contains(pgame22));
        assertEquals("end switch is not successor of pgame22", endswt, pgame22.getNext());
    }//testPMethod5()
    
    
    /**
     * Test Case: pmethod, composite
     * 
     * @throws Exception
     */
    @Test
    public void testPMethod6() throws Exception {
        String xml =
            "<pmethod name=\" pmethod 1 \" description=\" pmethod 1 \">"
            + "    <sequence>"
            + "        <pgame name=\" pgame 2 \"/>"
            + "        <pgame name=\" pgame 1 \"/>"
            + "        <switch>"
            + "            <sequence>"
            + "                <pgame name=\" pgame 1 \"/>"
            + "                <branch>"
            + "                    <sequence>"
            + "                        <pgame name=\" pgame 1 \"/>"
            + "                        <while>"
            + "                            <pgame name=\" pgame 1 \"/>"
            + "                            <pgame name=\" pgame 2 \"/>"
            + "                        </while>"
            + "                        <pgame name=\" pgame 2 \"/>"
            + "                    </sequence>"
            + "                    <sequence>"
            + "                        <pgame name=\" pgame 2 \"/>"
            + "                        <repeat>"
            + "                            <pgame name=\" pgame 2 \"/>"
            + "                            <pgame name=\" pgame 1 \"/>"
            + "                        </repeat>"
            + "                        <pgame name=\" pgame 1 \"/>"
            + "                    </sequence>"
            + "                </branch>"
            + "                <pgame name=\" pgame 2 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <pgame name=\" pgame 2 \"/>"
            + "                <pgame name=\" pgame 1 \"/>"
            + "            </sequence>"
            + "            <sequence>"
            + "                <pgame name=\" pgame 2 \"/>"
            + "                <branch>"
            + "                    <sequence>"
            + "                        <pgame name=\" pgame 1 \"/>"
            + "                        <while>"
            + "                            <pgame name=\" pgame 1 \"/>"
            + "                            <pgame name=\" pgame 2 \"/>"
            + "                        </while>"
            + "                        <pgame name=\" pgame 2 \"/>"
            + "                    </sequence>"
            + "                    <sequence>"
            + "                        <pgame name=\" pgame 2 \"/>"
            + "                        <repeat>"
            + "                            <pgame name=\" pgame 2 \"/>"
            + "                            <pgame name=\" pgame 1 \"/>"
            + "                        </repeat>"
            + "                        <pgame name=\" pgame 1 \"/>"
            + "                    </sequence>"
            + "                </branch>"
            + "                <pgame name=\" pgame 1 \"/>"
            + "            </sequence>"
            + "        </switch>"
            + "        <pgame name=\" pgame 1 \"/>"
            + "        <pgame name=\" pgame 2 \"/>"
            + "    </sequence>"
            + "</pmethod>";
        
        Document doc = TestHelper.getDocument(xml);
        
        GroupActivity activity = parser.parse(doc.getRootElement());
        
        assertNotNull("activity is null", activity);
        assertNotNull("activity.context is null", activity.getContext());
        assertNotNull("activity.declaration is null", activity.getDeclaration());
        assertNotNull("activity.context.environment is null", activity.getContext().getEnvironment());
        
        assertTrue("activity is not 'pmethod'", activity.getLevel()==GroupActivity.LEVEL_PMETHOD);
        
        assertEquals("'name' incorrect", "pmethod 1", activity.getName());
        
        assertNotNull("group's head activity is null", activity.getHeadActivity());
        assertNotNull("group's tail activity is null", activity.getTailActivity());
        
        assertNotSame("tail is the same as head", activity.getHeadActivity(), activity.getTailActivity());
        
        //TODO
    }//testPMethod6()
    
    
}//class TestGroupParser
