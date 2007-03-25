package org.pgist.wfengine.util;

import org.hibernate.proxy.HibernateProxy;


/**
 * 
 * @author kenny
 *
 */
public class Utils {
    
    
    /**
     * For Hibernate, a persistent object is often proxied by a HibernateProxy which is used to
     * implement the lazy fetching. In order to get the concreate object of the activity subclass,
     * use this method to narrow the proxy and recover the real object.
     * 
     *  @param object
     *  @return
     */
    public static Object narrow(Object object){
        if(object instanceof HibernateProxy){
            return ((HibernateProxy)object).getHibernateLazyInitializer().getImplementation();
        } else {
            return object;
        }
    }//narrow()
    
    
}//class Utils
