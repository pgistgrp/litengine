package org.pgist.wfengine;

import org.dom4j.Element;

public interface ElementVisitor {
    
    
    Activity visit(Element element, Activity parent) throws Exception;
    
    
}
