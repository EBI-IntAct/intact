/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.uniprot.model.UniprotFeatureChain;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.model.UniprotProteinTranscript;
import uk.ac.ebi.intact.uniprot.model.UniprotSpliceVariant;
import uk.ac.ebi.intact.uniprot.service.referenceFilter.CrossReferenceFilter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Proxy implementation that caches the result of UniprotService queries.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class CachedUniprotService extends AbstractUniprotService implements UniprotService {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( CachedUniprotService.class );

    /**
     * Where the ehcache configuration file is.
     */
    public static final String EHCACHE_CONFIG_FILE = "/ehcache-config.xml";

    public static final String CACHE_NAME = "uniprot-service-cache";

    /**
     * Cache for queries to UniProt.
     */
    private Cache cache;
    private CacheManager cacheManager;

    /**
     * The UniprotService we are going to cache queries for.
     */
    private UniprotService service;

    public CachedUniprotService( UniprotService service ) {
        super();
        if ( service == null ) {
            throw new NullPointerException( "UniprotService must not be null." );
        }
        this.service = service;

        // building cache
        // Note: more info on how to use ehcache here: http://ehcache.sourceforge.net/samples.html
        URL url = getClass().getResource( EHCACHE_CONFIG_FILE );

        if ( log.isDebugEnabled() ) {
            log.debug( "Loading EHCACHE configuration: " + url );
        }

        // TODO This class could look who has references (soft reference) to it and shutdown the cache when no one is referencing to it anymore.
        this.cacheManager = new CacheManager( url );
        this.cache = cacheManager.getCache( CACHE_NAME );

        if ( cache == null ) {
            throw new RuntimeUniprotServiceException( "Could not load cache: " + CACHE_NAME );
        }
    }

    @Deprecated
    public Collection<UniprotProtein> retreive( String ac ) {
        return retrieve(ac);
    }

    @Deprecated
    public Map<String, Collection<UniprotProtein>> retreive( Collection<String> acs ) {
        return retrieve(acs);
    }

    public Collection<UniprotProtein> retrieve( String ac ) {

        Collection<UniprotProtein> proteins = getFromCache( ac );
        if ( proteins == null ) {
            proteins = service.retrieve( ac );
            storeInCache( proteins, ac );
        }

        return proteins;
    }

    public Collection<UniprotProtein> retrieve(String ac, boolean processSpliceVars) {
        return retrieve(ac);
    }

    public Map<String, Collection<UniprotProtein>> retrieve( Collection<String> acs ) {

        // TODO, here, we by-pass the underlying service method !!! Might not be what we want !!

        Map<String, Collection<UniprotProtein>> resultMap = new HashMap<String, Collection<UniprotProtein>>( acs.size() );

        for ( String ac : acs ) {
            Collection<UniprotProtein> proteins = retrieve( ac );
            resultMap.put( ac, proteins );
        }

        return resultMap;
    }

    public Map<String, Collection<UniprotProtein>> retrieve(Collection<String> acs, boolean processSpliceVars) {
        return retrieve(acs);
    }

    public Map<String, UniprotServiceReport> getErrors() {
        return service.getErrors();
    }

    public void clearErrors() {
        service.clearErrors();
    }

    public void setCrossReferenceSelector( CrossReferenceFilter crossReferenceFilter ) {
        service.setCrossReferenceSelector( crossReferenceFilter );
    }

    public CrossReferenceFilter getCrossReferenceSelector() {
        return service.getCrossReferenceSelector();
    }

    public Collection<UniprotProteinTranscript> retrieveProteinTranscripts( String ac ){
        if (log.isDebugEnabled()) {
            log.debug("Retrieving splice variants from UniProt: "+ac);
        }
        Collection<UniprotProteinTranscript> variants = new ArrayList<UniprotProteinTranscript>();

        variants.addAll(retrieveSpliceVariant(ac));
        variants.addAll(retrieveFeatureChain(ac));

        return variants;
    }

    public Collection<UniprotSpliceVariant> retrieveSpliceVariant( String ac ) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving splice variants from UniProt: "+ac);
        }
        Collection<UniprotSpliceVariant> variants = new ArrayList<UniprotSpliceVariant>();
        Collection<String> variantAcProcessed = new ArrayList<String>();

        Collection<UniprotProtein> proteins = getFromCache( ac );
        if ( proteins == null ) {
            proteins = service.retrieve( ac );
            storeInCache( proteins, ac );
        }

        for (UniprotProtein p : proteins){
            UniprotSpliceVariant variant = super.retrieveUniprotSpliceVariant(p, ac);

            if (!variantAcProcessed.contains(variant.getPrimaryAc())){
                variants.add(variant);
                variantAcProcessed.add(variant.getPrimaryAc());
            }
        }

        return variants;
    }

    public Collection<UniprotFeatureChain> retrieveFeatureChain( String ac ) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving feature chains from UniProt: "+ac);
        }
        Collection<UniprotFeatureChain> variants = new ArrayList<UniprotFeatureChain>();

        Collection<UniprotProtein> proteins = getFromCache( ac );
        if ( proteins == null ) {
            proteins = service.retrieve( ac );
            storeInCache( proteins, ac );
        }

        for (UniprotProtein p : proteins){
            UniprotFeatureChain variant = retrieveUniprotFeatureChain(p, ac);

            variants.add(variant);
        }

        return variants;
    }

    @Override
    public void close() {
        if (this.cache != null){
            if( Status.STATUS_ALIVE.equals( cache.getStatus() ) ) {
                System.out.println( "Attempting to dispose of: " + cache.getName() + "..." );
                cache.dispose();
            }
        }

        if (cacheManager != null){
            cacheManager.clearAll();
        }
    }

    /////////////////////////
    // EH CACHE utilities

    private Collection<UniprotProtein> getFromCache( String ac ) {

        Collection<UniprotProtein> proteins = null;

        Element element = cache.get( ac );

        if ( element != null ) {
            proteins = ( Collection<UniprotProtein> ) element.getValue();
        }

        return proteins;
    }

    private void storeInCache( Collection<UniprotProtein> proteins, String ac ) {
        Element element = new Element( ac, proteins );
        cache.put( element );
    }
}