/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class SearchReplace {

    /**
     * Managment of search replace with regular expression
     */
    private static Pattern escaper = Pattern.compile("([^a-zA-z0-9])");
    /**
     * Escape all non alphabetical caracters.
     * ${TEST} becomes \$\{TEST\}, so \\$\\{TEST\\} in java !
     *
     * @param str the pattern to modify
     * @return an new pattern with all non alphabetical caracters protected with a '\'.
     */
    private static String escapeRE (String str) {
        return escaper.matcher(str).replaceAll("\\\\$1");
    }

    /**
     * Perform a search replace on a text
     *
     * @param text the text to work on.
     * @param patternStr the string to look for
     * @param replacement the replacement string
     * @return the modified text
     */
    public static String replace (String text, String patternStr, String replacement) {
        String escapedPatternStr = escapeRE(patternStr);

        // Compile regular expression
        Pattern pattern = Pattern.compile (escapedPatternStr);

        // Replace all occurrences of pattern in input
        Matcher matcher = pattern.matcher (text);
        String result   = matcher.replaceAll (replacement);

        return result;
    }
}
