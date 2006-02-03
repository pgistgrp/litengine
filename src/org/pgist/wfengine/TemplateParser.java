package org.pgist.wfengine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.pgist.wfengine.activity.MeetingActivity;
import org.pgist.wfengine.activity.PActActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.PMethodActivity;


/**
 * The parser to be used to parse the XML Workflow Definition
 * 
 * @author kenny
 *
 */
public class TemplateParser {

    
    private WorkflowDAO workflowDAO;
    
    private Environment globalEnv = new Environment();
    
    
    public void setWorkflowDAO(WorkflowDAO workflowDAO) {
        this.workflowDAO = workflowDAO;
    }
    
    
    public Environment getGlobalEnv() {
        return globalEnv;
    }//getGlobalEnv()


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public List parse(InputStream stream) throws Exception {
        Document document = new SAXReader().read(stream);
        return parseTemplates(document.getRootElement());
    }//parse()


    private List parseTemplates(Element root) throws Exception {
        List templates = new ArrayList(5);
        
        FlowPieceParser parser = new FlowPieceParser();
        
        //parse pgame
        List elements = root.elements("pgame");
        for (int i=0, n=elements.size(); i<n; i++) {
            Element element = (Element) elements.get(i);
            Template template = new Template();
            template.setDeleted(false);
            template.setName(element.attribute("name").getStringValue());
            template.setDescription(element.attribute("description").getStringValue());
            template.setType(Template.TEMPLATE_PGAME);
            
            Element sequence = (Element) element.element("sequence");
            if (sequence==null) return templates;
            
            parser.setVisitor(new ElementVisitor() {
                public Activity visit(Element element, Activity parent) throws Exception {
                    String name = element.getName();
                    if (!"pact".equalsIgnoreCase(name)) throw new Exception("element pgame can not contain element "+name+" !");
                    
                    Long id = new Long(element.attribute("reference").getStringValue());
                    PActActivity ref = workflowDAO.getPActActivityByRefId(id);
                    if (ref==null) throw new Exception("PActActivity referenced by refid "+id+" not found!");
                    
                    PActActivity activity = new PActActivity();
                    activity.setCount(0);
                    activity.setExpression(0);
                    activity.setPrev(parent);
                    activity.setName(ref.getName());
                    activity.setDescription(ref.getDescription());
                    Task task = ref.getTask();
                    if (task!=null) activity.setTask(task.clone(activity));
                    
                    return activity;
                }//visit()
            });
            
            FlowPiece piece = parser.parse(sequence);
            
            template.setHead((Activity) piece.getHead());
            template.setTail((Activity) piece.getTail());
            
            workflowDAO.saveTemplate(template);
            
            PGameActivity game = new PGameActivity();
            game.setTemplate(template);
            game.setCount(0);
            game.setExpression(0);
            game.setDescription(template.getDescription());
            game.setName(template.getName());
            game.setRefId(new Long(element.attribute("refid").getStringValue()));
            game.setTask(null);
            
            workflowDAO.saveActivity(game);
            
            templates.add(template);
        }//for i
        
        //parse pmethod
        elements = root.elements("pmethod");
        for (int i=0, n=elements.size(); i<n; i++) {
            Element element = (Element) elements.get(i);
            Template template = new Template();
            template.setDeleted(false);
            template.setName(element.attribute("name").getStringValue());
            template.setDescription(element.attribute("description").getStringValue());
            template.setType(Template.TEMPLATE_PMETHOD);
            
            Element sequence = (Element) element.element("sequence");
            if (sequence==null) return templates;
            
            parser.setVisitor(new ElementVisitor() {
                public Activity visit(Element element, Activity parent) throws Exception {
                    String name = element.getName();
                    if (!"pgame".equalsIgnoreCase(name)) throw new Exception("element pmethod can not contain element "+name+" !");
                    
                    Long id = new Long(element.attribute("reference").getStringValue());
                    PGameActivity ref = workflowDAO.getPGameActivityByRefId(id);
                    if (ref==null) throw new Exception("PGameActivity referenced by refid "+id+" not found!");
                    
                    PGameActivity activity = new PGameActivity();
                    activity.setCount(0);
                    activity.setExpression(0);
                    activity.setPrev(parent);
                    activity.setName(ref.getName());
                    activity.setDescription(ref.getDescription());
                    activity.setTemplate(ref.getTemplate());
                    Task task = ref.getTask();
                    if (task!=null) activity.setTask(task.clone(activity));
                    
                    return activity;
                }//visit()
            });
            
            FlowPiece piece = parser.parse(sequence);
            
            template.setHead((Activity) piece.getHead());
            template.setTail((Activity) piece.getTail());
            
            workflowDAO.saveTemplate(template);
            
            PMethodActivity method = new PMethodActivity();
            method.setTemplate(template);
            method.setCount(0);
            method.setExpression(0);
            method.setDescription(template.getDescription());
            method.setName(template.getName());
            method.setRefId(new Long(element.attribute("refid").getStringValue()));
            method.setTask(null);
            
            workflowDAO.saveActivity(method);
            
            templates.add(template);
        }//for i
        
        //parse meeting
        elements = root.elements("meeting");
        for (int i=0, n=elements.size(); i<n; i++) {
            Element element = (Element) elements.get(i);
            Template template = new Template();
            template.setDeleted(false);
            template.setName(element.attribute("name").getStringValue());
            template.setDescription(element.attribute("description").getStringValue());
            template.setType(Template.TEMPLATE_PMETHOD);
            
            Element sequence = (Element) element.element("sequence");
            if (sequence==null) return templates;
            
            parser.setVisitor(new ElementVisitor() {
                public Activity visit(Element element, Activity parent) throws Exception {
                    String name = element.getName();
                    if (!"pmethod".equalsIgnoreCase(name)) throw new Exception("element meeting can not contain element "+name+" !");
                    
                    Long id = new Long(element.attribute("reference").getStringValue());
                    PMethodActivity ref = workflowDAO.getPMethodActivityByRefId(id);
                    if (ref==null) throw new Exception("PMethodActivity referenced by refid "+id+" not found!");
                    
                    PMethodActivity activity = new PMethodActivity();
                    activity.setCount(0);
                    activity.setExpression(0);
                    activity.setPrev(parent);
                    activity.setName(ref.getName());
                    activity.setDescription(ref.getDescription());
                    activity.setTemplate(ref.getTemplate());
                    Task task = ref.getTask();
                    if (task!=null) activity.setTask(task.clone(activity));
                    
                    return activity;
                }//visit()
            });
            
            FlowPiece piece = parser.parse(sequence);
            
            template.setHead((Activity) piece.getHead());
            template.setTail((Activity) piece.getTail());
            
            workflowDAO.saveTemplate(template);
            
            MeetingActivity meeting = new MeetingActivity();
            meeting.setTemplate(template);
            meeting.setCount(0);
            meeting.setExpression(0);
            meeting.setDescription(template.getDescription());
            meeting.setName(template.getName());
            meeting.setRefId(new Long(element.attribute("refid").getStringValue()));
            meeting.setTask(null);
            
            workflowDAO.saveActivity(meeting);
            
            templates.add(template);
        }//for i
        
        /*
        //parse situation
        elements = root.elements("situation");
        for (int i=0, n=elements.size(); i<n; i++) {
            Element element = (Element) elements.get(i);
            Template template = new Template();
            template.setDeleted(false);
            template.setName(element.attribute("name").getStringValue());
            template.setDescription(element.attribute("description").getStringValue());
            template.setType(Template.TEMPLATE_PMETHOD);
            
            Element sequence = (Element) element.element("sequence");
            if (sequence==null) return templates;
            
            parser.setVisitor(new ElementVisitor() {
                public Activity visit(Element element, Activity parent) throws Exception {
                    String name = element.getName();
                    if (!"meeting".equalsIgnoreCase(name)) throw new Exception("element situation can not contain element "+name+" !");
                    
                    Long id = new Long(element.attribute("reference").getStringValue());
                    MeetingActivity ref = workflowDAO.getMeetingActivityByRefId(id);
                    if (ref==null) throw new Exception("MeetingActivity referenced by refid "+id+" not found!");
                    
                    MeetingActivity activity = new MeetingActivity();
                    activity.setCount(0);
                    activity.setExpression(0);
                    activity.setPrev(parent);
                    activity.setName(ref.getName());
                    activity.setDescription(ref.getDescription());
                    activity.setTemplate(ref.getTemplate());
                    Task task = ref.getTask();
                    if (task!=null) activity.setTask(task.clone(activity));
                    
                    return activity;
                }//visit()
            });
            
            FlowPiece piece = parser.parse(sequence);
            
            template.setHead((Activity) piece.getHead());
            template.setTail((Activity) piece.getTail());
            
            workflowDAO.saveTemplate(template);
            
            SituationActivity meeting = new SituationActivity();
            meeting.setTemplate(template);
            meeting.setCount(0);
            meeting.setExpression(0);
            meeting.setDescription(template.getDescription());
            meeting.setName(template.getName());
            meeting.setRefId(new Long(element.attribute("refid").getStringValue()));
            meeting.setTask(null);
            
            workflowDAO.saveActivity(meeting);
            
            templates.add(template);
        }//for i
        */
        
        return templates;
    }//parseTemplates()


}//class TemplateParser
