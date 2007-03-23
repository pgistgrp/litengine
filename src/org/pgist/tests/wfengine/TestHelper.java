package org.pgist.tests.wfengine;

import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;


/**
 * 
 * @author kenny
 *
 */
public class TestHelper {
    
    
    private static SAXReader saxReader = new SAXReader();
    
    
    public static Document getDocument(String xml) throws Exception {
        return saxReader.read(new StringReader(xml));
    }//getDocument()
    
    
}//class TestHelper
