package org.pgist.tests.wfengine.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.dom4j.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pgist.tests.wfengine.TestHelper;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.activity.PAutoGameActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.PManualGameActivity;
import org.pgist.wfengine.parser.DeclarationParser;
import org.pgist.wfengine.parser.PGameParser;


/**
 * Test for PGameParser.
 * 
 * @author kenny
 */
public class TestPGameParser {
    
    
    private static PGameParser parser;
    
    
    @BeforeClass
    public static void setUp() {
        parser = new PGameParser();
        
        DeclarationParser declParser = new DeclarationParser();
        parser.setDeclParser(declParser);
    }//setUp()
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    /**
     * Test Case: An automatic PGame activity
     * 
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        String xml =
            "<pgame name=\" pgame1 \" description=\" pgame1 \" auto=\" true \">"
          + "    <taskName> task1 </taskName>"
          + "    <declaration>"
          + "        <ins>"
          + "            <var name=\" in_var_1 \"/>"
          + "        </ins>"
          + "        <outs>"
          + "            <var name=\" out_var_1 \"/>"
          + "        </outs>"
          + "    </declaration>"
          + "</pgame>";
        
        Document doc = TestHelper.getDocument(xml);
        
        PGameActivity pgame = parser.parse(doc.getRootElement());
        
        assertNotNull("pgame is null", pgame);
        assertNotNull("declaration is null", pgame.getDeclaration());
        
        assertTrue("pgame is not an auto one", pgame instanceof PAutoGameActivity);
        
        assertEquals("pgame 'name' incorrect", "pgame1", pgame.getName());
        assertEquals("pgame 'description' incorrect", "pgame1", pgame.getDescription());
        
        assertTrue("member counts of pgame is not 0", pgame.getCounts()==0);
        assertTrue("member type of pgame is not "+Activity.TYPE_PAUTOGAME, pgame.getType()==Activity.TYPE_PAUTOGAME);
        
        assertEquals("ins value 'in_var_1' incorrect", "in_var_1", pgame.getDeclaration().getIns().get("in_var_1"));
        assertEquals("outs value 'out_var_1' incorrect", "out_var_1", pgame.getDeclaration().getOuts().get("out_var_1"));
        
        PAutoGameActivity activity = (PAutoGameActivity) pgame;
        assertEquals("task name incorrect", "task1", activity.getTaskName());
    }//test1()
    
    
    /**
     * Test Case: An manual PGame activity
     * 
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        String xml =
            "<pgame name=\" pgame1 \" description=\" pgame1 \" auto=\" false \" access=\" moderator \">"
          + "    <actionName> action1 </actionName>"
          + "    <declaration>"
          + "        <ins>"
          + "            <var name=\" in_var_1 \"/>"
          + "        </ins>"
          + "        <outs>"
          + "            <var name=\" out_var_1 \"/>"
          + "        </outs>"
          + "    </declaration>"
          + "    <timer extension=\"86400000\"/>"
          + "</pgame>";
        
        Document doc = TestHelper.getDocument(xml);
        
        PManualGameActivity pgame = (PManualGameActivity) parser.parse(doc.getRootElement());
        
        assertNotNull("pgame is null", pgame);
        assertNotNull("declaration is null", pgame.getDeclaration());
        
        assertTrue("pgame is not an manual one", pgame instanceof PManualGameActivity);
        
        assertEquals("pgame 'name' incorrect", "pgame1", pgame.getName());
        assertEquals("pgame 'description' incorrect", "pgame1", pgame.getDescription());
        assertEquals("pgame 'access' incorrect", "moderator", pgame.getAccess());
        assertEquals("pgame extension incorrect", 86400000L, pgame.getExtension());
        
        assertTrue("member counts of pgame is not 0", pgame.getCounts()==0);
        assertTrue("member type of pgame is not "+Activity.TYPE_PMANUALGAME, pgame.getType()==Activity.TYPE_PMANUALGAME);
        
        assertEquals("ins value 'in_var_1' incorrect", "in_var_1", pgame.getDeclaration().getIns().get("in_var_1"));
        assertEquals("outs value 'out_var_1' incorrect", "out_var_1", pgame.getDeclaration().getOuts().get("out_var_1"));
        
        PManualGameActivity activity = (PManualGameActivity) pgame;
        assertEquals("action name incorrect", "action1", activity.getActionName());
    }//test2()
    
    
}//class TestPGameParser
