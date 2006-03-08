package org.pgist.wfengine;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
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
        PGameActivity pgame = new PGameActivity();
        pgame.setRefId(new Long(ele.attribute("refid").getStringValue()));
        pgame.setAction(ele.elementText("action"));
        pgame.setName(ele.attribute("name").getStringValue());
        pgame.setDescription(ele.attribute("description").getStringValue());
        pgame.setCount(0);
        pgame.setExpression(0);
        pgame.setType(Activity.TYPE_PGAME);
        pgame.setTask(null);
        
        if (engineDAO.getPGameActivityByRefId(pgame.getRefId())!=null) throw new Exception("Another PGameActivity has this refid: "+pgame.getRefId());
        
        String depends = ele.elementText("depends");
        if (depends!=null) {
            String[] s = depends.split(",");
            for (int i=0; i<s.length; i++) {
                System.out.println("Depends ---> "+s[i]);
                Long dependId = new Long(s[i]);
                PGameActivity one = engineDAO.getPGameActivityByRefId(dependId);
                if (one!=null) pgame.getDepends().add(one);
            }//for i
        }
        
        engineDAO.saveActivity(pgame);
        
        return pgame;
    }//parseProcess()
    
    
}//class PGameParser
