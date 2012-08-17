/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.uniprot.model.UniprotFeatureChain;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.model.UniprotSpliceVariant;
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

    protected static final String CHAIN_SEPARATOR = "PRO_";

    /**
     * Holds error messages accumulated during protein retreival.
     */
    private Map<String, UniprotServiceReport> errors = new HashMap<String, UniprotServiceReport>();

    /**
     * Defines how should the cross references be selected.
     */
    private CrossReferenceFilter crossReferenceFilter;

    public AbstractUniprotService(){
        this.crossReferenceFilter = new DefaultCrossReferenceFilter();
    }

    public AbstractUniprotService(CrossReferenceFilter filter){
        this.crossReferenceFilter = filter != null ? filter : new DefaultCrossReferenceFilter();
    }

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

    public UniprotSpliceVariant retrieveUniprotSpliceVariant( UniprotProtein uniProtEntry, String ac) {

        if (uniProtEntry != null && ac != null){
            for (UniprotSpliceVariant sv : uniProtEntry.getSpliceVariants()){
                if (ac.equals(sv.getPrimaryAc())){
                    return sv;
                }
                else if (sv.getSecondaryAcs().contains(ac)){
                    return sv;
                }
            }
        }

        return null;
    }

    public UniprotFeatureChain retrieveUniprotFeatureChain( UniprotProtein uniProtEntry, String ac) {

        if (uniProtEntry != null && ac != null){
            for (UniprotFeatureChain fc : uniProtEntry.getFeatureChains()){
                String acFixed = ac;

                // if not of type primaryAc-PRO_xxxxx, it is not a feature chain. we have to build the feature chain ac
                if (ac.indexOf("-") == -1){
                    acFixed = uniProtEntry.getPrimaryAc() + "-" + ac;
                }
                
                if (acFixed.equals(fc.getPrimaryAc())){
                    return fc;
                }
            }
        }

        return null;
    }
}