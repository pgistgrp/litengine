package org.pgist.tests.wfengine.parser;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.pgist.wfengine.Environment;
import org.pgist.wfengine.parser.EnvironmentParser;


/**
 * Test for EnvironmentParser
 * 
 * @author kenny
 */
public class TestEnvironmentParser {
    
    
    private EnvironmentParser parser = new EnvironmentParser();
    
    SAXReader saxReader = new SAXReader();
    
    
    private Document getDocument(String xml) throws Exception {
        return saxReader.read(new StringReader(xml));
    }//getDocument()
    
    
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
        Document doc = getDocument("<environment/>");
        
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
            + "    <var type=\"integer\" name=\" var_1 \" value=\" 100 \"/>"
            + "    <var type=\"integer\" name=\" var_2 \" value=\" 200 \"/>"
            + "    <var type=\"string\" name=\" var_3 \" value=\" value_3 \"/>"
            + "    <var type=\"string\" name=\" var_4 \" value=\" value_4 \"/>"
            + "    <var name=\" var_5 \" value=\" value_5 \"/>"
            + "    <var name=\" var_6 \" value=\" value_6 \"/>"
            + "</environment>";
        
        Document doc = getDocument(xml);
        
        Environment environment = parser.parse(doc.getRootElement());
        
        assertNotNull("'declaration' is null", environment);
        
        assertNotNull("'string values' is null", environment.getStrValues());
        assertNotNull("'integer values' is null", environment.getIntValues());
        
        assertTrue("values total size incorrect", environment.getStrValues().size()+environment.getIntValues().size()==6);
        assertTrue("'string values' size incorrect", environment.getStrValues().size()==4);
        assertTrue("'integer values' size incorrect", environment.getIntValues().size()==2);
        
        //TODO, check each value
    }//test2()
    
    
}//class TestPGameParser
