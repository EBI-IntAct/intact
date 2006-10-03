/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Managment of search replace with regular expression.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class SearchReplace {

    /**
     * Non special caracter definition
     */
    private static Pattern escaper = Pattern.compile( "([^a-zA-z0-9])" );

    private SearchReplace()
    {
        // no instantiation allowed
    }


    /**
     * Escape all non alphabetical caracters.
     * ${TEST} becomes \$\{TEST\}, so \\$\\{TEST\\} in java !
     *
     * @param str the pattern to modify
     * @return an new pattern with all non alphabetical caracters protected with a '\'.
     */
    private static String escapeRE ( String str ) {
        return escaper.matcher( str ).replaceAll( "\\\\$1" );
    }


    /**
     * Perform a search replace on a text.
     * Takes into account special caracters.
     *
     * @param text the text to work on - must not be null.
     * @param patternStr the string to look for - must not be null.
     * @param replacement the replacement string - must not be null.
     * @return the modified String
     */
    public static String replace ( String text, String patternStr, String replacement ) {
        if ( text == null )        throw new IllegalArgumentException( "The text you want to modify must not be null!" );
        if ( patternStr == null )  throw new IllegalArgumentException( "The pattern you want to replace must not be null!" );
        if ( replacement == null ) throw new IllegalArgumentException( "The replacement string must not be null!" );

        String escapedPatternStr = escapeRE( patternStr );

        // Compile regular expression
        Pattern pattern = Pattern.compile (escapedPatternStr);

        // Replace all occurrences of pattern in input
        Matcher matcher = pattern.matcher (text);
        String result   = matcher.replaceAll (replacement);

        return result;
    }
}
