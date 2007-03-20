package org.pgist.tests.wfengine.parser;

import static org.junit.Assert.*;

import org.dom4j.Document;
import org.junit.Test;
import org.pgist.tests.wfengine.TestHelper;
import org.pgist.wfengine.Environment;
import org.pgist.wfengine.parser.EnvironmentParser;


/**
 * Test for EnvironmentParser
 * 
 * @author kenny
 */
public class TestEnvironmentParser {
    
    
    private EnvironmentParser parser = new EnvironmentParser();
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    /**
     * Test Case: Environment is empty.
     * 
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        Document doc = TestHelper.getDocument("<environment/>");
        
        Environment environment = parser.parse(doc.getRootElement());
        
        assertNotNull("'declaration' is null", environment);
        
        assertNotNull("'string values' is null", environment.getStrValues());
        assertNotNull("'integer values' is null", environment.getIntValues());
        
        assertTrue("'string values' is not empty", environment.getStrValues().size()==0);
        assertTrue("'integer values' is not empty", environment.getIntValues().size()==0);
    }//test1()
    
    
    /**
     * Test Case: Environment contains only "var" elements.
     * 
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        String xml =
            "<environment>"
            + "    <var type=\" integer \" name=\" var_1 \" value=\" 100 \"/>"
            + "    <var type=\" integer \" name=\" var_2 \" value=\" 200 \"/>"
            + "    <var type=\" string \" name=\" var_3 \" value=\" value_3 \"/>"
            + "    <var type=\" string \" name=\" var_4 \" value=\" value_4 \"/>"
            + "    <var name=\" var_5 \" value=\" value_5 \"/>"
            + "    <var name=\" var_6 \" value=\" value_6 \"/>"
            + "</environment>";
        
        Document doc = TestHelper.getDocument(xml);
        
        Environment environment = parser.parse(doc.getRootElement());
        
        assertNotNull("'declaration' is null", environment);
        
        assertNotNull("'string values' is null", environment.getStrValues());
        assertNotNull("'integer values' is null", environment.getIntValues());
        
        assertTrue("values total size incorrect", environment.getStrValues().size()+environment.getIntValues().size()==6);
        assertTrue("'string values' size incorrect", environment.getStrValues().size()==4);
        assertTrue("'integer values' size incorrect", environment.getIntValues().size()==2);
        
        assertTrue("int value 'var_1' incorrect", environment.getIntValues().get("var_1")==100);
        assertTrue("int value 'var_2' incorrect", environment.getIntValues().get("var_2")==200);
        
        assertEquals("string value 'var_3' incorrect", "value_3", environment.getStrValues().get("var_3"));
        assertEquals("string value 'var_4' incorrect", "value_4", environment.getStrValues().get("var_4"));
        assertEquals("string value 'var_5' incorrect", "value_5", environment.getStrValues().get("var_5"));
        assertEquals("string value 'var_6' incorrect", "value_6", environment.getStrValues().get("var_6"));
        //TODO, check each value
    }//test2()
    
    
}//class TestPGameParser
