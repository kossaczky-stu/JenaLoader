/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author igor
 */
public abstract class DataSource {
    /**
     * Provides access to the next data-item.
     * @return true if data-item is available. 
     */
    public abstract boolean getNext();
    
    /**
     * Returns attribute value of current data-item. 
     * @param name - name of attribute.
     * @return string-representation of attribute value
     */
    public abstract String asString(Object name);
    
    /**
     * Returns all values of a multiple-value attribute of current data-item.
     * Default implementation returns Collection.singleton with the value returned by asString.
     * @param name - name of attribute.
     * @return collection of string-representation of attribute values 
     */
    public Collection<String> asStrings(Object name) {
        String val = asString(name);
        if (val==null) return null;
        return Collections.singleton(val);
    }
    
    /**
     * Checks if datasource attribute values are unique.  
     * @param name - name of attribute.
     * @return true if datasource contains no duplicate attribute values except null-values.
     */
    protected boolean checkUnique(Object name) {
        Set<String> values = new HashSet<>();
        while (getNext()) {
            String v = asString(name);
            if (v==null) continue;
            if (values.contains(v))
                return false;
            values.add(v);
        }
        return true;
    }
    /**
     * Checks if datasource attribute contains no null-values.
     * @param name - name of attribute
     * @return true if there are no null-values.
     */
    protected boolean checkNotNull(Object name) {
        // TODO implement        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
