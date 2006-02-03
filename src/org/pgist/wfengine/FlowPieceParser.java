package org.pgist.wfengine;

import java.util.List;

import org.dom4j.Element;
import org.pgist.wfengine.activity.BranchActivity;
import org.pgist.wfengine.activity.EndSwitchActivity;
import org.pgist.wfengine.activity.JoinActivity;
import org.pgist.wfengine.activity.LoopActivity;
import org.pgist.wfengine.activity.RepeatActivity;
import org.pgist.wfengine.activity.SwitchActivity;
import org.pgist.wfengine.activity.TerminateActivity;
import org.pgist.wfengine.activity.UntilActivity;
import org.pgist.wfengine.activity.WhileActivity;

public class FlowPieceParser {
    
    
    private ElementVisitor visitor;
    
    
    public void setVisitor(ElementVisitor visitor) {
        this.visitor = visitor;
    }
    
    
    public FlowPiece parse(Element element) throws Exception {
        return parseSequence(element);
    }//parse()
    
    
    private FlowPiece parseSequence(Element element) throws Exception {
        SingleIn first = null;
        SingleOut last = null;
        
        List elements = element.elements();
        for (int i=0, n=elements.size(); i<n; i++) {
            Element one = (Element) elements.get(i);
            String name = one.getName().toLowerCase();
            if ("branch".equals(name)) {
                BranchActivity branch = parseBranch(one);
                if (first==null) {
                    first = branch;
                } else {
                    branch.setPrev((Activity)last);
                    last.setNext(branch);
                }
                last = branch.getJoinActivity();
            } else if ("switch".equals(name)) {
                SwitchActivity switche = parseSwitch(one);
                if (first==null) {
                    first = switche;
                } else {
                    switche.setPrev((Activity)last);
                    last.setNext(switche);
                }
                last = switche.getEndSwitchActivity();
            } else if ("while".equals(name)) {
                WhileActivity whilst = parseWhile(one);
                if (first==null) {
                    first = whilst;
                } else {
                    whilst.setPrev((Activity)last);
                    last.setNext(whilst);
                }
                last = whilst.getLoop();
            } else if ("repeat".equals(name)) {
                RepeatActivity repeat = parseRepeat(one);
                if (first==null) {
                    first = repeat;
                } else {
                    repeat.setPrev((Activity)last);
                    last.setNext(repeat);
                }
                last = repeat.getUntil();
            } else {
                Activity activity = visitor.visit(one, (Activity)last);
                if (first==null) {
                    first = (SingleIn) activity;
                } else {
                    last.setNext(activity);
                }
                last = (SingleOut) activity;
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
    
    
}//class FlowPieceParser
