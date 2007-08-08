package org.pgist.wfengine.parser;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Declaration;
import org.pgist.wfengine.FlowPiece;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.activity.BranchActivity;
import org.pgist.wfengine.activity.EndSwitchActivity;
import org.pgist.wfengine.activity.JoinActivity;
import org.pgist.wfengine.activity.LoopActivity;
import org.pgist.wfengine.activity.MeetingActivity;
import org.pgist.wfengine.activity.PMethodActivity;
import org.pgist.wfengine.activity.RepeatActivity;
import org.pgist.wfengine.activity.SwitchActivity;
import org.pgist.wfengine.activity.UntilActivity;
import org.pgist.wfengine.activity.WhileActivity;


/**
 * Parser for MeetingActivity
 * 
 * @author kenny
 */
public class MeetingParser {
    
    
    private DeclarationParser declParser;
    
    private EnvironmentParser envParser;
    
    private Map<String, PMethodActivity> pmethods;
    
    
    /*
     * ------------------------ Getters and Setters ---------------------------
     */
    
    
    public void setDeclParser(DeclarationParser declParser) {
        this.declParser = declParser;
    }


    public void setEnvParser(EnvironmentParser envParser) {
        this.envParser = envParser;
    }


    public void setPmethods(Map<String, PMethodActivity> pmethods) {
        this.pmethods = pmethods;
    }


