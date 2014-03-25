/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.taxonomy;

import uk.ac.ebi.ook.web.services.QueryService;
import uk.ac.ebi.ook.web.services.QueryServiceLocator;
import uk.ac.ebi.ook.web.services.Query;

import javax.xml.rpc.ServiceException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.rmi.RemoteException;

/**
 * OLS integration for accession the NCBI Taxonomy.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class OLSTaxonomyService implements TaxonomyService {

    public static final String NEWT = "NEWT";
    // match things like: Mus musculus (Mouse)
    private Pattern NAME_PATTERN = Pattern.compile( "(.+)\\((.+)\\)" );

    private Query ols;

    public OLSTaxonomyService() {
        QueryService locator = new QueryServiceLocator();
        try {
            this.ols = locator.getOntologyQuery();
        } catch ( ServiceException e ) {
            throw new RuntimeException( "Could not initialize OLS service", e );
        }
    }

    public TaxonomyTerm getTaxonomyTerm( int taxid ) throws TaxonomyServiceException {

        TaxonomyUtils.isSupportedTaxid( taxid );

        TaxonomyTerm term = null;

        if ( taxid == -1 ) {
            term = new TaxonomyTerm( -1 );
            term.setScientificName( "In vitro" );
            term.setCommonName( "In vitro" );
        } else if ( taxid == -2 ) {
            term = new TaxonomyTerm( -2 );
            term.setScientificName( "Chemical synthesis" );
            term.setCommonName( "Chemical synthesis" );
        } else if ( taxid == -3 ) {
            term = new TaxonomyTerm( -3 );
            term.setScientificName( "Unknown" );
            term.setCommonName( "Unknown" );
        } else if ( taxid == -4 ) {
            term = new TaxonomyTerm( -4 );
            term.setScientificName( "In vivo" );
            term.setCommonName( "In vivo" );
        } else if ( taxid == -5 ) {
            term = new TaxonomyTerm( -5 );
            term.setScientificName( "In Silico" );
            term.setCommonName( "In Silico" );
        }

        if( term == null ) {

            term = new TaxonomyTerm( taxid );

            try {
                final String name = ols.getTermById( String.valueOf( taxid ), NEWT );

                String scientificName = null;
                String commonName = null;

                final Matcher matcher = NAME_PATTERN.matcher( name );
                if( matcher.matches() ) {
                    scientificName = matcher.group( 1 ).trim();
                    commonName = matcher.group( 2 ).trim();
                } else {
                    scientificName = name;
                    commonName = name;
                }

                term.setCommonName( commonName );
                term.setScientificName( scientificName );
            } catch ( RemoteException e ) {
                throw new TaxonomyServiceException( e );
            }
        }

        return term;
    }

    public void retrieveChildren( TaxonomyTerm term, boolean recursively ) throws TaxonomyServiceException {
        // This could be achieved by using getting the relationship of a term and iteratively getting the terms details...
        throw new UnsupportedOperationException( );
    }

    public void retrieveParents( TaxonomyTerm term, boolean recursively ) throws TaxonomyServiceException {
        throw new UnsupportedOperationException( );
    }

    public List<TaxonomyTerm> getTermChildren( int taxid ) throws TaxonomyServiceException {
        throw new UnsupportedOperationException( );
    }

    public List<TaxonomyTerm> getTermParent( int taxid ) throws TaxonomyServiceException {
        throw new UnsupportedOperationException( );
    }

    public String getSourceDatabaseMiRef() {
        return "MI:0942"; // cheating here, there is no OLS database in the MI ontology ... and Newt is dead :(
    }

    public String getSourceDatabaseName() {
       return "uniprot taxonomy";  // cheating here, there is no OLS database in the MI ontology ... and Newt is dead :(
    }
}
