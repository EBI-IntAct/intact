/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.util;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.util.*;

/**
 * Management of the list of shortLabel of the IntAct object.
 * This is a Singleton.
 *
 * @author Samuel Kerrien (Samuel Kerrien)
 * @version $Id$
 */
public class MenuManager {

    /**
     * Class -> list of short label.
     */
    private HashMap myMenuList;

    /**
     * The unique instance of that class.
     */
    private static MenuManager ourInstance;

    /**
     * Returns only instance of this class.
     * @return an instance of this class.
     */
    public synchronized static MenuManager getInstance () {
        if ( ourInstance == null ) {
            ourInstance = new MenuManager ();
        }
        return ourInstance;
    }

    // No instantionation from outside.
    private MenuManager () {
        myMenuList = new HashMap();
    }

    /**
     * Return a Vector of all shortLabels of the class, e.g. for menus.
     *
     * @param helper Database access object
     * @return a List of short labels as Strings.
     */
    public synchronized List getMenuList(Class targetClass, IntactHelper helper)
            throws IntactException {
        // displayCacheContent();

        // Check for any previous menus for the target class.
        List menu = (List) myMenuList.get(targetClass);

        // Get all elements of the class
        Collection elements = helper.search(targetClass.getName(), "ac", "*");

        if (menu == null) {
            // No previous menu for the target class; create a new one.
            menu = new ArrayList(elements.size());
            // cache it to avoid creating a new menu.
            myMenuList.put(targetClass, menu);
        }
        else {
            // Clear previous menu.
            menu.clear();
        }
        // save all shortLabels
        for (Iterator i = elements.iterator(); i.hasNext();) {
            menu.add(((AnnotatedObject) i.next()).getShortLabel());
        }
        return menu;
    }

    /**
     * Return a Vector of all shortLabels of the class, e.g. for menus.
     *
     * @param helper Database access object
     * @param forceUpdate If true, an update of the list is forced.
     *
     * @return Vector of Strings. Each string one shortlabel.
     */
//    public Vector getMenuList( Class targetClass, IntactHelper helper, boolean forceUpdate )
//            throws IntactException {
//
//        Vector _menuList = null;
//
//        // displayCacheContent();
//
//        _menuList = (Vector) myMenuList.get( targetClass );
//
//        if (( _menuList == null) || forceUpdate) {
//            // get all elements of the class
//            Collection allElements = helper.search( targetClass.getName(), "ac", "*" );
//
//            // create the collection
//            _menuList = new Vector( allElements.size() );
//
//            // save all shortLabels
//            for (Iterator i = allElements.iterator(); i.hasNext();) {
//                _menuList.add(((AnnotatedObject) i.next()).getShortLabel());
//            }
//
//            // cache it
//            myMenuList.put( targetClass, _menuList);
//        }
//
//        return _menuList;
//    }

    /**
     * Debugging method
     */
    private void displayCacheContent() {
        System.out.println ( "Content of the cache:" );
        for ( Iterator iterator = myMenuList.keySet ().iterator (); iterator.hasNext (); ) {
            Class aClass = (Class) iterator.next ();
            Vector v = (Vector) myMenuList.get(aClass);
            String firstNames = "";
            for ( Iterator iterator2 = v.iterator (); iterator2.hasNext (); ) {
                String s = (String) iterator2.next ();
                firstNames += s + ", ";
            }
            System.out.println ( aClass.getName() + " -> " + firstNames + " ... " + v.size() + " elements");
        }
    }

}

