package uk.ac.ebi.intact.bridges.olslight;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.util.Map;

/**
 * Cached ontology service.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.4
 */
public class CachedOntologyService implements OntologyService {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( CachedOntologyService.class );

    /**
     * Where the ehcache configuration file is.
     */
    public static final String EHCACHE_CONFIG_FILE = "/ontology-service.ehcache.xml";

    public static final String CACHE_NAME = "ontology-service-cache";

    /**
     * Cache for queries to UniProt.
     */
    private Cache cache;

    private static CacheManager cacheManager;

    /**
     * Service we are caching for.
     */
    private OntologyService service;

    public CachedOntologyService( OntologyService service ) throws OntologyServiceException {
        if( service == null ){
            throw new NullPointerException( "OntologyService must not be null." );
        }
        this.service = service;

        // building cache
        // Note: more info on how to use ehcache here: http://ehcache.sourceforge.net/samples.html
        URL url = getClass().getResource( EHCACHE_CONFIG_FILE );

        if( log.isDebugEnabled() ){
            log.debug( "Loading EHCACHE configuration: " + url );
        }

        if( cacheManager == null ) {
            // we should only have a single instance of CacheManager for that configuration file.
            cacheManager = new CacheManager( url );
        }

        this.cache = cacheManager.getCache( CACHE_NAME );

        if( cache == null ){
            throw new OntologyServiceException( "Could not load cache: " + CACHE_NAME );
        }
    }

    @Override
    public String getTermName( OntologyId ontologyId, String termId ) throws OntologyServiceException {
        final String key = "getTermName#"+ontologyId+"#"+termId;
        Object data = getFromCache( key );
        if( data == null ) {
            data = service.getTermName( ontologyId, termId );
            storeInCache( key, data );
        }
        return (String) data;
    }

    @Override
    public Map<String, String> getTermDirectChildren( OntologyId ontologyId, String termId ) throws OntologyServiceException {
        final String key = "getTermDirectChildren#"+ontologyId+"#"+termId;
        Object data = getFromCache( key );
        if( data == null ) {
            data = service.getTermDirectChildren( ontologyId, termId );
            storeInCache( key, data );
        }
        return (Map<String, String>) data;
    }

    @Override
    public Map<String, String> getTermChildren( OntologyId ontologyId, String termId, int depth ) throws OntologyServiceException {
        final String key = "getTermChildren#"+ontologyId+"#"+termId+"#"+depth;
        Object data = getFromCache( key );
        if( data == null ) {
            data = service.getTermChildren( ontologyId, termId, depth );
            storeInCache( key, data );
        }
        return (Map<String, String>) data;
    }

    /////////////////////////
    // EH CACHE utilities

    private Object getFromCache( String key ) {
        Object data = null;
        Element element = cache.get( key );
        if( element != null ){
            data = element.getValue();
        }
        return data;
    }

    private void storeInCache( String key, Object data ) {
        Element element = new Element( key, data );
        cache.put( element );
    }
}
