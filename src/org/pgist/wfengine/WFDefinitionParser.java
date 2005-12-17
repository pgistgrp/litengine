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
        LinearTasks tasks = parseSequence(sequence);
        
        process.setHead(tasks.getFirst());
        process.setTail(tasks.getLast());
        
        return process;
    }//parseProcess()
    
    
    private LinearTasks parseSequence(Element ele) throws Exception {
        BackTracable first = null;
        PushDownable last = null;
        
        List elements = ele.elements();
        for (int i=0, n=elements.size(); i<n; i++) {
            Element one = (Element) elements.get(i);
            if ("task".equals(one.getName().toLowerCase())) {
                SequenceActivity activity = new SequenceActivity();
                activity.setTaskName(one.attributeValue("name"));
                if (first==null) {
                    first = activity;
                } else {
                    last.setNext(activity);
                    activity.setPrev((Activity)last);
                }
                last = activity;
            } else if ("branch".equals(one.getName().toLowerCase())) {
                BranchActivity branch = parseBranch(one);
                String auto = one.attributeValue("automatic");
                branch.setAutomatic("true".equals(auto));
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
        
        return new LinearTasks(first, last);
    }//parseSequence()
    
    
    private BranchActivity parseBranch(Element element) throws Exception {
        BranchActivity branch = new BranchActivity();
        JoinActivity join = new JoinActivity();
        branch.setJoinActivity(join);
        join.setBranchActivity(branch);
        
        List sequences = element.elements("sequence");
        for (int i=0, n=sequences.size(); i<n; i++) {
            Element sequence = (Element) sequences.get(i);
            
            LinearTasks tasks = parseSequence(sequence);
            BackTracable first = tasks.getFirst();
            PushDownable last = tasks.getLast();
            
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
        
        List sequences = element.elements("sequence");
        for (int i=0, n=sequences.size(); i<n; i++) {
            Element sequence = (Element) sequences.get(i);
            
            LinearTasks tasks = parseSequence(sequence);
            BackTracable first = tasks.getFirst();
            PushDownable last = tasks.getLast();
            
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
        
        Element sequence = element.element("sequence");
        
        LinearTasks tasks = parseSequence(sequence);
        BackTracable first = tasks.getFirst();
        PushDownable last = tasks.getLast();
        
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
        
        Element sequence = element.element("sequence");
        
        LinearTasks tasks = parseSequence(sequence);
        BackTracable first = tasks.getFirst();
        PushDownable last = tasks.getLast();
        
        first.setPrev(repeat);
        repeat.setNext((Activity) first);
        if ( ! (last instanceof TerminateActivity) ) {
            until.setPrev((Activity) last);
            last.setNext(until);
        }
        
        return repeat;
    }//parseRepeat()
    

}//class WFDefinitionParser
