/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.business.data;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Subclassing Hashtable to provide a more elegant toString - that's all ;-)
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class MyHashtable extends Hashtable {

    public String toString() {
        StringBuffer result = new StringBuffer();
        Enumeration enumer = this.keys();
        Collection values = this.values();
        for (Iterator iterator = values.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            result.append("key: " + enumer.nextElement().toString());
            result.append("  ->  value: " + o.toString() + "\n");
        }
        return result.toString();
    }
}
