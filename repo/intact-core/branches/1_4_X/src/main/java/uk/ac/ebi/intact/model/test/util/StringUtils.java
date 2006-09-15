// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.model.test.util;

/**
 * TODO document this ;o)
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class StringUtils {

    /**
     * Generate a random String of specified length.
     *
     * @param length the length of the required string.
     * @return a string of length <code>length</code>
     */
    public static String generateStringOfLength( int length ) {

        if( length < 0 ) {
            throw new IllegalArgumentException( "length must be greater than zero." );
        }

        StringBuffer sb = new StringBuffer( length );

        for( int i = 0; i < length; i++ ) {
            sb.append( "A" );
        }

        return sb.toString();
    }
}