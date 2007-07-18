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
import org.pgist.wfengine.activity.PAutoGameActivity;
import org.pgist.wfengine.activity.PGameActivity;
import org.pgist.wfengine.activity.PManualGameActivity;
import org.pgist.wfengine.activity.PMethodActivity;
import org.pgist.wfengine.activity.RepeatActivity;
import org.pgist.wfengine.activity.SwitchActivity;
import org.pgist.wfengine.activity.UntilActivity;
import org.pgist.wfengine.activity.WhileActivity;


/**
 * Parser for PMethodActivity
 * 
 * @author kenny
 */
public class PMethodParser {
    
    
    private DeclarationParser declParser;
    
    private EnvironmentParser envParser;
    
    private Map<String, PGameActivity> pgames;
    
    
    /*
     * ------------------------ Getters and Setters ---------------------------
     */
    
    
    public void setDeclParser(DeclarationParser declParser) {
        this.declParser = declParser;
    }


    public void setEnvParser(EnvironmentParser envParser) {
        this.envParser = envParser;
    }


    public void setPgames(Map<String, PGameActivity> pgames) {
        this.pgames = pgames;
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
            
            if ("pgame".equalsIgnoreCase(nodeName)) {
                String attrName = node.attributeValue("name");
                if (attrName==null) throw new ParserException("attribute 'name' of pgame is required");
                attrName = attrName.trim();
                if (attrName.length()==0) throw new ParserException("attribute 'name' of pgame is required");
                
                PGameActivity pgame = pgames.get(attrName);
                if (pgame==null) throw new ParserException("pgame '"+attrName+"' is not found");
                
                PGameActivity newPgame;
                
                switch (pgame.getType()) {
                    case Activity.TYPE_PAUTOGAME:
                        PAutoGameActivity auto = new PAutoGameActivity();
                        PAutoGameActivity oldAuto = (PAutoGameActivity) pgame;
                        auto.setName(oldAuto.getName());
                        auto.setDescription(oldAuto.getDescription());
                        auto.setTaskName(oldAuto.getTaskName());
                        newPgame = auto;
                        break;
                    case Activity.TYPE_PMANUALGAME:
                        PManualGameActivity manual = new PManualGameActivity();
                        PManualGameActivity oldManual = (PManualGameActivity) pgame;
                        manual.setName(oldManual.getName());
                        manual.setDescription(oldManual.getDescription());
                        manual.setActionName(oldManual.getActionName());
                        manual.setAccess(oldManual.getAccess());
                        manual.setExtension(oldManual.getExtension());
                        newPgame = manual;
                        break;
                    default:
                        throw new ParserException("unknown pgame type "+pgame.getType());
                }
                
                SingleIn singleIn = (SingleIn) newPgame;
                SingleOut singleOut = (SingleOut) newPgame;
                
                //declaration inheritance
                newPgame.getDeclaration().getIns().putAll(pgame.getDeclaration().getIns());
                newPgame.getDeclaration().getOuts().putAll(pgame.getDeclaration().getOuts());
                newPgame.getDeclaration().getProperties().putAll(pgame.getDeclaration().getProperties());
                
                //declaration override
                Element declElement = node.element("declaration");
                if (declElement!=null) {
                    Declaration decl = declParser.parse(declElement);
                    newPgame.getDeclaration().getIns().putAll(decl.getIns());
                    newPgame.getDeclaration().getOuts().putAll(decl.getOuts());
                    newPgame.getDeclaration().getProperties().putAll(decl.getProperties());
                }
                
                if (piece.getHead()==null) {
                    piece.setHead(singleIn);
                    piece.setTail(singleOut);
                } else {
                    piece.getTail().setNext(newPgame);
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
    
    
    public PMethodActivity parse(Element rootElement) throws ParserException {
        PMethodActivity pmethod = new PMethodActivity();
        
        //name
        String name = rootElement.attributeValue("name");
        if (name==null) throw new ParserException("attribute 'name' for 'pmethod' is required");
        name = name.trim();
        if (name.length()==0) throw new ParserException("attribute 'name' for 'pmethod' is required");
        pmethod.setName(name);
        
        //desc
        String desc = rootElement.attributeValue("description");
        if (desc==null) throw new ParserException("attribute 'description' for 'pmethod' is required");
        desc = desc.trim();
        if (desc.length()==0) throw new ParserException("attribute 'description' for 'pmethod' is required");
        pmethod.setDescription(desc);
        
        //environment
        Element envElement = rootElement.element("environment");
        if (envElement!=null) {
            pmethod.getContext().getInitEnvironment().getIntValues().putAll(envParser.parse(envElement).getIntValues());
            pmethod.getContext().getInitEnvironment().getStrValues().putAll(envParser.parse(envElement).getStrValues());
        }
        
        //declaration
        Element declElement = rootElement.element("declaration");
        if (declElement!=null) {
            pmethod.getContext().getDeclaration().getIns().putAll(declParser.parse(declElement).getIns());
            pmethod.getContext().getDeclaration().getOuts().putAll(declParser.parse(declElement).getOuts());
        }
        
        //sequence
        FlowPiece piece = parseSequence(rootElement.element("sequence"));
        
        pmethod.setHeadActivity((Activity) piece.getHead());
        pmethod.setTailActivity((Activity) piece.getTail());
        
        return pmethod;
    }//parse()


}//class PMethodParser
