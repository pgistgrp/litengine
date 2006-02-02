package org.pgist.wfengine;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.pgist.wfengine.activity.BranchActivity;
import org.pgist.wfengine.activity.EndSwitchActivity;
import org.pgist.wfengine.activity.JoinActivity;
import org.pgist.wfengine.activity.LoopActivity;
import org.pgist.wfengine.activity.RepeatActivity;
import org.pgist.wfengine.activity.SequenceActivity;
import org.pgist.wfengine.activity.SwitchActivity;
import org.pgist.wfengine.activity.TerminateActivity;
import org.pgist.wfengine.activity.UntilActivity;
import org.pgist.wfengine.activity.WhileActivity;


/**
 * The parser to be used to parse the XML Workflow Definition
 * 
 * @author kenny
 *
 */
public class WFDefinitionParser {

    
    Document document = null;
    
    List processes = new ArrayList(5);
    
    Stack pushdownStack = new Stack();
    
    Stack activityStack = new Stack();
    
    Stack elementStack = new Stack();
    
    Environment globalEnv = new Environment();
    
    
    public WFDefinitionParser(File file) throws DocumentException {
        document = new SAXReader().read(file);
    }
    
    
    public WFDefinitionParser(InputStream stream) throws DocumentException {
        document = new SAXReader().read(stream);
    }
    
    
    public WFDefinitionParser(URL url) throws DocumentException {
        document = new SAXReader().read(url);
    }
    
    
    public List getProcesses() {
        return processes;
    }//getProcesses()
    
    
    public Environment getGlobalEnv() {
        return globalEnv;
    }//getGlobalEnv()


    /*
     * ------------------------------------------------------------------------------
     */
    
    
    public void parse() throws Exception {
        Element root = document.getRootElement();
        parseWorkflow(root);
    }//parse()


    private void parseWorkflow(Element root) throws Exception {
        //parse environment
        Element environment = root.element("environment");
        List vars = environment.elements("var");
        for (int i=0, n=vars.size(); i<n; i++) {
            Element var = (Element) vars.get(i);
            globalEnv.put(var.attribute("name").getStringValue(), var.attribute("value").getStringValue());
        }//for i
        
        //parse process
        List elements = root.elements("process");
        for (int i=0, n=elements.size(); i<n; i++) {
            Element element = (Element) elements.get(i);
            WFProcess process = parseProcess(element);
            processes.add(process);
        }//for i
    }//parseWorkflow()


