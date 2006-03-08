package org.pgist.wfengine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.pgist.wfengine.activity.GroupActivity;
import org.pgist.wfengine.activity.PGameActivity;


/**
 * The parser to be used to parse the XML Workflow Definition
 * 
 * @author kenny
 *
 */
public class TemplateParser {

    
    private WorkflowEngineDAO engineDAO;
    
    private Environment globalEnv = new Environment();
    
    
    public void setEngineDAO(WorkflowEngineDAO engineDAO) {
        this.engineDAO = engineDAO;
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
        
        List elements = null;
        
        //parse pgame
        /*
        elements = root.elements("pgame");
        for (int i=0, n=elements.size(); i<n; i++) {
            Element element = (Element) elements.get(i);
            
            Long refId = new Long(element.attribute("refid").getStringValue());
            if (engineDAO.getGroupActivityByRefId(new Integer(GroupActivity.LEVEL_PGAME), refId)!=null)
                throw new Exception("Another GroupActivity (pgame) has this refid: "+refId);
            
            Template template = new Template();
            template.setDeleted(false);
            template.setName(element.attribute("name").getStringValue());
            template.setDescription(element.attribute("description").getStringValue());
            template.setType(Template.TYPE_PGAME);
            
            Element sequence = (Element) element.element("sequence");
            if (sequence==null) return templates;
            
            parser.setVisitor(new ElementVisitor() {
                public Activity visit(Element element, Activity parent) throws Exception {
                    String name = element.getName();
                    if (!"pact".equalsIgnoreCase(name)) throw new Exception("element pgame can not contain element "+name+" !");
                    
                    Long id = new Long(element.attribute("template").getStringValue());
                    PActActivity ref = engineDAO.getPActActivityByRefId(id);
                    if (ref==null) throw new Exception("PActActivity template with refid "+id+" not found!");
                    
                    PActActivity activity = new PActActivity();
                    activity.setCount(0);
                    activity.setExpression(0);
                    activity.setPrev(parent);
                    activity.setName(ref.getName());
                    activity.setDescription(ref.getDescription());
                    activity.setAction(ref.getAction());
                    Task task = ref.getTask();
                    if (task!=null) activity.setTask(task.clone(activity));
                    
                    return activity;
                }//visit()
            });
            
            FlowPiece piece = parser.parse(sequence);
            
            template.setHead((Activity) piece.getHead());
            template.setTail((Activity) piece.getTail());
            
            GroupActivity game = new GroupActivity(GroupActivity.LEVEL_PGAME);
            game.setTemplate(template);
            game.setCount(0);
            game.setExpression(0);
            game.setDescription(template.getDescription());
            game.setName(template.getName());
            game.setRefId(refId);
            game.setTask(null);
            
            engineDAO.saveTemplate(template);
            engineDAO.saveActivity(game);
            
            templates.add(template);
        }//for i
        */
        
        //parse pmethod
        elements = root.elements("pmethod");
        for (int i=0, n=elements.size(); i<n; i++) {
            Element element = (Element) elements.get(i);
            
            Long refId = new Long(element.attribute("refid").getStringValue());
            if (engineDAO.getGroupActivityByRefId(new Integer(GroupActivity.LEVEL_PMETHOD), refId)!=null)
                throw new Exception("Another GroupActivity (pmethod) has this refid: "+refId);
            
            Template template = new Template();
            template.setDeleted(false);
            template.setName(element.attribute("name").getStringValue());
            template.setDescription(element.attribute("description").getStringValue());
            template.setType(Template.TYPE_PMETHOD);
            
            Element sequence = (Element) element.element("sequence");
            if (sequence==null) return templates;
            
            final Map map = new HashMap();
            
            parser.setVisitor(new ElementVisitor() {
                public Activity visit(Element element, Activity parent) throws Exception {
                    String name = element.getName();
                    if (!"pgame".equalsIgnoreCase(name)) throw new Exception("element pmethod can not contain element "+name+" !");
                    
                    Long id = new Long(element.attributeValue("id"));
                    Long tid = new Long(element.attributeValue("template"));
                    PGameActivity ref = engineDAO.getPGameActivityByRefId(tid);
                    if (ref==null) throw new Exception("PGameActivity template with refid "+tid+" not found!");
                    
                    PGameActivity activity = (PGameActivity) map.get(""+id);
                    if (activity==null) {
                        activity = new PGameActivity();
                        map.put(""+id, activity);
                    }
                    
                    activity.setType(Activity.TYPE_PGAME);
                    activity.setCount(0);
                    activity.setExpression(0);
                    activity.setPrev(parent);
                    activity.setName(ref.getName());
                    activity.setDescription(ref.getDescription());
                    activity.setAction(ref.getAction());
                    Task task = ref.getTask();
                    if (task!=null) activity.setTask(task.clone(activity));
                    
                    String depends = element.attributeValue("depends");
                    if (depends!=null) {
                        String[] s = depends.split(",");
                        for (int i=0; i<s.length; i++) {
                            Long sid = new Long(s[i]);
                            PGameActivity depend = (PGameActivity) map.get(""+sid);
                            if (depend==null) {
                                depend = new PGameActivity();
                                map.put(""+sid, depend);
                            }
                            activity.getDepends().add(depend);
                        }//for i
                    }
                    
                    return activity;
                }//visit()
            });
            
            FlowPiece piece = parser.parse(sequence);
            
            template.setHead((Activity) piece.getHead());
            template.setTail((Activity) piece.getTail());
            
            GroupActivity method = new GroupActivity(GroupActivity.LEVEL_PMETHOD);
            method.setTemplate(template);
            method.setCount(0);
            method.setExpression(0);
            method.setDescription(template.getDescription());
            method.setName(template.getName());
            method.setRefId(refId);
            method.setTask(null);
            
            engineDAO.saveTemplate(template);
            engineDAO.saveActivity(method);
            
            templates.add(template);
        }//for i
        
        //parse meeting
        elements = root.elements("meeting");
        for (int i=0, n=elements.size(); i<n; i++) {
            Element element = (Element) elements.get(i);
            
            Long refId = new Long(element.attribute("refid").getStringValue());
            if (engineDAO.getGroupActivityByRefId(new Integer(GroupActivity.LEVEL_MEETING), refId)!=null)
                throw new Exception("Another GroupActivity (meeting) has this refid: "+refId);
            
            Template template = new Template();
            template.setDeleted(false);
            template.setName(element.attribute("name").getStringValue());
            template.setDescription(element.attribute("description").getStringValue());
            template.setType(Template.TYPE_MEETING);
            
            Element sequence = (Element) element.element("sequence");
            if (sequence==null) return templates;
            
            parser.setVisitor(new ElementVisitor() {
                public Activity visit(Element element, Activity parent) throws Exception {
                    String name = element.getName();
                    if (!"pmethod".equalsIgnoreCase(name)) throw new Exception("element meeting can not contain element "+name+" !");
                    
                    Long id = new Long(element.attribute("template").getStringValue());
                    GroupActivity ref = engineDAO.getGroupActivityByRefId(new Integer(GroupActivity.LEVEL_PMETHOD), id);
                    if (ref==null) throw new Exception("GroupActivity (pmethod) template with refid "+id+" not found!");
                    
                    GroupActivity activity = new GroupActivity(GroupActivity.LEVEL_PMETHOD);
                    activity.setType(Activity.TYPE_PMETHOD);
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
            
            GroupActivity meeting = new GroupActivity(GroupActivity.LEVEL_MEETING);
            meeting.setTemplate(template);
            meeting.setCount(0);
            meeting.setExpression(0);
            meeting.setDescription(template.getDescription());
            meeting.setName(template.getName());
            meeting.setRefId(refId);
            meeting.setTask(null);
            
            engineDAO.saveTemplate(template);
            engineDAO.saveActivity(meeting);
            
            templates.add(template);
        }//for i
        
        //parse situation
        elements = root.elements("situation");
        for (int i=0, n=elements.size(); i<n; i++) {
            Element element = (Element) elements.get(i);
            
            Long refId = new Long(element.attribute("refid").getStringValue());
            if (engineDAO.getGroupActivityByRefId(new Integer(GroupActivity.LEVEL_SITUATION), refId)!=null)
                throw new Exception("Another GroupActivity (situation) has this refid: "+refId);
            
            Template template = new Template();
            template.setDeleted(false);
            template.setName(element.attribute("name").getStringValue());
            template.setDescription(element.attribute("description").getStringValue());
            template.setType(Template.TYPE_SITUATION);
            
            Element sequence = (Element) element.element("sequence");
            if (sequence==null) return templates;
            
            parser.setVisitor(new ElementVisitor() {
                public Activity visit(Element element, Activity parent) throws Exception {
                    String name = element.getName();
                    if (!"meeting".equalsIgnoreCase(name)) throw new Exception("element situation can not contain element "+name+" !");
                    
                    Long id = new Long(element.attribute("template").getStringValue());
                    GroupActivity ref = engineDAO.getGroupActivityByRefId(new Integer(GroupActivity.LEVEL_MEETING), id);
                    if (ref==null) throw new Exception("GroupActivity (meeting) template with refid "+id+" not found!");
                    
                    GroupActivity activity = new GroupActivity(GroupActivity.LEVEL_MEETING);
                    activity.setType(Activity.TYPE_MEETING);
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
            
            GroupActivity situation = new GroupActivity(GroupActivity.LEVEL_SITUATION);
            situation.setTemplate(template);
            situation.setCount(0);
            situation.setExpression(0);
            situation.setDescription(template.getDescription());
            situation.setName(template.getName());
            situation.setRefId(refId);
            situation.setTask(null);
            
            engineDAO.saveTemplate(template);
            engineDAO.saveActivity(situation);
            
            templates.add(template);
        }//for i
        
        return templates;
    }//parseTemplates()


}//class TemplateParser
