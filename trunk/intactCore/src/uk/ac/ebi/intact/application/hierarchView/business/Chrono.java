/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class Chrono {

    private static final long NO_TIME_SET = -1;

    private long start;
    private long stop;

    public void reset () {
        start = stop = NO_TIME_SET;
    }

    public long start() {
        return (start = System.currentTimeMillis());
    }

    public long stop() {
        return (stop = System.currentTimeMillis());
    }

    public boolean hasTime () {
        return ((start != NO_TIME_SET) && (stop != NO_TIME_SET));
    }

    public String toString () {
        if (hasTime())
            return printTime (stop - start);
        else {
            long current = System.currentTimeMillis();
            return printTime (current - start);
        }
    }

    public String printTime(long delay) {
        StringBuffer sb = new StringBuffer();

        if (delay<1000) sb.append (delay + " ms");
        else {
            long d1	=	delay/1000;
            long ms	= 	(delay-d1*1000);
            if (d1<60) sb.append (d1+" s " + ms+" ms");
            else {
                long d2	=	d1/60;
                long reste	=	d1-d2*60;
                if (d2<60) sb.append (d2+" min. "+ reste + " s");
                else {
                    long d3	=	d2/60;
                    long r2	=	d2-d3*60;
                    sb.append (d3+ " h " + r2+" min. "+ reste + " s");
                }
            }
        }
        return sb.toString();
    }

    public String printTimeInMinutes(long delay) {
        StringBuffer sb = new StringBuffer();
        double d1 = delay/60000;
        sb.append (d1+" min.");
        return sb.toString();
    }

    public String printTimeInSeconds(long delay) {
        StringBuffer sb = new StringBuffer();
        double d1 = delay/1000;
        sb.append (d1+" sec.");
        return sb.toString();
    }


    /**
     * DEMO
     */
    public static void main(String[] args) {
        Chrono 	chrono	=	new Chrono();

        chrono.start();

        for (int i=0;i<100000;++i)
            for (int j=0;j<10000;++j);

        chrono.stop();

        System.out.println( chrono );
    }
}
