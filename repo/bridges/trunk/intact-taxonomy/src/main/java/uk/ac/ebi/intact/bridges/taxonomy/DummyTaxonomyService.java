/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.taxonomy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.List;

/**
 * Taxonomy Bridge to be used for testing purpose as it creates Taxonomy terms in a predictible manner without relying
 * on the network.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class DummyTaxonomyService implements TaxonomyService {

    public static final Log log = LogFactory.getLog( DummyTaxonomyService.class );

    public TaxonomyTerm getTaxonomyTerm( int taxid ) throws TaxonomyServiceException {
        log.debug( "Building Taxonomy term with taxid: " + taxid );
        TaxonomyTerm term = new TaxonomyTerm( taxid );
        term.setCommonName( String.valueOf( taxid ) );
        term.setScientificName( String.valueOf( taxid ) );
        return term;
    }

    public void retrieveChildren( TaxonomyTerm term, boolean recursively ) throws TaxonomyServiceException {
        log.debug( "Retreiving children taxonomy term(s) of: " + term.getTaxid() );
    }

    public void retrieveParents( TaxonomyTerm term, boolean recursively ) throws TaxonomyServiceException {
        log.debug( "Retreiving parent taxonomy term of: " + term.getTaxid() );
        if( term.getTaxid() != 1 ) {
            // add the root: 1
            term.addParent( buildRoot() );
        }
    }

    private TaxonomyTerm buildRoot() {

        TaxonomyTerm term = new TaxonomyTerm( 1 );

        term.setCommonName( "root" );
        term.setScientificName( "root" );

        return term;
    }

    public List<TaxonomyTerm> getTermChildren( int taxid ) throws TaxonomyServiceException {
        log.debug( "Collecting all children of Taxonomy term: " + taxid );
        return Collections.EMPTY_LIST;
    }

    public List<TaxonomyTerm> getTermParent( int taxid ) throws TaxonomyServiceException {
        log.debug( "Collecting parent of Taxonomy term: " + taxid );
        return Collections.EMPTY_LIST;
    }

    public String getSourceDatabaseMiRef() {
        // return IntAct
        return "MI:0469";
    }

    public String getSourceDatabaseName() {
        // return IntAct
        return "intact";
    }
}
