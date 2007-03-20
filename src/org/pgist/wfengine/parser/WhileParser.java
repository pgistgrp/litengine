package org.pgist.wfengine.parser;

import org.dom4j.Element;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.activity.WhileActivity;


/**
 * 
 * @author kenny
 *
 */
public class WhileParser {
    
    
    private ParserSuite suite;
    
    
    public ParserSuite getSuite() {
        return suite;
    }
    
    
    public void setSuite(ParserSuite suite) {
        this.suite = suite;
    }
    
    
    /*
     * ------------------------------------------------------------------------
     */


    public WhileActivity parsePGames(Element rootElement) {
        return null;
    }//parsePGames()
    
    
}//class WhileParser
