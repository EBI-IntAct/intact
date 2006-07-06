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
 * This class provides a specific Comparator for use with AnnotatedObjects. The
 * comparison is done on short labels, and is a typical String comparison. Common
 * use of this class is for sorting AnnotatedObjects.
 *
 * @author Chris Lewington
 */
public class ShortLabelComparator implements Comparator {

    /**
     * This method compares the two object parameters based on their short label
     * values. The comparison is not case-sensitive.
     * @param obj1
     * @param obj2
     * @return int -1 if obj1 is less than obj2, 0 if they are equal and 1 if obj1 is greater than obj2.
     * @exception ClassCastException thrown if the arguments are not AnnotatedObjects.
     * @exception NullPointerException if either or both arguments have no shortLabel set.
     */
    public int compare(Object obj1, Object obj2) {
        String label1 = ((AnnotatedObject)obj1).getShortLabel();
        String label2 = ((AnnotatedObject)obj2).getShortLabel();
        if((label1 == null) ||(label2 == null)) throw new NullPointerException("no short label(s) - cannot compare");
        return(label1.compareToIgnoreCase(label2));

    }
}
