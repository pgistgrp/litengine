package org.pgist.wfengine.parser;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Declaration;
import org.pgist.wfengine.activity.PAutoGameActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.PManualGameActivity;


/**
 * Parser for PGame activities
 * 
 * @author kenny
 */
public class PGameParser {
    
    
    private ParserSuite suite;
    
    
    public ParserSuite getSuite() {
        return suite;
    }
    
    
    public void setSuite(ParserSuite suite) {
        this.suite = suite;
    }
    
    
    /*
     * ------------------------------------------------------------------------------
     */


    public PGameActivity parse(Element ele) throws Exception {
        String auto = ele.attribute("auto").getStringValue();
        
        if (auto==null) auto = "true";
        else {
            auto = auto.trim();
            if (auto.length()==0) throw new ParserException("attribute 'auto' can't be empty, but can be omitted");
        }
        
        PGameActivity pgame = null;
        
        if ("true".equalsIgnoreCase(auto)) {
            PAutoGameActivity autoGame = new PAutoGameActivity();
            pgame = autoGame;
            autoGame.setTaskName(ele.elementTextTrim("taskName"));
        } else {
            PManualGameActivity manualGame = new PManualGameActivity();
            pgame = manualGame;
            manualGame.setActionName(ele.elementTextTrim("actionName"));
        }
        
        //name
        String name = ele.attributeValue("name");
        if (name==null) throw new ParserException("attribute 'name' is required");
        name = name.trim();
        if (name.length()==0) throw new ParserException("attribute 'name' can't be empty");
        pgame.setName(name);
        
        //description
        String desc = ele.attributeValue("description");
        if (desc==null) throw new ParserException("attribute 'description' is required");
        desc = desc.trim();
        if (desc.length()==0) throw new ParserException("attribute 'description' can't be empty");
        pgame.setDescription(desc);
        
        //declaration
        Element declElement = ele.element("declaration");
        if (declElement!=null) {
            Declaration decl = suite.getDeclParser().parse(declElement);
            pgame.setDeclaration(decl);
        }
        
        pgame.setCounts(0);
        pgame.setExpression(0);
        pgame.setType(Activity.TYPE_PGAME);
        
        return pgame;
    }//parse()
    
    
}//class PGameParser
