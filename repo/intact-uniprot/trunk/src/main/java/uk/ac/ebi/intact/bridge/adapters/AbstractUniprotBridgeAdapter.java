/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge.adapters;

import uk.ac.ebi.intact.bridge.adapters.referenceFilter.CrossReferenceFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract UniProt Adapter.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Oct-2006</pre>
 */
public abstract class AbstractUniprotBridgeAdapter implements UniprotBridgeAdapter {

    public static final String SWISS_PROT_PREFIX = "SP_";
    public static final String TREMBL_PREFIX = "TrEMBL_";

    /**
     * Holds error messages accumulated during protein retreival.
     */
    Map<String, String> errors = new HashMap<String, String>();

    /**
     * Defines how should the cross references be selected.
     */
    private CrossReferenceFilter crossReferenceFilter;

    public Map<String, String> getErrors() {
        return errors;
    }

    public void clearErrors() {
        errors.clear();
    }

    ///////////////////////////
    // Strategies

    public void setCrossReferenceSelector( CrossReferenceFilter crossReferenceFilter ) {
        this.crossReferenceFilter = crossReferenceFilter;
    }

    public CrossReferenceFilter getCrossReferenceSelector() {
        return crossReferenceFilter;
    }
}