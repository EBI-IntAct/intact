/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.taxonomy;

import java.util.List;

/**
 * What a Taxonomy can do.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public interface TaxonomyService {

    /**
     * retrieves a term by taxid.
     *
     * @param taxid the taxid of the wanted term
     *
     * @return the term or null if not found.
     *
     * @throws TaxonomyServiceException if an error occur during the processing.
     */
    public TaxonomyTerm getTaxonomyTerm( int taxid ) throws TaxonomyServiceException;

    /**
     * Update a NewtTerm by adding its children. the process can be done recursively if the given flag it true.
     *
     * @param term        the term to update.
     * @param recursively if true, update recursively.
     *
     * @throws TaxonomyServiceException if an error occur during the processing.
     */
    public void retrieveChildren( TaxonomyTerm term, boolean recursively ) throws TaxonomyServiceException;

    /**
     * Update a NewtTerm by adding its parents. the process can be done recursively if the given flag it true.
     *
     * @param term        the term to update.
     * @param recursively if true, update recursively.
     *
     * @throws TaxonomyServiceException if an error occur during the processing.
     */
    public void retrieveParents( TaxonomyTerm term, boolean recursively ) throws TaxonomyServiceException;

    /**
     * Get the list of children of a given term given its taxid.
     *
     * @param taxid the term's taxid.
     *
     * @return a non null list of NewtTerm.
     *
     * @throws TaxonomyServiceException if an error occur during the processing.
     */
    public List<TaxonomyTerm> getTermChildren( int taxid ) throws TaxonomyServiceException;

    /**
     * Get the list of parents of a given term given its taxid.
     *
     * @param taxid the term's taxid.
     *
     * @return a non null list of NewtTerm.
     *
     * @throws TaxonomyServiceException if an error occur during the processing.
     */
    public List<TaxonomyTerm> getTermParent( int taxid ) throws TaxonomyServiceException;

    /**
     * Return the IntAct CvDatabase corresponding to the resource that is being queried.
     *
     * @return the MI reference of the source database.
     */
    public String getSourceDatabaseMiRef();
}