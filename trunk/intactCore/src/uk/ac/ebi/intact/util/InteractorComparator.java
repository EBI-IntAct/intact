package uk.ac.ebi.intact.util;

import java.util.*;
import uk.ac.ebi.intact.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: clewington
 * Date: 12-May-2003
 * Time: 10:34:41
 * To change this template use Options | File Templates.
 */

/**
 * This class provides a specific Comparator for  'hidden' Interactors, based on short label.
 * It should be used for Components which contain Interactor (eg Protein) instances.
 *
 * @author Chris Lewington
 */
public class InteractorComparator implements Comparator {

    /**
     * This method compares two Components and orders them according to the short label
     * alphabetic ordering of their Interactors. The comparison is not case-sensitive.
     * @param obj1
     * @param obj2
     * @return int -1 if obj1 is less than obj2, 0 if they are equal and 1 if obj1 is greater than obj2.
     * @exception ClassCastException thrown if the arguments are not Components.
     * @exception NullPointerException if either or both arguments have no Protein shortLabel set.
     */
    public int compare(Object obj1, Object obj2) {
        Interactor interactor1 = ((Component)obj1).getInteractor();
        Interactor interactor2 = ((Component)obj2).getInteractor();
        Comparator labelComparator = new ShortLabelComparator();
        return(labelComparator.compare(interactor1, interactor2));

    }
}