    private WFProcess parseProcess(Element ele) throws Exception {
        WFProcess process = new WFProcess();
        process.setName(ele.attribute("name").getStringValue());
        process.setDescription(ele.attribute("description").getStringValue());
        
        //parse environment
        Element envElement = ele.element("environment");
        List vars = envElement.elements("var");
        for (int i=0, n=vars.size(); i<n; i++) {
            Element var = (Element) vars.get(i);
            String name = var.attribute("name").getText();
            String value = var.attribute("value").getText();
            process.getEnv().put(name, value);
        }//for i
        
        //parse sequence
        Element sequence = ele.element("sequence");
        FlowPiece tasks = parseSequence(sequence);
        
        process.setHead((Activity) (tasks.getHead()) );
        process.setTail((Activity) (tasks.getTail()) );
        
        return process;
    }//parseProcess()
    
    
    private FlowPiece parseSequence(Element ele) throws Exception {
        SingleIn first = null;
        SingleOut last = null;
        
        List elements = ele.elements();
        for (int i=0, n=elements.size(); i<n; i++) {
            Element one = (Element) elements.get(i);
            if ("task".equals(one.getName().toLowerCase())) {
                SequenceActivity activity = new SequenceActivity();
                String className = one.attributeValue("class");
                if (className!=null) {
                    activity.setTask((Task) Class.forName(className).newInstance());
                }
                if (first==null) {
                    first = activity;
                } else {
                    last.setNext(activity);
                    activity.setPrev((Activity)last);
                }
                last = activity;
            } else if ("branch".equals(one.getName().toLowerCase())) {
                BranchActivity branch = parseBranch(one);
                if (first==null) {
                    first = branch;
                } else {
                    branch.setPrev((Activity)last);
                    last.setNext(branch);
                }
                last = branch.getJoinActivity();
            } else if ("switch".equals(one.getName().toLowerCase())) {
                SwitchActivity switche = parseSwitch(one);
                if (first==null) {
                    first = switche;
                } else {
                    switche.setPrev((Activity)last);
                    last.setNext(switche);
                }
                last = switche.getEndSwitchActivity();
            } else if ("while".equals(one.getName().toLowerCase())) {
                WhileActivity whilst = parseWhile(one);
                if (first==null) {
                    first = whilst;
                } else {
                    whilst.setPrev((Activity)last);
                    last.setNext(whilst);
                }
                last = whilst.getLoop();
            } else if ("repeat".equals(one.getName().toLowerCase())) {
                RepeatActivity repeat = parseRepeat(one);
                if (first==null) {
                    first = repeat;
                } else {
                    repeat.setPrev((Activity)last);
                    last.setNext(repeat);
                }
                last = repeat.getUntil();
            }
        }//for i
        
        return new FlowPiece(first, last);
    }//parseSequence()
    
    
    private BranchActivity parseBranch(Element element) throws Exception {
        BranchActivity branch = new BranchActivity();
        JoinActivity join = new JoinActivity();
        branch.setJoinActivity(join);
        join.setBranchActivity(branch);
        
        Element pretask = element.element("pretask");
        if (pretask!=null) {
            String className = pretask.attributeValue("class");
            if (className!=null) {
                branch.setTask((Task) Class.forName(className).newInstance());
            }
        }
        
        Element posttask = element.element("posttask");
        if (posttask!=null) {
            String className = pretask.attributeValue("class");
            if (className!=null) {
                join.setTask((Task) Class.forName(className).newInstance());
            }
        }
        
        List sequences = element.elements("sequence");
        for (int i=0, n=sequences.size(); i<n; i++) {
            Element sequence = (Element) sequences.get(i);
            
            FlowPiece tasks = parseSequence(sequence);
            SingleIn first = tasks.getHead();
            SingleOut last = tasks.getTail();
            
            first.setPrev(branch);
            branch.getBranches().add(first);
            if ( ! (last instanceof TerminateActivity) ) {
                join.getJoins().add(last);
                last.setNext(join);
            }
        }//for i
        
        return branch;
    }//parseBranch()
    
    
    private SwitchActivity parseSwitch(Element element) throws Exception {
        SwitchActivity switche = new SwitchActivity();
        EndSwitchActivity endSwitch = new EndSwitchActivity();
        switche.setEndSwitchActivity(endSwitch);
        endSwitch.setSwitchActivity(switche);
        
        Element pretask = element.element("pretask");
        if (pretask!=null) {
            String className = pretask.attributeValue("class");
            if (className!=null) {
                switche.setTask((Task) Class.forName(className).newInstance());
            }
        }
        
        Element posttask = element.element("posttask");
        if (posttask!=null) {
            String className = pretask.attributeValue("class");
            if (className!=null) {
                endSwitch.setTask((Task) Class.forName(className).newInstance());
            }
        }
        
        List sequences = element.elements("sequence");
        for (int i=0, n=sequences.size(); i<n; i++) {
            Element sequence = (Element) sequences.get(i);
            
            FlowPiece tasks = parseSequence(sequence);
            SingleIn first = tasks.getHead();
            SingleOut last = tasks.getTail();
            
            first.setPrev(switche);
            switche.getSwitches().add(first);
            if ( ! (last instanceof TerminateActivity) ) {
                endSwitch.getChoices().add(last);
                last.setNext(endSwitch);
            }
        }//for i
        
        return switche;
    }//parseSwitch()


    private WhileActivity parseWhile(Element element) throws Exception {
        WhileActivity whilst = new WhileActivity();
        LoopActivity loop = new LoopActivity();
        whilst.setLoop(loop);
        loop.setWhilst(whilst);
        
        Element pretask = element.element("pretask");
        if (pretask!=null) {
            String className = pretask.attributeValue("class");
            if (className!=null) {
                whilst.setTask((Task) Class.forName(className).newInstance());
            }
        }
        
        Element posttask = element.element("posttask");
        if (posttask!=null) {
            String className = pretask.attributeValue("class");
            if (className!=null) {
                loop.setTask((Task) Class.forName(className).newInstance());
            }
        }
        
        Element sequence = element.element("sequence");
        
        FlowPiece tasks = parseSequence(sequence);
        SingleIn first = tasks.getHead();
        SingleOut last = tasks.getTail();
        
        first.setPrev(whilst);
        whilst.setNext((Activity) first);
        if ( ! (last instanceof TerminateActivity) ) {
            loop.setPrev((Activity) last);
            last.setNext(loop);
        }
        
        return whilst;
    }//parseWhile()


    private RepeatActivity parseRepeat(Element element) throws Exception {
        RepeatActivity repeat = new RepeatActivity();
        UntilActivity until = new UntilActivity();
        repeat.setUntil(until);
        until.setRepeat(repeat);
        
        Element pretask = element.element("pretask");
        if (pretask!=null) {
            String className = pretask.attributeValue("class");
            if (className!=null) {
                repeat.setTask((Task) Class.forName(className).newInstance());
            }
        }
        
        Element posttask = element.element("posttask");
        if (posttask!=null) {
            String className = pretask.attributeValue("class");
            if (className!=null) {
                until.setTask((Task) Class.forName(className).newInstance());
            }
        }
        
        Element sequence = element.element("sequence");
        
        FlowPiece tasks = parseSequence(sequence);
        SingleIn first = tasks.getHead();
        SingleOut last = tasks.getTail();
        
        first.setPrev(repeat);
        repeat.setNext((Activity) first);
        if ( ! (last instanceof TerminateActivity) ) {
            until.setPrev((Activity) last);
            last.setNext(until);
        }
        
        return repeat;
    }//parseRepeat()
    

}//class WFDefinitionParser
