/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import java.util.*;

/**
 * Represents a controlled vocabulary object. CvObject is derived from
  * AnnotatedObject to allow to store annotation of the term within the
  * object itself, thus allowing to build an integrated dictionary.
  *
  *
  * @author Henning Hermjakob
 */
public abstract class CvObject extends AnnotatedObject {

    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus.
     *
     */
    protected static Vector menuList = null;

    /** Return a Vector of all shortLabels of the class, e.g. for menus.
     *
     * @param helper Database access object
     * @param forceUpdate If true, an update of the list is forced.
     *
     * @return Vector of Strings. Each string one shortlabel.
     */
    public static Vector getMenuList(Class targetClass, IntactHelper helper, boolean forceUpdate)
            throws IntactException {
        if ((menuList == null) || forceUpdate){
            menuList = new Vector();
            // get all elements of the class
            Collection allElements = helper.search(targetClass.getName(),"ac","*");
            // save all shortLabels
            for (Iterator i = allElements.iterator(); i.hasNext();) {
                CvObject o = (CvObject) i.next();
                menuList.add(o.getShortLabel());
            }
        }
        return menuList;
    }

} // end CvObject




