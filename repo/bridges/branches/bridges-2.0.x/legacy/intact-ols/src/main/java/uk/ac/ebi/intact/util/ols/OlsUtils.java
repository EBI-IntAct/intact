/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.ols;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.ook.web.services.Query;
import uk.ac.ebi.ook.web.services.QueryService;
import uk.ac.ebi.ook.web.services.QueryServiceLocator;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.*;
import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Utility giving access to some of the ontologies supported by OLS.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OlsUtils {

    public static final Log log = LogFactory.getLog( OlsUtils.class );

    /**
     * Where the ehcache configuration file is.
     */
    public static final String EHCACHE_CONFIG_FILE = "/ehcache-ols-config.xml";

    public static final String CACHE_NAME = "ols-cache";    

    /**
     * Identifier of the PSI-MI ontology in OLS.
     */
    public static final String PSI_MI_ONTOLOGY = "MI";

    /**
     * Identifier of the GO ontology in OLS.
     */
    public static final String GO_ONTOLOGY = "GO";

    /**
     * Identifier of the Newt ontology in OLS.
     */
    public static final String NEWT_ONTOLOGY = "NEWT";

    /**
     * Cache for queries to UniProt.
     */
    private static Cache cache;

    private OlsUtils() {
    }

    static {
        // building cache
        // Note: more info on how to use ehcache here: http://ehcache.sourceforge.net/samples.html
        URL url = OlsUtils.class.getResource( EHCACHE_CONFIG_FILE );

        if ( log.isDebugEnabled() ) {
            log.debug( "Loading EHCACHE configuration: " + url );
        }

        // TODO This class could look who has references (soft reference) to it and shutdown the cache when no one is referencing to it anymore.
        cache = new CacheManager( url ).getCache( CACHE_NAME );

        if ( cache == null ) {
            log.info( "Could not configure find " + EHCACHE_CONFIG_FILE + ", OlsUtils will wun with caching disabled." );
        } else {
            log.info( "OlsUtils will run with caching enabled." );
        }
    }

    ///////////////////////////
    // MI terms

    /**
     * Gets a term from the MI ontology using the provided MI term id.
     *
     * @param miTermId the term id to look for
     *
     * @return the term, including the children
     */
    public static Term getMiTerm( String miTermId ) throws RemoteException {
        Term term = getMiTermCached( miTermId, PSI_MI_ONTOLOGY );

        if( term == null ) {
            OlsClient olsClient = new OlsClient();
            Query ontologyQuery = olsClient.getOntologyQuery();

            term = getTerm( miTermId, PSI_MI_ONTOLOGY );

            populateChildren( term, PSI_MI_ONTOLOGY, ontologyQuery );
            populateParents( term, PSI_MI_ONTOLOGY, ontologyQuery );

            storeMiTermInCache( miTermId, PSI_MI_ONTOLOGY, term );
        }

        return term;
    }

    private static void storeMiTermInCache( String termId, String ontologyId, Term term ) {
        if( cache != null ) {
            Element element = new Element( "getMiTerm#" + ontologyId + "#" + termId, term );
            cache.put( element );
        }
    }

    private static Term getMiTermCached( String termId, String ontologyId ) {
        Term term = null;
        if( cache != null ) {
            final Element element = cache.get( "getMiTerm#" + ontologyId + "#" + termId );
            if( element != null ) {
                term = (Term) element.getValue();
            }
        }
        return term;
    }

    public static Term getGoTerm( String goTermId ) throws RemoteException {
        return getTerm( goTermId, "GO" );
        
    }

    private static void populateChildren( Term term, String ontology, Query ontologyQuery ) throws RemoteException {
        Map<String, String> childrenMap = ontologyQuery.getTermChildren( term.getId(), ontology, 1, new int[0] );

        for ( Map.Entry<String, String> entry : childrenMap.entrySet() ) {
            Term child = getTerm(entry.getKey(), ontology);
            populateChildren( child, ontology, ontologyQuery );
            term.addChild( child );
        }
    }

    /**
     * Returns all the parents for the given mi Identifier, ie goes until the root parent
     *
     * @param miId
     * @param ontology
     * @param ontologyQuery
     * @param parents a new List<Term> which holds the collection of parent Terms
     * @param excludeRootParent if true, doesn't include root parent term
     * @return List of parent terms
     * @throws RemoteException   call from OlsUtils throws RemoteException
     */
    public static List<Term> getAllParents( String miId, String ontology, Query ontologyQuery, List parents, boolean excludeRootParent ) throws RemoteException {

        Map<String, String> parentMap = ontologyQuery.getTermParents( miId, ontology );

        for ( Map.Entry<String, String> entry : parentMap.entrySet() ) {
            Term parent = getTerm( entry.getKey(), ontology );
            getAllParents( entry.getKey(), ontology, ontologyQuery, parents, excludeRootParent );

            if ( excludeRootParent ) {
                if ( hasParent( parent.getId(), ontology, ontologyQuery ) ) {
                    parents.add( parent );
                }
            } else {
                parents.add( parent );
            }
        }
        List<Term> allParents = new ArrayList<Term>(new HashSet<Term>(parents));
        return allParents;
    }

    public static boolean hasParent( String miId, String ontology, Query ontologyQuery ) throws RemoteException {
        Map<String, String> parentMap = ontologyQuery.getTermParents( miId, ontology );

        if (parentMap.size() == 0 ) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Get all the parents for the given term, but only the immediete parents
     * @param term
     * @param ontology
     * @param ontologyQuery
     * @throws RemoteException
     */
    private static void populateParents( Term term, String ontology, Query ontologyQuery ) throws RemoteException {
        Map<String, String> parentMap = ontologyQuery.getTermParents( term.getId(), ontology );

        for ( Map.Entry<String, String> entry : parentMap.entrySet() ) {
            Term parent = getTerm( entry.getKey(), ontology );
            populateParents( parent, ontology, ontologyQuery );
            term.addParents( parent );
        }
    }

    protected static Term getTerm(String id, String ontologyId) throws RemoteException {
        Term term = getCached( id, ontologyId );
        if( term == null ) {
            OlsClient olsClient = new OlsClient();
            Query ontologyQuery = olsClient.getOntologyQuery();
            String termName = ontologyQuery.getTermById( id, ontologyId );
            Map termMetadata = ontologyQuery.getTermMetadata(id, ontologyId);
            term = new Term( id, termName, termMetadata);
            storeTermInCache( id, ontologyId, term );
        }
        return term;
    }

    private static void storeTermInCache( String termId, String ontologyId, Term term ) {
        if( cache != null ) {
            Element element = new Element( "getTerm#" + ontologyId + "#" + termId, term );
            cache.put( element );
        }
    }

    private static Term getCached( String termId, String ontologyId ) {
        Term term = null;
        if( cache != null ) {
            final Element element = cache.get( "getTerm#" + ontologyId + "#" + termId );
            if( element != null ) {
                term = (Term) element.getValue();
            }
        }
        return term;
    }

    ///////////////////////////
    // Ontology terms

    /**
     * Gets a term from the NEWT ontology using the provided taxid.
     *
     * @param taxid           the term id to look for
     * @param includeChildren if true, children of the term will be included.
     *
     * @return the term, including the children
     */
    public static Term getOntologyTerm( String taxid, boolean includeChildren ) throws RemoteException, ServiceException {
        Term term = getTerm( taxid, NEWT_ONTOLOGY );

        if ( includeChildren ) {
            populateOntologyChildren( term, true );
        }

        return term;
    }

    private static void populateOntologyChildren( Term term, boolean recursive ) throws RemoteException, ServiceException {
        Collection<String> termIds = getChildrenIdentifiers( term.getId(), NEWT_ONTOLOGY );
        if( log.isDebugEnabled() ) {
            log.debug( "" );
        }
        for ( String termId : termIds ) {
            Term child = getOntologyTerm( termId, false );
            term.addChild( child );

            if ( recursive ) {
                populateOntologyChildren( child, recursive );
            }
        }
    }

    private static Collection<String> getChildrenIdentifiers( String id, String ontology ) throws ServiceException, RemoteException {
        QueryService locator = new QueryServiceLocator();
        Query qs = locator.getOntologyQuery();
        Map map = qs.getTermRelations( id, ontology );
        return map.keySet();
    }
}