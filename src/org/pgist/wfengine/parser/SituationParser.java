package org.pgist.wfengine.parser;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Declaration;
import org.pgist.wfengine.FlowPiece;
import org.pgist.wfengine.SingleIn;
import org.pgist.wfengine.SingleOut;
import org.pgist.wfengine.activity.*;


/**
 * Parser for SituationActivity
 * 
 * @author kenny
 */
public class SituationParser {
    
    
    private DeclarationParser declParser;
    
    private EnvironmentParser envParser;
    
    private Map<String, MeetingActivity> meetings;
    
    
    /*
     * ------------------------ Getters and Setters ---------------------------
     */
    
    
    public void setDeclParser(DeclarationParser declParser) {
        this.declParser = declParser;
    }


    public void setEnvParser(EnvironmentParser envParser) {
        this.envParser = envParser;
    }


    public void setMeetings(Map<String, MeetingActivity> meetings) {
        this.meetings = meetings;
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
            
            if ("meeting".equalsIgnoreCase(nodeName)) {
                String attrName = node.attributeValue("name");
                if (attrName==null) throw new ParserException("attribute 'name' of meeting is required");
                attrName = attrName.trim();
                if (attrName.length()==0) throw new ParserException("attribute 'name' of meeting is required");
                
                MeetingActivity meeting = meetings.get(attrName);
                if (meeting==null) throw new ParserException("meeting '"+attrName+"' is not found");
                
                MeetingActivity newMeeting = new MeetingActivity();
                newMeeting.setName(meeting.getName());
                newMeeting.setDescription(meeting.getDescription());
                newMeeting.setDefinition(meeting);
                
                SingleIn singleIn = (SingleIn) newMeeting;
                SingleOut singleOut = (SingleOut) newMeeting;
                
                //declaration override
                Element declElement = node.element("declaration");
                if (declElement!=null) {
                    Declaration decl = declParser.parse(declElement);
                    newMeeting.getDeclaration().getIns().putAll(decl.getIns());
                    newMeeting.getDeclaration().getOuts().putAll(decl.getOuts());
                }
                
                if (piece.getHead()==null) {
                    piece.setHead(singleIn);
                    piece.setTail(singleOut);
                } else {
                    piece.getTail().setNext(newMeeting);
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
    
    
    public SituationActivity parse(Element rootElement) throws ParserException {
        SituationActivity meeting = new SituationActivity();
        
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
        
        //environment
        Element envElement = rootElement.element("environment");
        if (envElement!=null) {
            meeting.getContext().setEnvironment(envParser.parse(envElement));
        }
        
        //declaration
        Element declElement = rootElement.element("declaration");
        if (declElement!=null) {
            meeting.setDeclaration(declParser.parse(declElement));
        }
        
        //sequence
        FlowPiece piece = parseSequence(rootElement.element("sequence"));
        
        meeting.setHeadActivity((Activity) piece.getHead());
        meeting.setTailActivity((Activity) piece.getTail());
        
        return meeting;
    }//parse()


}//class SituationParser
