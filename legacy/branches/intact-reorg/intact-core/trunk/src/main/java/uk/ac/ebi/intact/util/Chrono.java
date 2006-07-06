/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

/**
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class Chrono {

    private static final long NO_TIME_SET = -1;

    private long start = NO_TIME_SET;
    private long stop = NO_TIME_SET;

    public void reset() {
        start = stop = NO_TIME_SET;
    }

    public long start() {
        return ( start = System.currentTimeMillis() );
    }

    public long stop() {
        return ( stop = System.currentTimeMillis() );
    }

    public boolean hasTime() {
        return ( ( start != NO_TIME_SET ) && ( stop != NO_TIME_SET ) );
    }

    @Override
    public String toString() {
        if( hasTime() ) {
            return printTime( stop - start );
        } else {
            long current = System.currentTimeMillis();
            return printTime( current - start );
        }
    }

    public String printTime( long delay ) {
        StringBuffer sb = new StringBuffer();

        if( delay < 1000 ) {
            sb.append(delay).append(" ms");
        } else {
            long d1 = delay / 1000;
            long ms = ( delay - d1 * 1000 );
            if( d1 < 60 ) {
                sb.append(d1).append(" s ").append(ms).append(" ms");
            } else {
                long mins = d1 / 60;
                long secs = d1 - mins * 60;
                if( mins < 60 ) {
                    sb.append(mins).append(" min. ").append(secs).append(" s");
                } else {
                    long hours = mins / 60;
                    mins = mins - hours * 60;
                    sb.append(hours).append(" h ").append(mins).append(" min. ").append(secs).append(" s");
                }
            }
        }
        return sb.toString();
    }

    public String printTimeInMinutes( long delay ) {
        StringBuffer sb = new StringBuffer();
        double d1 = delay / 60000;
        sb.append(d1).append(" min.");
        return sb.toString();
    }

    public String printTimeInSeconds( long delay ) {
        StringBuffer sb = new StringBuffer();
        double d1 = delay / 1000;
        sb.append(d1).append(" sec.");
        return sb.toString();
    }

}
