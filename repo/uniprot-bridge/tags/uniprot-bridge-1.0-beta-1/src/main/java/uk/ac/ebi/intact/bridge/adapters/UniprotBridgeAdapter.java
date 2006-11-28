/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge.adapters;

import uk.ac.ebi.intact.bridge.model.UniprotProtein;
import uk.ac.ebi.intact.bridge.UniprotBridgeException;

import java.util.Collection;
import java.util.Map;

/**
 * Definition of the access to the UniProt protein retreival service.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15-Sep-2006</pre>
 */
public interface UniprotBridgeAdapter {

    /**
     * Retreive a Uniprot protein based on its ID, AC or splice variant ID.
     *
     * @param ac ID, AC or splice variant ID of the protein we are searching for.
     * @return a collection of protein found.
     * @throws UniprotBridgeException
     */
    public Collection<UniprotProtein> retreive( String ac ) throws UniprotBridgeException;

    /**
     * Retreive a set of Uniprot proteins based on their ID, AC or splice variant ID.
     *
     * @param acs list of ID, AC or splice variant ID of the proteins we are searching for.
     * @return an associative structure where each given ac is an entry and associated is a collection of protein found.
     * @throws UniprotBridgeException
     */
    public Map<String, Collection<UniprotProtein>> retreive( Collection<String> acs ) throws UniprotBridgeException;

    /**
     * Error messages encountered during retreival of proteins.
     * @return an associative structure where each given ac is an entry and associated a message.
     */
    public Map<String, String> getErrors();

    /**
     * Clear existing error messages.
     */
    public void clearErrors();
}