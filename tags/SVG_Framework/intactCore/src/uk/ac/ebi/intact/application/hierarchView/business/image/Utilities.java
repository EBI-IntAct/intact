/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.image;

import java.awt.*;
import java.util.StringTokenizer;



/**
 * This class give some usefull methods
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */


public class Utilities {

    /**
     * Parse a string (number separated by comma) and create a Color Object
     * The string has to be initialized
     *
     * @param stringColor the string to parse
     * @param defaultRed default RED composite
     * @param defaultGreen default GREEN composite
     * @param defaultBlue default BLUE composite
     * @return the parsed object color
     */
    public static Color parseColor (String stringColor,
                                    int defaultRed, int defaultGreen, int defaultBlue) {

        StringTokenizer tokens = null;

        int red               = defaultRed;
        int green             = defaultGreen;
        int blue              = defaultBlue;

        tokens = new StringTokenizer(stringColor,",");

        try {
            if (tokens.hasMoreTokens()) red   = new Integer(tokens.nextToken()).intValue();
            if (tokens.hasMoreTokens()) green = new Integer(tokens.nextToken()).intValue();
            if (tokens.hasMoreTokens()) blue  = new Integer(tokens.nextToken()).intValue();
        } catch (NumberFormatException e) {
            // let the default value
        }

        return new Color(red, green, blue);
    }
}





