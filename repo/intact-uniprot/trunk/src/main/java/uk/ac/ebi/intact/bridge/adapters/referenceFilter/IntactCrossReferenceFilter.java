/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge.adapters.referenceFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-Oct-2006</pre>
 */
public class IntactCrossReferenceFilter implements CrossReferenceFilter {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( IntactCrossReferenceFilter.class );

    private List<String> databases = new ArrayList<String>();

    public IntactCrossReferenceFilter() {
        databases.add( format( "GO" ) );
        databases.add( format( "InterPro" ) );
        databases.add( format( "PDB" ) );
        databases.add( format( "HUGE" ) );
        databases.add( format( "SGD" ) );
        databases.add( format( "FlyBase" ) );
        databases.add( format( "HUGE" ) );
    }

    private String format( String s ) {
        if ( s == null ) {
            throw new IllegalArgumentException( "Database must not be null." );
        }

        s = s.trim();
        if ( "".equals( s ) ) {
            throw new IllegalArgumentException( "Database must be a non empty String." );
        }

        return s.toLowerCase();
    }

    /////////////////////////////////////
    // CrossReferenceSelector method

    public boolean isSelected( String database ) {
        return databases.contains( format( database ) );
    }

    public List<String> getFilteredDatabases() {
        // TODO test this
        return Collections.unmodifiableList( databases );
    }
}