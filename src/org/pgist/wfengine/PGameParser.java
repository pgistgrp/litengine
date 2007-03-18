package org.pgist.wfengine;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.pgist.wfengine.activity.PAutoGameActivity;
import org.pgist.wfengine.activity.PGameActivity;


/**
 * The parser to be used to parse the XML pgame Definition
 * 
 * @author kenny
 *
 */
public class PGameParser {
    
    
    private WorkflowEngineDAO engineDAO;
    
    
    public PGameParser() {
    }
    
    
    public void setEngineDAO(WorkflowEngineDAO engineDAO) {
        this.engineDAO = engineDAO;
    }


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public List parse(URL url) throws Exception {
        return parse(new SAXReader().read(url));
    }//parse()
    
    
    public List parse(File file) throws Exception {
        return parse(new SAXReader().read(file));
    }//parse()
    
    
    public List parse(InputStream stream) throws Exception {
        Document document = new SAXReader().read(stream);
        return parse(document);
    }//parse()
    
    
    public List parse(Document document) throws Exception {
        Element root = document.getRootElement();
        return parse(root);
    }//parse()


    private List parse(Element root) throws Exception {
        List pGames = new ArrayList(10);
        
        //parse pgame
        List elements = root.elements("pgame");
        for (int i=0, n=elements.size(); i<n; i++) {
            Element element = (Element) elements.get(i);
            PGameActivity pGame = parsePGame(element);
            pGames.add(pGame);
        }//for i
        
        return pGames;
    }//parseWorkflow()


    private PGameActivity parsePGame(Element ele) throws Exception {
        PGameActivity pgame = new PAutoGameActivity();
        pgame.setName(ele.attribute("name").getStringValue());
        pgame.setDescription(ele.attribute("description").getStringValue());
        pgame.setCounts(0);
        pgame.setExpression(0);
        pgame.setType(Activity.TYPE_PGAME);
        
        engineDAO.saveActivity(pgame);
        
        return pgame;
    }//parseProcess()
    
    
}//class PGameParser
