/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.taxonomy;

import java.util.List;

/**
 * OLS integration for accession the NCBI Taxonomy.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class OLSTaxonomyService implements TaxonomyService {

    public TaxonomyTerm getTaxonomyTerm( int taxid ) throws TaxonomyServiceException {
        throw new UnsupportedOperationException( );
    }

    public void retrieveChildren( TaxonomyTerm term, boolean recursively ) throws TaxonomyServiceException {
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
        throw new UnsupportedOperationException();
    }
}