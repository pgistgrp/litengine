package org.pgist.wfengine.parser;

import org.dom4j.Element;
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
    
    
    private DeclarationParser declParser;
    
    
    public void setDeclParser(DeclarationParser declParser) {
        this.declParser = declParser;
    }


    /*
     * ------------------------------------------------------------------------------
     */


    public PGameActivity parse(Element pgameElement) throws Exception {
        String auto = pgameElement.attribute("auto").getStringValue();
        
        if (auto==null) auto = "true";
        else {
            auto = auto.trim();
            if (auto.length()==0) throw new ParserException("attribute 'auto' can't be empty, but can be omitted");
        }
        
        PGameActivity pgame = null;
        
        if ("true".equalsIgnoreCase(auto)) {
            PAutoGameActivity autoGame = new PAutoGameActivity();
            pgame = autoGame;
            autoGame.setTaskName(pgameElement.elementTextTrim("taskName"));
        } else {
            PManualGameActivity manualGame = new PManualGameActivity();
            pgame = manualGame;
            manualGame.setActionName(pgameElement.elementTextTrim("actionName"));
            
            //access
            String access = pgameElement.attributeValue("access");
            if (access==null) access = "all";
            access = access.trim();
            if (access.length()==0) throw new ParserException("attribute 'access' can't be empty");
            manualGame.setAccess(access);
            
            //extension
            Element timerElement = pgameElement.element("timer");
            if (timerElement!=null) {
                manualGame.setExtension(Long.parseLong(timerElement.attributeValue("extension")));
            }
            
            //revisitable
            String revisitable = pgameElement.attributeValue("revisitable");
            if (revisitable!=null && "true".equalsIgnoreCase(revisitable.trim())) {
                manualGame.setRevisitable(true);
            }
        }
        
        //name
        String name = pgameElement.attributeValue("name");
        if (name==null) throw new ParserException("attribute 'name' is required");
        name = name.trim();
        if (name.length()==0) throw new ParserException("attribute 'name' can't be empty");
        pgame.setName(name);
        
        //title
        String title = pgameElement.attributeValue("title");
        if (title==null) throw new ParserException("attribute 'title' is required");
        title = title.trim();
        if (title.length()==0) throw new ParserException("attribute 'title' can't be empty");
        pgame.setTitle(title);
        
        //description
        String desc = pgameElement.elementTextTrim("description");
        if (desc==null) desc = "";
        desc = desc.trim();
        pgame.setDescription(desc);
        
        //declaration
        Element declElement = pgameElement.element("declaration");
        if (declElement!=null) {
            Declaration decl = declParser.parse(declElement);
            pgame.setDeclaration(decl);
        }
        
        pgame.setCounts(0);
        
        return pgame;
    }//parse()
    
    
}//class PGameParser
