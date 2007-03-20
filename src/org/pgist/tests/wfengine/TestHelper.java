package org.pgist.tests.wfengine;

import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.pgist.wfengine.parser.BranchParser;
import org.pgist.wfengine.parser.DeclarationParser;
import org.pgist.wfengine.parser.EnvironmentParser;
import org.pgist.wfengine.parser.GroupParser;
import org.pgist.wfengine.parser.LoopParser;
import org.pgist.wfengine.parser.PGameParser;
import org.pgist.wfengine.parser.ParserSuite;
import org.pgist.wfengine.parser.SequenceParser;
import org.pgist.wfengine.parser.SwitchParser;
import org.pgist.wfengine.parser.WhileParser;


/**
 * 
 * @author kenny
 *
 */
public class TestHelper {
    
    
    private static SAXReader saxReader = new SAXReader();
    
    private static ParserSuite suite = new ParserSuite();
    
    
    static {
        //environment parser
        EnvironmentParser envParser = new EnvironmentParser();
        suite.setEnvParser(envParser);
        
        //declaration parser
        DeclarationParser declParser = new DeclarationParser();
        suite.setDeclParser(declParser);
        
        //branch parser
        BranchParser branchParser = new BranchParser();
        branchParser.setSuite(suite);
        suite.setBranchParser(branchParser);
        
        //switch parser
        SwitchParser switchParser = new SwitchParser();
        switchParser.setSuite(suite);
        suite.setSwitchParser(switchParser);
        
        //while parser
        WhileParser whileParser = new WhileParser();
        whileParser.setSuite(suite);
        suite.setWhileParser(whileParser);
        
        //loop parser
        LoopParser loopParser = new LoopParser();
        loopParser.setSuite(suite);
        suite.setLoopParser(loopParser);
        
        //sequence parser
        SequenceParser sequenceParser = new SequenceParser();
        sequenceParser.setSuite(suite);
        suite.setSequenceParser(sequenceParser);
        
        //pgame parser
        PGameParser pgameParser = new PGameParser();
        pgameParser.setSuite(suite);
        suite.setPgameParser(pgameParser);
        
        //group parser
        GroupParser groupParser = new GroupParser();
        groupParser.setSuite(suite);
        suite.setGroupParser(groupParser);
    }//static
    
    
    public static Document getDocument(String xml) throws Exception {
        return saxReader.read(new StringReader(xml));
    }//getDocument()
    
    
    public static ParserSuite getParserSuite() {
        return suite;
    }//getParserSuite()
    
    
}//class TestHelper
