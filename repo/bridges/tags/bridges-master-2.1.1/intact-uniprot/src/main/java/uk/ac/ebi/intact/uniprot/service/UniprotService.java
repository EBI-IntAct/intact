/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.service;

import uk.ac.ebi.intact.uniprot.UniprotServiceException;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.service.referenceFilter.CrossReferenceFilter;

import java.util.Collection;
import java.util.Map;

/**
 * Definition of the access to the UniProt protein retreival service.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public interface UniprotService {

    // TODO REFACTORING IDEA return the result and the report in the same object so we don't have to call getErrors

    /**
     * Retreive a Uniprot protein based on its ID, AC or splice variant ID.
     *
     * @param ac ID, AC or splice variant ID of the protein we are searching for.
     *
     * @return a collection of protein found.
     *
     * @throws UniprotServiceException
     */
    public Collection<UniprotProtein> retrieve( String ac );

    @Deprecated
    public Collection<UniprotProtein> retreive( String ac );

    /**
     * Retreive a set of Uniprot proteins based on their ID, AC or splice variant ID.
     *
     * @param acs list of ID, AC or splice variant ID of the proteins we are searching for.
     *
     * @return an associative structure where each given ac is an entry and associated is a collection of protein found.
     *
     * @throws UniprotServiceException
     */
    public Map<String, Collection<UniprotProtein>> retrieve( Collection<String> acs );

    @Deprecated
    public Map<String, Collection<UniprotProtein>> retreive( Collection<String> acs );

    /**
     * Error messages encountered during retreival of proteins.
     *
     * @return an associative structure where each given ac is an entry and associated a message.
     */
    public Map<String, UniprotServiceReport> getErrors();

    /**
     * Clear existing error messages.
     */
    public void clearErrors();

    /**
     * Allow the user to define which Uniprot CrossReference should be selected.
     *
     * @param crossReferenceFilter
     */
    public void setCrossReferenceSelector( CrossReferenceFilter crossReferenceFilter );

    /**
     * Getter for property 'crossReferenceSelector'.
     *
     * @return Value for property 'crossReferenceSelector'.
     */
    public CrossReferenceFilter getCrossReferenceSelector();
}