    /*
     * ------------------------------------------------------------------------
     */
    
    
    public FlowPiece parseSequence(Element sequenceElement) throws ParserException {
        if (sequenceElement==null) return null;
        
        FlowPiece piece = new FlowPiece();
        
        List<Element> nodes = sequenceElement.elements();
        
        for (Element node : nodes) {
            String nodeName = node.getName();
            
            if ("pmethod".equalsIgnoreCase(nodeName)) {
                String attrName = node.attributeValue("name");
                if (attrName==null) throw new ParserException("attribute 'name' of pmethod is required");
                attrName = attrName.trim();
                if (attrName.length()==0) throw new ParserException("attribute 'name' of pmethod is required");
                
                PMethodActivity pmethod = pmethods.get(attrName);
                if (pmethod==null) throw new ParserException("pmethod '"+attrName+"' is not found");
                
                PMethodActivity newPmethod = new PMethodActivity();
                newPmethod.setName(pmethod.getName());
                newPmethod.setDescription(pmethod.getDescription());
                newPmethod.setAccess(pmethod.getAccess());
                newPmethod.setDefinition(pmethod);
                
                SingleIn singleIn = (SingleIn) newPmethod;
                SingleOut singleOut = (SingleOut) newPmethod;
                
                //declaration override
                Element declElement = node.element("declaration");
                if (declElement!=null) {
                    Declaration decl = declParser.parse(declElement);
                    newPmethod.getContext().getDeclaration().getIns().putAll(decl.getIns());
                    newPmethod.getContext().getDeclaration().getOuts().putAll(decl.getOuts());
                }
                
                if (piece.getHead()==null) {
                    piece.setHead(singleIn);
                    piece.setTail(singleOut);
                } else {
                    piece.getTail().setNext(newPmethod);
                    singleIn.setPrev((Activity) piece.getTail());
                    piece.setTail(singleOut);
                }
            } else if ("while".equalsIgnoreCase(nodeName)) {
                WhileActivity whilst = new WhileActivity();
                LoopActivity loop = new LoopActivity();
                
                whilst.setCounts(0);
                whilst.setLoop(loop);
                
                loop.setCounts(0);
                loop.setWhilst(whilst);
                
                FlowPiece body = parseSequence(node.element("sequence"));
                
                if (body!=null) {
                    whilst.setNext((Activity) body.getHead());
                    body.getHead().setPrev(whilst);
                    loop.setPrev((Activity) body.getTail());
                    body.getTail().setNext(loop);
                } else {
                    whilst.setNext(loop);
                    loop.setPrev(whilst);
                }
                
                if (piece.getHead()==null) {
                    piece.setHead(whilst);
                    piece.setTail(loop);
                } else {
                    whilst.setPrev((Activity) piece.getTail());
                    piece.getTail().setNext(whilst);
                    piece.setTail(loop);
                }
            } else if ("repeat".equalsIgnoreCase(nodeName)) {
                RepeatActivity repeat = new RepeatActivity();
                UntilActivity until = new UntilActivity();
                
                repeat.setCounts(0);
                repeat.setUntil(until);
                
                until.setCounts(0);
                until.setRepeat(repeat);
                
                FlowPiece body = parseSequence(node.element("sequence"));
                
                if (body!=null) {
                    repeat.setNext((Activity) body.getHead());
                    body.getHead().setPrev(repeat);
                    until.setPrev((Activity) body.getTail());
                    body.getTail().setNext(until);
                } else {
                    repeat.setNext(until);
                    until.setPrev(repeat);
                }
                
                if (piece.getHead()==null) {
                    piece.setHead(repeat);
                    piece.setTail(until);
                } else {
                    repeat.setPrev((Activity) piece.getTail());
                    piece.getTail().setNext(repeat);
                    piece.setTail(until);
                }
            } else if ("branch".equalsIgnoreCase(nodeName)) {
                BranchActivity branch = new BranchActivity();
                JoinActivity join = new JoinActivity();
                
                branch.setCounts(0);
                branch.setJoinActivity(join);
                
                join.setCounts(0);
                join.setBranchActivity(branch);
                
                List<Element> elements = node.elements("sequence");
                
                for (Element seqElement : elements) {
                    FlowPiece body = parseSequence(seqElement);
                    
                    if (body!=null) {
                        branch.getBranches().add((Activity) body.getHead());
                        body.getHead().setPrev(branch);
                        join.getJoins().add((Activity) body.getTail());
                        body.getTail().setNext(join);
                    }
                }//for
                
                if (piece.getHead()==null) {
                    piece.setHead(branch);
                    piece.setTail(join);
                } else {
                    branch.setPrev((Activity) piece.getTail());
                    piece.getTail().setNext(branch);
                    piece.setTail(join);
                }
            } else if ("switch".equalsIgnoreCase(nodeName)) {
                SwitchActivity swtch = new SwitchActivity();
                EndSwitchActivity eswtch = new EndSwitchActivity();
                
                swtch.setCounts(0);
                swtch.setEndSwitchActivity(eswtch);
                
                eswtch.setCounts(0);
                eswtch.setSwitchActivity(swtch);
                
                List<Element> elements = node.elements("sequence");
                
                for (Element seqElement : elements) {
                    FlowPiece body = parseSequence(seqElement);
                    
                    if (body!=null) {
                        swtch.getSwitches().add((Activity) body.getHead());
                        body.getHead().setPrev(swtch);
                        eswtch.getChoices().add((Activity) body.getTail());
                        body.getTail().setNext(eswtch);
                    }
                }//for
                
                if (piece.getHead()==null) {
                    piece.setHead(swtch);
                    piece.setTail(eswtch);
                } else {
                    swtch.setPrev((Activity) piece.getTail());
                    piece.getTail().setNext(swtch);
                    piece.setTail(eswtch);
                }
            } else {
                throw new ParserException("invalid element in sequence");
            }
        }//for
        
        return piece;
    }//parseSequence()
    
    
    /*
     * ------------------------------------------------------------------------
     */
    
    
    public MeetingActivity parse(Element rootElement) throws ParserException {
        MeetingActivity meeting = new MeetingActivity();
        
        //name
        String name = rootElement.attributeValue("name");
        if (name==null) throw new ParserException("attribute 'name' for 'meeting' is required");
        name = name.trim();
        if (name.length()==0) throw new ParserException("attribute 'name' for 'meeting' is required");
        meeting.setName(name);
        
        //desc
        String desc = rootElement.attributeValue("description");
        if (desc==null) throw new ParserException("attribute 'description' for 'meeting' is required");
        desc = desc.trim();
        if (desc.length()==0) throw new ParserException("attribute 'description' for 'meeting' is required");
        meeting.setDescription(desc);
        
        //access
        String access = rootElement.attributeValue("access");
        if (access==null) access = "all";
        desc = desc.trim();
        if (desc.length()==0) access = "all";
        meeting.setAccess(access);
        
        //environment
        Element envElement = rootElement.element("environment");
        if (envElement!=null) {
            meeting.getContext().getInitEnvironment().getIntValues().putAll(envParser.parse(envElement).getIntValues());
            meeting.getContext().getInitEnvironment().getStrValues().putAll(envParser.parse(envElement).getStrValues());
        }
        
        //declaration
        Element declElement = rootElement.element("declaration");
        if (declElement!=null) {
            meeting.getContext().getDeclaration().getIns().putAll(declParser.parse(declElement).getIns());
            meeting.getContext().getDeclaration().getOuts().putAll(declParser.parse(declElement).getOuts());
        }
        
        //sequence
        FlowPiece piece = parseSequence(rootElement.element("sequence"));
        
        meeting.setHeadActivity((Activity) piece.getHead());
        meeting.setTailActivity((Activity) piece.getTail());
        
        return meeting;
    }//parse()


}//class MeetingParser
