package org.pgist.wfengine.parser;

import java.util.Map;

import org.dom4j.Element;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Environment;
import org.pgist.wfengine.FlowPiece;
import org.pgist.wfengine.activity.GroupActivity;
import org.pgist.wfengine.activity.PGameActivity;

import EDU.oswego.cs.dl.util.concurrent.FJTask.Seq;


/**
 * Parser for group activities
 * 
 * @author kenny
 */
public class GroupParser {
    
    
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
    
    
    public GroupActivity parse(Element rootElement) throws ParserException {
        GroupActivity group = new GroupActivity();
        
        String groupName = rootElement.getName();
        
        //name
        String name = rootElement.attributeValue("name");
        if (name==null) throw new ParserException("attribute 'name' for '"+groupName+"' is required");
        name = name.trim();
        if (name.length()==0) throw new ParserException("attribute 'name' for '"+groupName+"' is required");
        group.setName(name);
        
        //desc
        String desc = rootElement.attributeValue("description");
        if (desc==null) throw new ParserException("attribute 'description' for '"+groupName+"' is required");
        desc = desc.trim();
        if (desc.length()==0) throw new ParserException("attribute 'description' for '"+groupName+"' is required");
        group.setDescription(desc);
        
        //environment
        Element envElement = rootElement.element("environment");
        if (envElement!=null) {
            group.getContext().setEnvironment(suite.getEnvParser().parse(envElement));
        }
        
        //declaration
        Element declElement = rootElement.element("declaration");
        if (declElement!=null) {
            group.setDeclaration(suite.getDeclParser().parse(declElement));
        }
        
        //sequence
        if ("pmethod".equalsIgnoreCase(groupName)) {
            group.setLevel(GroupActivity.LEVEL_PMETHOD);
            parsePGames(group, rootElement.element("sequence"));
        } else if ("meeting".equalsIgnoreCase(groupName)) {
            group.setLevel(GroupActivity.LEVEL_MEETING);
            parsePMethod(group, rootElement.element("sequence"));
        } else if ("situation".equalsIgnoreCase(groupName)) {
            group.setLevel(GroupActivity.LEVEL_SITUATION);
            parseMeeting(group, rootElement.element("sequence"));
        } else {
            throw new ParserException("unknown type of group activity '"+groupName+"'");
        }
        
        return group;
    }//parse()


    private void parsePGames(GroupActivity group, Element sequenceElement) throws ParserException {
        if (sequenceElement==null) return;
        
        FlowPiece piece = suite.getSequenceParser().parsePGames(sequenceElement, new PGameParseStrategy());
        
        group.setHeadActivity((Activity) piece.getHead());
        group.setTailActivity((Activity) piece.getTail());
    }//parsePGames()
    
    
    private void parsePMethod(GroupActivity group, Element sequenceElement) throws ParserException {
        if (sequenceElement==null) return;
    }//parsePMethod()


    private void parseMeeting(GroupActivity group, Element sequenceElement) throws ParserException {
        if (sequenceElement==null) return;
    }//parseMeeting()


}//class GroupParser
