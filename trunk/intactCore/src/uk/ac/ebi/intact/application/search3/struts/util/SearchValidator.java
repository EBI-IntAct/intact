package uk.ac.ebi.intact.application.search3.struts.util;

import uk.ac.ebi.intact.model.*;

/**
 * @author Michael Kleen
 * @version SearchClassValidator.java Date: Jan 25, 2005 Time: 08:21:01 AM
 */
public class SearchValidator {

    public static boolean isValid(String searchValue) {
        if (searchValue.equalsIgnoreCase("%") || searchValue.equalsIgnoreCase("")) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isIntactObject(String searchClass) {
        Class clazz = null;
        try {
            clazz = Class.forName("uk.ac.ebi.intact.model." + searchClass);
        } catch (ClassNotFoundException e1) {
            // not a valid className
            return false;
        }
        if (IntactObject.class.isAssignableFrom(clazz)) {
            return true;
        }
        // not an IntactObject
        return false;

    }

    public static boolean isSearchable(String searchClass) {
        Class clazz = null;
        try {
            clazz = Class.forName("uk.ac.ebi.intact.model." + searchClass);
        } catch (ClassNotFoundException e) {
            // not a valid className
            return false;
        }
        if (IntactObject.class.isAssignableFrom(clazz)) {

            if (Protein.class.isAssignableFrom(clazz)) {
                return true;
            }
            if (Experiment.class.isAssignableFrom(clazz)) {
                return true;
            }
            if (Interaction.class.isAssignableFrom(clazz)) {
                return true;
            }
            if (CvObject.class.isAssignableFrom(clazz)) {
                return true;
            }
        }
        // class is an IntactObject but not searchable
        return false;
    }
}


