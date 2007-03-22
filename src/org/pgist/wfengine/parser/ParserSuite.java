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


    public EnvironmentParser getEnvParser() {
        return envParser;
    }
    
    
    public void setEnvParser(EnvironmentParser envParser) {
        this.envParser = envParser;
    }
    
    
}//ParserSuite
