package org.pgist.wfengine;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.pgist.wfengine.activity.PActActivity;


/**
 * The parser to be used to parse the XML PAct Definition
 * 
 * @author kenny
 *
 */
public class PActParser {
    
    
    Document document = null;
    
    List pActs = new ArrayList(10);
    
    
    public PActParser(File file) throws DocumentException {
        document = new SAXReader().read(file);
    }
    
    
    public PActParser(InputStream stream) throws DocumentException {
        document = new SAXReader().read(stream);
    }
    
    
    public PActParser(URL url) throws DocumentException {
        document = new SAXReader().read(url);
    }
    
    
    public List getPActs() {
        return pActs;
    }//getProcesses()
    
    
    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public void parse() throws Exception {
        Element root = document.getRootElement();
        parse(root);
    }//parse()


    private void parse(Element root) throws Exception {
        //parse pact
        List elements = root.elements("pact");
        for (int i=0, n=elements.size(); i<n; i++) {
            Element element = (Element) elements.get(i);
            PActActivity pact = parsePAct(element);
            pActs.add(pact);
        }//for i
    }//parseWorkflow()


    private PActActivity parsePAct(Element ele) throws Exception {
        PActActivity pact = new PActActivity();
        pact.setRefId(new Long(ele.attribute("refid").getStringValue()));
        pact.setAction(ele.elementText("action"));
        pact.setName(ele.attribute("name").getStringValue());
        pact.setDescription(ele.attribute("description").getStringValue());
        pact.setCount(0);
        pact.setExpression(0);
        pact.setType(Activity.TYPE_PACT);
        pact.setTask(null);
        
        return pact;
    }//parseProcess()
    
    
}//class PActParser
