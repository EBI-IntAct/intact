/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

/**
 *  This class provides a wrapper around a <code>String</code> object
 * for use with java Maps. The <code>String</code> class is inefficient
 * for direct use as a Map key, since it computes its hashcode every time
 * the <code>Map</code> is accessed. The <code>Key</code> caches it instead
 * to improve searching performance with Maps.
 *
 * @author Chris Lewington
 *
 */
public class Key {

    private String key;
    private int hashCode;

    public Key(String key) {

        setKey(key);
    }

    public void setKey(String key) {

        this.key = key;
        this.hashCode = key.hashCode();
    }


    public String getKey() {

        return this.key;
    }


    /**
     * returns a cached value, rather than re-computing for every call
     *
     * @return int the cached hash code
     */
    public int hashCode() {

        return hashCode;
    }

    public boolean equals(Object obj) {

        if(this == obj) return true;

        //NB instanceof is not symmetric - this is better...
        if((obj == null) || (getClass() != obj.getClass())) return false;

        //Must be right class, so check local equality
        Key other = (Key)obj;
        return (key.equals(other.getKey()));

    }


}
