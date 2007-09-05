/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.uniprot.service.referenceFilter.CrossReferenceFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract UniProt Adapter.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Oct-2006</pre>
 */
public abstract class AbstractUniprotService implements UniprotService {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( AbstractUniprotService.class );

    public static final String SWISS_PROT_PREFIX = "SP_";

    public static final String TREMBL_PREFIX = "TrEMBL_";

    /**
     * Holds error messages accumulated during protein retreival.
     */
    private Map<String, UniprotServiceReport> errors = new HashMap<String, UniprotServiceReport>();

    /**
     * Defines how should the cross references be selected.
     */
    private CrossReferenceFilter crossReferenceFilter;

    public Map<String, UniprotServiceReport> getErrors() {
        return errors;
    }

    public void clearErrors() {
        errors.clear();
    }

    public void addError( String ac, UniprotServiceReport report ) {
        if( ac == null ) {
            throw new IllegalArgumentException( "You must give a non null UniProt AC." );
        }

        if( report == null ) {
            throw new IllegalArgumentException( "You must give a non null Report." );
        }

        if( errors.containsKey( ac ) ) {
            log.warn( "Overwriting existing report for UniProt AC: " + ac );
        }
        
        errors.put( ac, report );
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