package org.pgist.wfengine.parser;


/**
 * 
 * @author kenny
 *
 */
public class ParserSuite {
    
    
    private EnvironmentParser envParser;
    
    private DeclarationParser declParser;
    
    private PGameParser pgameParser;
    
    private GroupParser groupParser;
    
    private SequenceParser sequenceParser;
    
    private WhileParser whileParser;
    
    private LoopParser loopParser;
    
    private BranchParser branchParser;
    
    private SwitchParser switchParser;
    
    
    public BranchParser getBranchParser() {
        return branchParser;
    }


    public void setBranchParser(BranchParser branchParser) {
        this.branchParser = branchParser;
    }


    public DeclarationParser getDeclParser() {
        return declParser;
    }


    public void setDeclParser(DeclarationParser declParser) {
        this.declParser = declParser;
    }


    public GroupParser getGroupParser() {
        return groupParser;
    }


    public void setGroupParser(GroupParser groupParser) {
        this.groupParser = groupParser;
    }


    public LoopParser getLoopParser() {
        return loopParser;
    }


    public void setLoopParser(LoopParser loopParser) {
        this.loopParser = loopParser;
    }


    public PGameParser getPgameParser() {
        return pgameParser;
    }


    public void setPgameParser(PGameParser pgameParser) {
        this.pgameParser = pgameParser;
    }


    public SequenceParser getSequenceParser() {
        return sequenceParser;
    }


    public void setSequenceParser(SequenceParser sequenceParser) {
        this.sequenceParser = sequenceParser;
    }


    public SwitchParser getSwitchParser() {
        return switchParser;
    }


    public void setSwitchParser(SwitchParser switchParser) {
        this.switchParser = switchParser;
    }


    public WhileParser getWhileParser() {
        return whileParser;
    }


    public void setWhileParser(WhileParser whileParser) {
        this.whileParser = whileParser;
    }


    public EnvironmentParser getEnvParser() {
        return envParser;
    }
    
    
    public void setEnvParser(EnvironmentParser envParser) {
        this.envParser = envParser;
    }
    
    
}//ParserSuite
