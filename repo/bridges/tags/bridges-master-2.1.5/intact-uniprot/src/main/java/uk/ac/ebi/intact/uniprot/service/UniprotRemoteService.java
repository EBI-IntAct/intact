/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.uniprot.model.Organism;
import uk.ac.ebi.intact.uniprot.model.*;
import uk.ac.ebi.intact.uniprot.service.crossRefAdapter.ReflectionCrossReferenceBuilder;
import uk.ac.ebi.intact.uniprot.service.crossRefAdapter.UniprotCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.*;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.*;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Field;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.kraken.interfaces.uniprot.features.*;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.GeneNameSynonym;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.ORFName;
import uk.ac.ebi.kraken.interfaces.uniprot.genename.OrderedLocusName;
import uk.ac.ebi.kraken.uuw.services.remoting.*;

import java.util.*;

/**
 * Adapter to read UniProt entries using the remote services.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Oct-2006</pre>
 */
public class UniprotRemoteService extends SimpleUniprotRemoteService {

    private Map<String,Collection<UniprotProtein>> retrievalCache;

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( UniprotRemoteService.class );

    public UniprotRemoteService() {
        super();
        retrievalCache = new WeakHashMap<String,Collection<UniprotProtein>>();
    }

    @Override
    public Collection<UniprotSpliceVariant> retrieveSpliceVariant( String ac ) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving splice variants from UniProt: "+ac);
        }
        Collection<UniprotSpliceVariant> variants = new ArrayList<UniprotSpliceVariant>();
        Collection<String> variantAcProcessed = new ArrayList<String>();

        if (retrievalCache.containsKey(ac)) {
            if (log.isDebugEnabled()) log.debug("\tFound in cache");
            Collection<UniprotProtein> uniprotProteins = retrievalCache.get(ac);

            for (UniprotProtein p : uniprotProteins){
                UniprotSpliceVariant variant = retrieveUniprotSpliceVariant(p, ac);

                if (variant != null && !variantAcProcessed.contains(variant.getPrimaryAc())){
                    variants.add(variant);
                    variantAcProcessed.add(variant.getPrimaryAc());
                    variant.setMasterProtein(p);
                }
            }
            return variants;
        }

        Collection<UniprotProtein> proteins = new ArrayList<UniprotProtein>();

        Iterator<UniProtEntry> it = getUniProtEntry( ac );

        if ( !it.hasNext() ) {
            // we didn't find anything
            addError( ac, new UniprotServiceReport( "Could not find splice variants: " + ac ) );
        }

        while ( it.hasNext() ) {
            UniProtEntry uniProtEntry = it.next();
            UniprotProtein uniprotProtein = buildUniprotProtein( uniProtEntry, true );
            proteins.add( uniprotProtein );
            UniprotSpliceVariant variant = retrieveUniprotSpliceVariant(uniprotProtein, ac);

            if (variant != null){
                if (!variantAcProcessed.contains(variant.getPrimaryAc())){
                    variants.add(variant);
                    variantAcProcessed.add(variant.getPrimaryAc());
                    variant.setMasterProtein(uniprotProtein);
                }
            }
        }

        retrievalCache.put(ac, proteins);

        return variants;
    }

    @Override
    public Collection<UniprotFeatureChain> retrieveFeatureChain( String ac ) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving feature chains from UniProt: "+ac);
        }
        Collection<UniprotFeatureChain> variants = new ArrayList<UniprotFeatureChain>();

        if (retrievalCache.containsKey(ac)) {
            if (log.isDebugEnabled()) log.debug("\tFound in cache");
            Collection<UniprotProtein> uniprotProteins = retrievalCache.get(ac);

            for (UniprotProtein p : uniprotProteins){
                UniprotFeatureChain variant = retrieveUniprotFeatureChain(p, ac);

                if (variant != null){
                    variants.add(variant);
                    variant.setMasterProtein(p);
                }
            }

            return variants;
        }

        Collection<UniprotProtein> proteins = new ArrayList<UniprotProtein>();

        Iterator<UniProtEntry> it = getUniProtEntry( ac );

        if ( !it.hasNext() ) {
            // we didn't find anything
            addError( ac, new UniprotServiceReport( "Could not find splice variants: " + ac ) );
        }

        while ( it.hasNext() ) {
            UniProtEntry uniProtEntry = it.next();
            UniprotProtein uniprotProtein = buildUniprotProtein( uniProtEntry, true );
            proteins.add( uniprotProtein );
            UniprotFeatureChain variant = retrieveUniprotFeatureChain(uniprotProtein, ac);

            if (variant != null) {
                variants.add(variant);
                variant.setMasterProtein(uniprotProtein);
            }
        }

        retrievalCache.put(ac, proteins);

        return variants;
    }

    @Override
    public void close() {
        this.retrievalCache.clear();
    }

    public Collection<UniprotProtein> retrieve( String ac, boolean processSpliceVars ) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving from UniProt: "+ac);
        }

        if (retrievalCache.containsKey(ac)) {
            if (log.isDebugEnabled()) log.debug("\tFound in cache");
            return retrievalCache.get(ac);
        }

        Collection<UniprotProtein> proteins = new ArrayList<UniprotProtein>();

        Iterator<UniProtEntry> it = getUniProtEntry( ac );

        if ( !it.hasNext() ) {
            // we didn't find anything
            addError( ac, new UniprotServiceReport( "Could not find protein: " + ac ) );
        }

        while ( it.hasNext() ) {
            UniProtEntry uniProtEntry = it.next();
            proteins.add( buildUniprotProtein( uniProtEntry, processSpliceVars ) );
        }

        retrievalCache.put(ac, proteins);

        return proteins;
    }

    public static void main(String[] args) {
        final UniProtQueryService uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
        final EntryIterator<UniProtEntry> iterator = uniProtQueryService.getEntryIterator(UniProtQueryBuilder.buildQuery("P43063"));

        for (UniProtEntry protEntry : iterator) {
            System.out.println(protEntry.getPrimaryUniProtAccession().getValue());
        }
    }
}