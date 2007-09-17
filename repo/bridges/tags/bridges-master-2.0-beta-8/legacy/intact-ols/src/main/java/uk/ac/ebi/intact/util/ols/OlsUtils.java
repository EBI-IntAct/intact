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
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

/**
 * Utility giving access to some of the ontologies supported by OLS.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OlsUtils {

    public static final Log log = LogFactory.getLog( OlsUtils.class );

    /**
     * Identifier of the PSI-MI ontology in OLS.
     */
    public static final String PSI_MI_ONTOLOGY = "MI";

    /**
     * Identifier of the Newt ontology in OLS.
     */
    public static final String NEWT_ONTOLOGY = "NEWT";

    private OlsUtils() {
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
        OlsClient olsClient = new OlsClient();
        Query ontologyQuery = olsClient.getOntologyQuery();

        Term term = getTerm( miTermId, PSI_MI_ONTOLOGY );

        populateChildren( term, PSI_MI_ONTOLOGY, ontologyQuery );

        return term;
    }

    private static void populateChildren( Term term, String ontology, Query ontologyQuery ) throws RemoteException {
        System.out.println( "term.getId() = " + term.getId() );
        Map<String, String> childrenMap = ontologyQuery.getTermChildren( term.getId(), ontology, 1, new int[0] );
        System.out.println( "Found " + childrenMap.size() + " children in the map" );

        for ( Map.Entry<String, String> entry : childrenMap.entrySet() ) {
            Term child = getTerm(entry.getKey(), ontology);
            populateChildren( child, ontology, ontologyQuery );
            term.addChild( child );
        }
    }

    protected static Term getTerm(String id, String ontologyId) throws RemoteException {
        OlsClient olsClient = new OlsClient();
        Query ontologyQuery = olsClient.getOntologyQuery();

        String termName = ontologyQuery.getTermById( id, ontologyId );
        Map termMetadata = ontologyQuery.getTermMetadata(id, ontologyId);

        return new Term( id, termName, termMetadata);
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