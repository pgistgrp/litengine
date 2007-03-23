package org.pgist.wfengine;



/**
 * 
 * @author kenny
 *
 */
public interface Declarable {
    
    
    String getName();
    
    void setName(String name);
    
    String getDescription();
    
    void setDescription(String description);
    
    Declaration getDeclaration();
    
    void setDeclaration(Declaration declaration);
    
    
}//interface Declarable
