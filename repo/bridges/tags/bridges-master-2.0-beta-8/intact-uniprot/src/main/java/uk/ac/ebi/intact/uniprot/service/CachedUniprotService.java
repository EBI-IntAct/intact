/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.service.referenceFilter.CrossReferenceFilter;

import java.net.URL;
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
public class CachedUniprotService implements UniprotService {

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

    /**
     * The UniprotService we are going to cache queries for.
     */
    private UniprotService service;

    public CachedUniprotService( UniprotService service ) {
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
        this.cache = new CacheManager( url ).getCache( CACHE_NAME );

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

    public Map<String, Collection<UniprotProtein>> retrieve( Collection<String> acs ) {

        // TODO, here, we by-pass the underlying service method !!! Might not be what we want !!

        Map<String, Collection<UniprotProtein>> resultMap = new HashMap<String, Collection<UniprotProtein>>( acs.size() );

        for ( String ac : acs ) {
            Collection<UniprotProtein> proteins = retrieve( ac );
            resultMap.put( ac, proteins );
        }

        return resultMap;
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