/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

/**
 * Crc64 checksum computation.
 *
 * @author Uniparc
 * @version $Id$
 */

/**
 * Crc64 checksum computation.
 */
public abstract class Crc64 {

    private static long _crc64Array [] = new long [256];

    /**
     * Initialization of _crc64Array.
     */
    static {

        for( int i = 0 ; i <= 255 ; ++i ) {
            long k = i;
            for( int j = 0 ; j < 8 ; ++j ) {
                if( (k & 1) != 0 ) {
                    k = (k >>> 1) ^ 0xd800000000000000l;
                }
                else {
                    k = k >>> 1;
                }
            }
            _crc64Array[ i ] = k;
        }
    }


    /**
     * Returns the crc64 checksum for the given sequence.
     *
     * @param sequence sequence
     * @return the crc64 checksum for the sequence
     */
    public static String getCrc64 ( String sequence ) {
        long crc64Number = 0;
        for( int i = 0; i < sequence.length(); ++i ) {
            char symbol = sequence.charAt( i );
            long a = ( crc64Number >>> 8 );
            long b = (crc64Number ^ symbol) & 0xff;
            crc64Number = a ^ _crc64Array[ (int) b ];
        }

        String crc64String = Long.toHexString( crc64Number ).toUpperCase();
        StringBuffer crc64 = new StringBuffer( "0000000000000000" );
        crc64.replace( crc64.length() - crc64String.length(),
                       crc64.length(),
                       crc64String );

        return crc64.toString();
    }
}

