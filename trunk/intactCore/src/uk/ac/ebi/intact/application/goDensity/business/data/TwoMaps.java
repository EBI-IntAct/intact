/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.business.data;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * TwoMaps.java represent a data structure, which allow to map unique keys
 * to one Object (like a Hashtable) AND in addition it allows to get back
 * all key Objects for a certain value.
 * <p><pre>
 * Example:                     <br>
 * put(a, 1)                    <br>
 * put(b, 2)                    <br>
 * put(b, 4)                    <br>
 * put(c, 2)                    <br>
 * getObjectByKey(b)   -> 4     <br>
 * getObjectByValue(2) -> b, c  <br>
 * </pre></p>
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */
public class TwoMaps {

    // =======================================================================
    // Class and object attributes
    // =======================================================================

    /**
     * represents key to value (ONE single value) mapping
     */
    private Hashtable _key2Value;

    /**
     * represents the way back: value to keyS (can be many!) mapping
     */
    private Hashtable _value2Keys;

    // =======================================================================
    // Constructor
    // =======================================================================

    /**
     * standard void constructor
     */
    public TwoMaps() {
        _key2Value = new Hashtable();
        _value2Keys = new Hashtable();
    }

    // =======================================================================
    // Public interface
    // =======================================================================

    /**
     * adds an Object by a certain value Object.
     * If value Object already exists for the key, it will be overwritten.
     * If no value an no key exists yet, it will be added anyway.
     * @param key
     * @param value
     */
    public void put(Object key, Object value) {
        Object old = _key2Value.put(key, value);

        ArrayList keys = (ArrayList) _value2Keys.get(value);
        if (keys == null) {
            keys = new ArrayList();
            _value2Keys.put(value, keys);
        }
        if (old != null) {
            ArrayList keysold = (ArrayList) _value2Keys.get(old);
            keysold.remove(key);
        }
        keys.add(key);
    }

    /**
     * @param key for which the value Object want to be retrieved
     * @return Object for key; if key-value relation doesn't exist,
     * null is returned
     */
    public Object getObjectByKey(Object key) {
        return _key2Value.get(key);
    }

    /**
     * @param value for which all mapped keys want to be retrieved
     * @return key Objects as an ArrayList (Set not possible!)<br>
     * null, if value not available
     */
    public ArrayList getObjectByValue(Object value) {
        return (ArrayList) _value2Keys.get(value);
    }

    /**
     * test, if key is already added
     * @param key
     * @return true, if key is already added
     */
    public boolean contains(Object key) {
        return _key2Value.containsKey(key);
    }

    /**
     * @return how many keys-value pairs are added
     */
    public int size() {
        return _key2Value.size();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer result = new StringBuffer("\n\nkey2Value\n");
        Enumeration keys = _key2Value.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            Object value = _key2Value.get(key);
            result.append(key + " -> " + value.toString() + "\n");
        }
        result.append("\n\nvalue2Key\n");
        keys = _value2Keys.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            ArrayList values = (ArrayList) _value2Keys.get(key);
            result.append(key + " -> " + values + "\n");
        }
        return result.toString();
    }

    // =======================================================================
    // Test methods
    // =======================================================================

    /*
    public static void main(String[] args) {

        TwoMaps tm = new TwoMaps();
        tm.put("B", "1");
        tm.put("B", "2");
        tm.put("C", "2");
        tm.put("C", "1");
        tm.put("D", "1");

        System.out.println(tm.getObjectByKey("B"));
        System.out.println(tm.getObjectByValue("1"));
        System.out.println(tm.getObjectByValue("2"));
        System.out.println(tm.contains("C"));
        System.out.println(tm.contains("D"));
        System.out.println(tm.getObjectByValue("10"));
        System.out.println(tm);
    */
}
