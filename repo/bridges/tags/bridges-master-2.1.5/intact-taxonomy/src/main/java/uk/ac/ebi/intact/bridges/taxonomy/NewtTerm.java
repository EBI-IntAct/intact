/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.taxonomy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representation of a Newt term.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class NewtTerm extends TaxonomyTerm {

    private static final String PATTERN = "(\\d+)\\|(.*?)\\|(.*?)\\|.*";

    /**
     * Regular expression to extract taxid, common and scientific name.
     * The pattern is -- number|short_label|full_name|ignore other text
     */
    private static final Pattern REG_EXP = Pattern.compile( PATTERN );

    public NewtTerm( String newtLine ) {
        // create a dummy term, it will be overriden later
        super( 1 );

        Matcher matcher = REG_EXP.matcher( newtLine );
        if ( !matcher.matches() ) {
            throw new IllegalArgumentException( "Could not parse: " + newtLine + " (pattern:"+ PATTERN +")" );
        }

        // Values from newt stored in
        setTaxid( Integer.parseInt( matcher.group( 1 ) ) );
        setCommonName( matcher.group( 2 ) );
        setScientificName( matcher.group( 3 ) );
    }
}