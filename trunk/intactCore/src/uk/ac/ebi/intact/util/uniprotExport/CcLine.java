// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.util.uniprotExport;

/**
 * TODO document this ;o)
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class CcLine implements Comparable {

    private final String ccLine;
    private final String geneName;

    public CcLine( String ccLine, String geneName ) {

        if( geneName == null ) {

        }

        this.ccLine = ccLine;
        this.geneName = geneName;
    }

    public String getCcLine() {
        return ccLine;
    }

    public String getGeneName() {
        return geneName;
    }

    /**
     * Compare two CC lines and more particularly the gene names.
     * The ordering is based after the following rules:
     * <pre>
     *    - If a protein interacts with itself, the gene name is 'Self' which appears always first.
     *    - In other cases, we do first an non case sensitive comparison,
     *                      if there are differences, keep that order.
     *                      if there are NO differences, perform a case sensitive comparison.
     * </pre>
     *
     * @param o
     * @return
     */
    public int compareTo( Object o ) {
        CcLine cc2 = null;
        cc2 = (CcLine) o;

        final String gene1 = getGeneName();
        final String gene2 = cc2.getGeneName();

        // the current string comes first if it's before in the alphabetical order

        if( gene1 == null ) {
            System.out.println( this );
        }

        if( gene1.equals( "Self" ) ) {

            return -1;

        } else if( gene2.equals( "Self" ) ) {

            return 1;

        } else {

            String lovercaseGene1 = gene1.toLowerCase();
            String lovercaseGene2 = gene2.toLowerCase();

            int score = lovercaseGene1.compareTo( lovercaseGene2 );

            if( score == 0 ) {

            } else {
                score = gene1.compareTo( gene2 );
            }

            return score;
        }
    }


    public String toString() {
        return "CcLine{" +
               "ccLine='" + ccLine + "'" +
               ", geneName='" + geneName + "'" +
               "}";
    }
}