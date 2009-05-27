/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.taxonomy;

/**
 * Utilities for the taxonomy module.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class TaxonomyUtils {

    /**
     * Answers the following question: "is the given taxid valid in the IntAct framework ?".
     *
     * @param taxid the taxid of interrest.
     *
     * @return true is the taxid is supported, false otherwise.
     */
    public static boolean isSupportedTaxid( int taxid ) {
        // -5, -4, -3, -2, -1 and 0 are defined by PSI
        // 1 and above are in the NCBI taxonomy.
        return taxid >= -5 && taxid != 0;
    }

    /**
     * Answers the following question: "is the given taxid valid in the NCBI taxonomy ?".
     *
     * @param taxid the taxid of interrest.
     *
     * @return true is the taxid is supported, false otherwise.
     */
    public static boolean isSupportedTaxonomyIdentifier( int taxid ) {
        return taxid >= 1;
    }

    /**
     * Answers the following question: "is the given taxid the root of the NCBI taxonomy ?".
     * <p/>
     * This root term has no biological significance, it just anchors all terms to a unique root term
     *
     * @param taxid the taxid of interrest.
     *
     * @return true is the taxid is the taxonomy root taxid, false otherwise.
     */
    public static boolean isTaxonomyRoot( int taxid ) {
        return taxid == 1;
    }
}