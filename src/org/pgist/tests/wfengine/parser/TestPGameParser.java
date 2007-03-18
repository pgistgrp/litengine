package org.pgist.tests.wfengine.parser;

import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Test;


/**
 * 
 * @author kenny
 *
 */
public class TestPGameParser {
    
    
    private static final String[] xmls = {
          "<environment>"
        + "    <ins>"
        + "        <var name=\"suite_id\"/>"
        + "    </ins>"
        + "    <outs>"
        + "        <var name=\"fake_id\"/>"
        + "    </outs>"
        + "</environment>",
    };
    
    
    @Test
    public void testPGameParser1() throws Exception {
        StringReader sreader = null;
        Document doc = null;
        SAXReader saxReader = new SAXReader();
        
        for (String xml : xmls) {
            sreader = new StringReader(xml);
            doc = saxReader.read(sreader);
            
        }//for
    }//testPGameParser1()
    
    
}//class TestPGameParser
