/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.taxonomy;

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

    public TaxonomyTerm getTaxonomyTerm( int taxid ) throws TaxonomyServiceException {
        throw new UnsupportedOperationException();
    }

    public void retreiveChildren( TaxonomyTerm term, boolean recursively ) throws TaxonomyServiceException {
    }

    public void retreiveParents( TaxonomyTerm term, boolean recursively ) throws TaxonomyServiceException {
    }

    public List<TaxonomyTerm> getTermChildren( int taxid ) throws TaxonomyServiceException {
        throw new UnsupportedOperationException();
    }

    public List<TaxonomyTerm> getTermParent( int taxid ) throws TaxonomyServiceException {
        throw new UnsupportedOperationException();
    }

    public String getSourceDatabaseMiRef() {
        return "MI:9999";
    }
}