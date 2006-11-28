/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge.adapters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>26-Oct-2006</pre>
 */
public class IdentifierChecker {

    // Protein : [OPQ][0-9][A-Z0-9]{3}[0-9]
    // Splice V: [OPQ][0-9][A-Z0-9]{3}[0-9]-[0-9]+
    // EMBL    : [A-Z]{3}[0-9]{5}
    // F. Chain: [OPQ][0-9][A-Z0-9]{3}[0-9]-PRO_[0-9]{10} | PRO_[0-9]{10}

    private static final String PROTEIN_AC_PATTERN_STR = "[OPQ][0-9][A-Z0-9]{3}[0-9]";


    private static final Pattern PROTEIN_AC_PATTERN = Pattern.compile( PROTEIN_AC_PATTERN_STR );
    private static final Pattern SPLICE_VARIANT_ID_PATTERN = Pattern.compile( PROTEIN_AC_PATTERN_STR + "-[0-9]+" );
    private static final Pattern FEATURE_CHAIN_ID_PATTERN = Pattern.compile( PROTEIN_AC_PATTERN_STR + "-PRO_[0-9]{10}|PRO_[0-9]{10}" );

    private static boolean check( Pattern pattern, String ac ) {
        if ( ac == null ) {
            throw new IllegalArgumentException( "You must give a non null Identifier to check upon." );
        }
        Matcher matcher = pattern.matcher( ac );
        return matcher.matches();
    }

    /**
     * Checks whether a protein ac is well formatted.
     *
     * @param ac the protein ac to check on.
     *
     * @return true is the syntax is correct, false otherwise.
     */
    public static boolean isProteinId( String ac ) {
        return check( PROTEIN_AC_PATTERN, ac );
    }

    /**
     * Checks whether a splice variant ac is well formatted.
     *
     * @param ac the splice variant ac to check on.
     *
     * @return true is the syntax is correct, false otherwise.
     */
    public static boolean isSpliceVariantId( String ac ) {
        return check( SPLICE_VARIANT_ID_PATTERN, ac );
    }

    /**
     * Checks whether a feature chain ac is well formatted.
     *
     * @param ac the feature chain ac to check on.
     *
     * @return true is the syntax is correct, false otherwise.
     */
    public static boolean isFeatureChainId( String ac ) {
        return check( FEATURE_CHAIN_ID_PATTERN, ac );
    }
}