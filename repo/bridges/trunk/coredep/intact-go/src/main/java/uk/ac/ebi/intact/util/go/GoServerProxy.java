/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.go;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.*;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.SearchReplace;
import uk.ac.ebi.ook.web.services.Query;
import uk.ac.ebi.ook.web.services.QueryServiceLocator;

import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.rmi.RemoteException;

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
    // Instance Data
    ///////////////////

    private static final String GO = "GO";

    public GoServerProxy() {
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
            throws RemoteException, ServiceException, GoIdNotFoundException {
               Query olsQuery = new QueryServiceLocator().getOntologyQuery();

        if (goId == null) throw new NullPointerException("goId cannot be null");

        String name = olsQuery.getTermById(goId, GO);

        if (goId.equals(name)) {
            throw new GoIdNotFoundException(goId);
        }

        Map<String,String> metadata = olsQuery.getTermMetadata(goId, GO);
        String definition = metadata.get("definition");

       return new GoTerm(name, definition);
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