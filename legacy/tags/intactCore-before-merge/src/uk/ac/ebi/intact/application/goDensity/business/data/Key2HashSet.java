/*
Copyright (c) 2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.business.data;

import uk.ac.ebi.intact.application.goDensity.exception.KeyNotFoundException;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Key2HashSet with unique (!) Values for a key.
 * That means, it is realated to a Hashtable: you have keys, and for each key you
 * get a set of unique values / Objects (internally stored in a HashSet).
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */
public class Key2HashSet {

    // =======================================================================
    // Class and object attributes
    // =======================================================================

    /**
     * All keys are stored in this Hashtable.
     */
    protected Hashtable _key2HashSet = new Hashtable();

    /**
     * Each value within the Hashtable _key2HashSet hast on HashSet - set of unique values
     */
    protected HashSet _values;

    //	=======================================================================
    // Constructor
    // =======================================================================

    /**
     * default Constructor
     */
    public Key2HashSet() {
        _key2HashSet = new Hashtable();
    }

    // =======================================================================
    // Public interface
    // =======================================================================

    /**
     * Allows to add a key / value pair.
     * @param key - the keyword
     * @param value - Object, which should be added for the key parameter.
     * Object "value" must implement the equals(Object o) and hasCode() method,
     * otherwise it will not be added! If key has already a value, this
     * value will be added as long as it is not already there (HashSet)
     * @return if key/value pair was already contained<br>
     * - true -> if the set did not already contain the specified element.
     */
    public boolean add(String key, Object value) {
        boolean notAlreadyRecorded;

        //if key is already defined in Hashtable, get the related values and add new value
        if (_key2HashSet.containsKey(key)) {
            _values = (HashSet) _key2HashSet.get(key);
            // notAlreadyRecorded:  true if the set did not already contain the specified element
            notAlreadyRecorded = (_values.add(value));
        }
        //if key is not already defined in key Hashtable
        else {
            _values = new HashSet();
            // notAlreadyRecorded:  true if the set did not already contain the specified element
            notAlreadyRecorded = _values.add(value);
            _key2HashSet.put(key, _values);
        }
        return notAlreadyRecorded;
    }

    /**
     * Allows to get the related HashSet by the unique key.
     * @param key - key
     * @return HashSet of Objects which belongs to one unique key
     * @throws KeyNotFoundException - if key was not found
     */
    public HashSet getValueByKey(String key) throws KeyNotFoundException {
        //todo: search for *
        if (_key2HashSet.containsKey(key))
            return ((HashSet) _key2HashSet.get(key));
        else
            throw new KeyNotFoundException("error: key \"" + key + "\" was not found");
    }

    /**
     * Allows to get all HashSets for all keys and all keys itself.
     * @return Hashtable: <br>
     *          - keys (String): the key for accessing one HashSet<br>
     *          - values: a HashSet for each key.
     */
    public Hashtable getAll() {
        return _key2HashSet;
    }

    /**
     * @return all keys (Strings) as a Enumeration
     */
    public Enumeration getKeys() {
        return _key2HashSet.keys();
    }

    /**
     * test, if Key2HashSet already contains the String "key"
     * @param key which should be tested if it is already added
     * @return  <code>true</code> if and only if the specified key
     *          is a key in this Key2HashSet; <code>false</code> otherwise.
     */
    public boolean containsKey(String key) {
        return _key2HashSet.containsKey(key);
    }

    /**
     * toString Method for Key2HashSet
     *   <br>
     *   Example output:<br>
     *   key: ac2  ->  values: [10, 30, 20] <br>
     *   key: ac1  ->  values: [2, 1]       <br>
     *
     * @return String representation for Key2HashSet
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        Hashtable ht = this.getAll();
        Enumeration e = ht.keys();
        Collection values = ht.values();
        for (Iterator iterator = values.iterator(); iterator.hasNext();) {
            HashSet o = (HashSet) iterator.next();
            result.append("\n\nkey: " + e.nextElement().toString());
            Iterator col = o.iterator();
            while (col.hasNext()) {
                Object obj = col.next();
                result.append("\n    ->  value: " + obj.toString());
            }
        }
        return result.toString();
    }

    // =======================================================================
    // Test methods
    // =======================================================================

    /*
    public static void main(String[] args) {
        Key2HashSet test = new Key2HashSet();
        System.out.println(test.toString());

        // test add

        System.out.println(test.add("ac1", "1"));
        System.out.println(test.add("ac1", "1"));
        test.add("ac1", "2");
        test.add("ac1", "3");
        test.add("ac2", "10");
        test.add("ac2", "20");
        test.add("ac2", "20");
        System.out.println(
                "test.add(\"ac1\", \"1\"); test.add(\"ac1\", "
                + "\"1\"); test.add(\"ac1\", \"2\"); test.add(\"ac1\", \"3\");");
        System.out.println(
                "test.add(\"ac2\", \"10\"); test.add(\"ac2\", "
                + "\"20\"); test.add(\"ac2\", \"20\");");

        // test get

        System.out.println("\ntest.getValueByKey(\"ac1\") -> ");
        try {
            System.out.println(test.getValueByKey("ac1"));
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("\ntest.getValueByKey(\"ac2\") -> ");
        try {
            System.out.println(test.getValueByKey("ac2"));
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("\ntest.getValueByKey(\"ac3\") -> ");
        try {
            System.out.println(test.getValueByKey("ac3"));
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        }

        //test toString (implizit getAll)

        System.out.println("\nTest test.getAll() and test.toString():");
        System.out.println(test.getAll());
        System.out.println(test.toString());
    }
    */
}
