package org.pgist.wfengine.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.pgist.wfengine.Activity;
import org.pgist.wfengine.Declaration;
import org.pgist.wfengine.FlowPiece;
import org.pgist.wfengine.activity.BranchActivity;
import org.pgist.wfengine.activity.EndSwitchActivity;
import org.pgist.wfengine.activity.GroupActivity;
import org.pgist.wfengine.activity.JoinActivity;
import org.pgist.wfengine.activity.LoopActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.RepeatActivity;
import org.pgist.wfengine.activity.SwitchActivity;
import org.pgist.wfengine.activity.UntilActivity;
import org.pgist.wfengine.activity.WhileActivity;


/**
 * Parser for sequence elements.
 * 
 * @author kenny
 */
public class SequenceParser {
    
    
    private ParserSuite suite;
    
    private Map<String, PGameActivity> pgames = new HashMap<String, PGameActivity>();
    
    private Map<String, GroupActivity> pmethods = new HashMap<String, GroupActivity>();
    
    private Map<String, GroupActivity> meetings = new HashMap<String, GroupActivity>();
    
    private Map<String, GroupActivity> situations = new HashMap<String, GroupActivity>();
    
    
    public ParserSuite getSuite() {
        return suite;
    }
    
    
    public void setSuite(ParserSuite suite) {
        this.suite = suite;
    }
    
    
    public void setMeetings(Map<String, GroupActivity> meetings) {
        this.meetings = meetings;
    }
    
    
    public void setPgames(Map<String, PGameActivity> pgames) {
        this.pgames = pgames;
    }
    
    
    public void setPmethods(Map<String, GroupActivity> pmethods) {
        this.pmethods = pmethods;
    }
    
    
    public void setSituations(Map<String, GroupActivity> situations) {
        this.situations = situations;
    }


    /*
     * ------------------------------------------------------------------------
     */
    
    
    public FlowPiece parsePGames(Element sequenceElement) throws ParserException {
        if (sequenceElement==null) return null;
        
        FlowPiece piece = new FlowPiece();
        
        List<Element> nodes = sequenceElement.elements();
        
        for (Element node : nodes) {
            String nodeName = node.getName();
            
            if ("pgame".equalsIgnoreCase(nodeName)) {
                String name = node.attributeValue("name");
                if (name==null) throw new ParserException("attribute 'name' of pgame is required");
                name = name.trim();
                if (name.length()==0) throw new ParserException("attribute 'name' of pgame is required");
                
                PGameActivity pgame = pgames.get(name);
                if (pgame==null) throw new ParserException("pgame '"+name+"' is not found");
                
                PGameActivity newGame = (PGameActivity) pgame.clone();
                
                //declaration override
                Element declElement = node.element("declaration");
                if (declElement!=null) {
                    Declaration decl = suite.getDeclParser().parse(declElement);
                    newGame.getDeclaration().getIns().putAll(decl.getIns());
                    newGame.getDeclaration().getOuts().putAll(decl.getOuts());
                }
                
                if (piece.getHead()==null) {
                    piece.setHead(newGame);
                    piece.setTail(newGame);
                } else {
                    piece.getTail().setNext(newGame);
                    newGame.setPrev((Activity) piece.getTail());
                    piece.setTail(newGame);
                }
            } else if ("while".equalsIgnoreCase(nodeName)) {
                WhileActivity whilst = new WhileActivity();
                LoopActivity loop = new LoopActivity();
                
                whilst.setCounts(0);
                whilst.setLoop(loop);
                
                loop.setCounts(0);
                loop.setWhilst(whilst);
                
                FlowPiece body = parsePGames(node.element("sequence"));
                
                if (body!=null) {
                    whilst.setNext((Activity) body.getHead());
                    body.getHead().setPrev(whilst);
                    loop.setPrev((Activity) body.getTail());
                    body.getTail().setNext(loop);
                }
                
                if (piece.getHead()==null) {
                    piece.setHead(whilst);
                    piece.setTail(loop);
                } else {
                    piece.getTail().setNext(loop);
                    piece.setTail(loop);
                }
            } else if ("repeat".equalsIgnoreCase(nodeName)) {
                RepeatActivity repeat = new RepeatActivity();
                UntilActivity until = new UntilActivity();
                
                repeat.setCounts(0);
                repeat.setUntil(until);
                
                until.setCounts(0);
                until.setRepeat(repeat);
                
                FlowPiece body = parsePGames(node.element("sequence"));
                
                if (body!=null) {
                    repeat.setNext((Activity) body.getHead());
                    body.getHead().setPrev(repeat);
                    until.setPrev((Activity) body.getTail());
                    body.getTail().setNext(until);
                }
                
                if (piece.getHead()==null) {
                    piece.setHead(repeat);
                    piece.setTail(until);
                } else {
                    piece.getTail().setNext(until);
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
                    FlowPiece body = parsePGames(seqElement);
                    
                    if (body!=null) {
                        branch.getBranches().add(body.getHead());
                        body.getHead().setPrev(branch);
                        join.getJoins().add(body.getTail());
                        body.getTail().setNext(join);
                    }
                }//for
                
                if (piece.getHead()==null) {
                    piece.setHead(branch);
                    piece.setTail(join);
                } else {
                    piece.getTail().setNext(join);
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
                    FlowPiece body = parsePGames(seqElement);
                    
                    if (body!=null) {
                        swtch.getSwitches().add(body.getHead());
                        body.getHead().setPrev(swtch);
                        eswtch.getChoices().add(body.getTail());
                        body.getTail().setNext(eswtch);
                    }
                }//for
                
                if (piece.getHead()==null) {
                    piece.setHead(swtch);
                    piece.setTail(eswtch);
                } else {
                    piece.getTail().setNext(eswtch);
                    piece.setTail(eswtch);
                }
            } else {
                throw new ParserException("invalid element in sequence");
            }
        }//for
        
        return piece;
    }//parsePGames
    
    
}//class SequenceParser
