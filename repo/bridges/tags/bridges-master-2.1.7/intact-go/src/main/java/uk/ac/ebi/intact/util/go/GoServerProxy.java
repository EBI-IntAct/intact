/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.go;

import psidev.psi.tools.ontology_manager.client.OlsClient;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * The proxy to the Go server. An example for the use of this class:
 * <pre>
 * GoServerProxy proxy = new GoServerProxy( "http://www.ebi.ac.uk/ego/DisplayGoTerm" );
 * // Could use the default: GoServerProxy proxy = new GoServerProxy( );
 * GoResponse response = proxy.query( "GO:0000074" );
 * System.out.println ( response );
 * </pre>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class GoServerProxy {

    ////////////////
    // Class Data
    ///////////////

    ///////////////////
    private static final String GO = "GO";

    // Instance Data

    private OlsClient olsQuery;

    private boolean categoryEnabled = true;

    ///////////////////

    public GoServerProxy() {
    }

    public GoServerProxy(boolean categoryEnabled) {
    }


    /**
     * Queries the GO terms using the OLS service
     *
     * @param goId the GO term to query the ego server.
     *
     * @return the GO term definition.
     *
     * @throws GoIdNotFoundException thrown when the server fails to find a response for GO id.
     * @throws java.rmi.RemoteException
     * @throws javax.xml.rpc.ServiceException
     */
    public GoTerm query( String goId )
            throws RemoteException, ServiceException, GoIdNotFoundException, MalformedURLException {
        if (goId == null) throw new NullPointerException("goId cannot be null");

        olsQuery = new OlsClient();

        String name = olsQuery.getTermById(goId, GO);

        if (goId.equals(name)) {
            throw new GoIdNotFoundException(goId);
        }

        Map<String,String> metadata = olsQuery.getTermMetadata(goId, GO);
        String definition = metadata.get("definition");

        GoTerm category = null;

        if (categoryEnabled) {
            String categoryGoId = getCategoryForGoId(goId);

            // do not create a go term if the go id is already a category
            if (!categoryGoId.equals(goId)) {
                category = query(categoryGoId);
            }
        }

        GoTerm term = new GoTerm(goId, name, definition);
        term.setCategory(category);
        
        return term;
    }

    private static String getCategoryForGoId(String goId) throws RemoteException, ServiceException, MalformedURLException {
        OlsClient olsQuery = new OlsClient();
        Map goIdMap = olsQuery.getTermParents(goId, "GO");

        if (goIdMap.isEmpty()) {
            return goId;
        }

        String parentId = (String) goIdMap.keySet().iterator().next();
        return getCategoryForGoId(parentId);
     }


    /*
     * Exception class for when a tax id is not found.
     */
    public static class GoIdNotFoundException extends Exception {

        public GoIdNotFoundException( String goId ) {
            super( "Failed to find a match for " + goId );
        }

        public GoIdNotFoundException( String goId, Exception nested ) {
            super( "Failed to find a match for " + goId, nested );
        }
    } // class GoIdNotFoundException

} // class GoServerProxy