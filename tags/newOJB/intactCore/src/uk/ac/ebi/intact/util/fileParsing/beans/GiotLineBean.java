/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.fileParsing.beans;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class GiotLineBean {

    public static final int NO_ORIENTATION               = 0;
    public static final int MONO_DIRECTIONAL_ORIENTATION = 1;
    public static final int BI_DIRECTIONAL_ORIENTATION   = 2;

    private String line;

    private String cg1;
    // Those CGs are the old Flybase prediction, the very fisrt on only has to be taken into account
    private Collection additionalCg1 = new ArrayList( 3 ); // enough in 90% cases

    private String cg2;
    // Those CGs are the old Flybase prediction, the very fisrt on only has to be taken into account
    private Collection additionalCg2 = new ArrayList( 3 ); // enough in 90% cases

    private String p1;
    private String p2;

    private int norient; // NO_ORIENTATION, MONO_DIRECTIONAL_ORIENTATION, BI_DIRECTIONAL_ORIENTATION

    private int baitPrey;
    private int preyBait;

    private int prey5utr;
    private int prey3utr;
    private int bait5utr;
    private int bait3utr;

    private int baitOther;

    private String cconf;

    public GiotLineBean ( String line ) {

        this.line = line;

        StringTokenizer st = new StringTokenizer( line, "\t" ); // tab delimited field.

//        System.out.println ( "Parsing: \n" + line + "\n" );

        /* 01 */ String _cg1 = st.nextToken().trim();

        StringTokenizer st_cg1 = new StringTokenizer( _cg1, " | " ); // tab delimited field.
        cg1 = st_cg1.nextToken().trim(); // keep the first one as interactor
        while ( st_cg1.hasMoreTokens() ) {
            additionalCg2.add( st_cg1.nextToken().trim() );
        }

        /* 02 */ String _cg2 = st.nextToken().trim();

        StringTokenizer st_cg2 = new StringTokenizer( _cg2, " | " ); // tab delimited field.
        cg2 = st_cg2.nextToken().trim(); // keep the first one as interactor
        while ( st_cg2.hasMoreTokens() ) {
            additionalCg2.add( st_cg2.nextToken().trim() );
        }

        /* 03 */ p1  = st.nextToken().trim();
        /* 04 */ p2  = st.nextToken().trim();
        /* 05 */ norient  = Integer.parseInt( st.nextToken().trim() );
        /* 06 */ baitPrey = Integer.parseInt( st.nextToken().trim() );
        /* 07 */ preyBait = Integer.parseInt( st.nextToken().trim() );

        /* 08 */ st.nextToken();
        /* 09 */ st.nextToken();
        /* 10 */ st.nextToken();

        /* 11 */ bait5utr = Integer.parseInt( st.nextToken().trim() );

        /* 12 */ st.nextToken();
        /* 13 */ st.nextToken();
        /* 14 */ st.nextToken();

        /* 15 */ bait3utr  = Integer.parseInt( st.nextToken().trim() );
        /* 16 */ baitOther = Integer.parseInt( st.nextToken().trim() );

        /* 17 */ st.nextToken();

        /* 18 */ prey5utr = Integer.parseInt( st.nextToken().trim() );

        /* 19 */ st.nextToken();
        /* 20 */ st.nextToken();
        /* 21 */ st.nextToken();

        /* 22 */ prey3utr  = Integer.parseInt( st.nextToken().trim() );

        /* 23 */ st.nextToken();
        /* 24 */ st.nextToken();
        /* 25 */ st.nextToken();
        /* 26 */ st.nextToken();
        /* 27 */ st.nextToken();
        /* 28 */ st.nextToken();
        /* 29 */ st.nextToken();
        /* 30 */ st.nextToken();
        /* 31 */ st.nextToken();
        /* 32 */ st.nextToken();
        /* 33 */ st.nextToken();
        /* 34 */ st.nextToken();
        /* 35 */ st.nextToken();

        /* 36 */ cconf = st.nextToken().trim();
    }

    public String getLine () {
        return line;
    }

    public String getCg1 () {
        return cg1;
    }

    public Collection getAdditionalCg1 () {
        return additionalCg1;
    }

    public String getCg2 () {
        return cg2;
    }

    public Collection getAdditionalCg2 () {
        return additionalCg2;
    }

    public String getP1 () {
        return p1;
    }

    public String getP2 () {
        return p2;
    }

    public int getNorient () {
        return norient;
    }

    public int getBaitPrey () {
        return baitPrey;
    }

    public int getPreyBait () {
        return preyBait;
    }

    public int getPrey5utr () {
        return prey5utr;
    }

    public int getPrey3utr () {
        return prey3utr;
    }

    public int getBait5utr () {
        return bait5utr;
    }

    public int getBait3utr () {
        return bait3utr;
    }

    public int getBaitOther () {
        return baitOther;
    }

    public String getCconf () {
        return cconf;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer( 256 );

        sb.append( "\n cg1      : " + cg1 );
        sb.append( "\n cg2      : " + cg2 );
        sb.append( "\n otherscg2: " );
        for ( Iterator iterator = additionalCg2.iterator (); iterator.hasNext (); ) {
            String s = (String) iterator.next ();
            sb.append( s ).append( " " );
        }
        sb.append( "\n p1       : " + p1 );
        sb.append( "\n p2       : " + p2 );
        sb.append( "\n norient  : " + norient );
        sb.append( "\n baitPrey : " + baitPrey );
        sb.append( "\n preyBait : " + preyBait );
        sb.append( "\n prey5utr : " + prey5utr );
        sb.append( "\n prey3utr : " + prey3utr );
        sb.append( "\n bait5utr : " + bait5utr );
        sb.append( "\n bait3utr : " + bait3utr );
        sb.append( "\n baitOther: " + baitOther );
        sb.append( "\n cconf    : " + cconf );

        return sb.toString();
    }
} // GiotLineBean
