/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import java.io.IOException;

/**
 * The proxy to the Go server. An example for the use of this class:
 * <pre>
 * GoServerProxy proxy = new GoServerProxy( "http://www.ebi.ac.uk/ego/DisplayGoTerm" );
 * // Could use the default: GoServerProxy proxy = new GoServerProxy( );
 * GoResponse response = proxy.query( "GO:0000074" );
 * System.out.println ( response );
 * </pre>
 *
 * @see uk.ac.ebi.intact.util.test.GoServerProxyTest
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class GoServerProxy {

    ////////////////
    // Class Data
    ///////////////

    public static final String GOID_FLAG = "${GO_ID}";

    public static final String DEFAULT_EGO_URL = "http://www.ebi.ac.uk/ego/DisplayGoTerm";

    public static final String EGO_QUERY = "?id="+ GOID_FLAG +"&intact=xml";

    ///////////////////
    // Instance Data
    ///////////////////

    /**
     * The URL to connect to Newt server.
     */
    private String myURL;

    /**
     * Constructs an instance of this class using the URL to connect to the
     * Newt server.
     *
     * @param url the URL to connect to the server.
     */
    public GoServerProxy( String url ) {
        if (url == null)
            myURL = DEFAULT_EGO_URL;
        else
            myURL = url;
    }

    public GoServerProxy( ) {
        myURL = DEFAULT_EGO_URL;
    }

    /**
     * Queries the Newt server with given tax id.
     * @param goId the tax id to query the Newt server.
     * @return an array with two elements. The first element contains
     * the short label and the second contains the full name (scientific name).
     * It is possible for the server to return empty values for both.
     * @exception IOException for network errors.
     * @exception GoIdNotFoundException thrown when the server fails to find
     * a response for GO id.
     */
    public GoResponse query (String goId)
            throws IOException,
            GoIdNotFoundException {

        GoResponse goRes = null;

        // Query the ego server.
        GoHandler goHandler = getGoResponse( goId );
        if (goHandler == null) {
            throw new GoIdNotFoundException ( goId );
        }

        // Values from newt stored in
        goRes = new GoResponse ( goId,
                goHandler.getName(),
                goHandler.getCategory() );
        return goRes;
    }

    // Helper methods

    private GoHandler getGoResponse(String goId)
            throws IOException,
            GoIdNotFoundException {

        String query = SearchReplace.replace ( EGO_QUERY, GOID_FLAG, goId );

        GoHandler goHandler = null;

        URL url = new URL (myURL + query);
        URLConnection servletConnection = url.openConnection();

        // Turn off caching
        servletConnection.setUseCaches(false);

        // Wrting to the server.
        servletConnection.setDoOutput(true);

        // The reader to read response from the server.
        InputStream inputStream = null;
        try {
            inputStream = servletConnection.getInputStream();
            InputSource source = new InputSource( inputStream );
            SAXParser parser   = new SAXParser( );
            goHandler = new GoHandler();
            parser.setContentHandler( goHandler );

            try {
                parser.parse( source );
            } catch ( SAXException e ) {
                throw new GoIdNotFoundException( goId );
            } catch ( IOException e ) {
                throw e;
            }
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException ioe) {
                }
            }
        }

        return goHandler;
    } // getGoResponse



    ///////////////////////
    // Inner class
    ///////////////////////

    public static class GoResponse {
        private String goId;
        private String name;
        private String category;

        public GoResponse ( String goId, String name, String category ) {
            this.goId = goId;
            this.name = name;
            this.category = category;
        }

        public String getGoId () {
            return goId;
        }

        public String getName () {
            return name;
        }

        public String getCategory () {
            return category;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(128);

            sb.append ( GoResponse.class.getName() );
            sb.append ( "\nID: " + goId);
            sb.append ( "\nName: " + name );
            sb.append ( "\nCategory: " + category);

            return sb.toString();
        }
    }

    /**
     * Parse the XML output given by the EGO web site.
     *
     * <p>
     * Example:
     *
     *   http://www.ebi.ac.uk/ego/DisplayGoTerm?id=GO:0000074&intact=xml
     *
     *   gives
     *
     * <pre>
     *   &lt;?xml version="1.0" encoding="iso-8859-1"?&gt;
     *   &lt;page&gt;
     *     &lt;name&gt;
     *        <b>regulation of cell cycle</b>
     *     &lt;/name&gt;
     *     &lt;category&gt;
     *        <b>process</b>
     *     &lt;/category&gt;
     *   &lt;/page&gt;
     * </pre>
     * </p>
     *
     * @author Samuel Kerrien (skerrien@ebi.ac.uk)
     * @version $Id$
     */
    public class GoHandler implements ContentHandler {

        //////////////////
        // Constants
        //////////////////

        /**
         * Tag containing the name of the GO term.
         */
        private static final String NAME = "name";

        /**
         * Tag containing the category of the GO term.
         */
        private static final String CATEGORY = "category";

        ///////////////////////
        // Instance variables
        ///////////////////////

        /**
         * Name of the Go term.
         */
        private String name;

        /**
         * Category of the GO term.
         */
        private String category;

        /**
         * Buffer containing temporarily the content of the curently parsed tag.
         */
        private StringBuffer contentTagBuffer = new StringBuffer(64);

        ///////////////////////////////////////
        // getter for the extracted attributes
        ///////////////////////////////////////
        public String getName () {
            return name;
        }

        public String getCategory () {
            return category;
        }

        ///////////////////
        // Parsing logic
        ///////////////////

        final public void characters( final char[] ch, final int start, final int len ){
            contentTagBuffer.append( ch, start, len );
        }

        /**
         * Called when the end of a tag is encoutered.
         * We are just interrested in the tags &lt;/name&gt; and &lt;/category&gt; and
         * store the content of the tag in the appropriate instance variable.
         *
         * @param namespaceURI The Namespace URI, or the empty string if the element has no Namespace URI or
         *                     if Namespace processing is not being performed.
         * @param localName    The local name (without prefix), or the empty string if Namespace processing
         *                     is not being performed.
         * @param rawName      The qualified XML 1.0 name (with prefix), or the empty string if qualified
         *                     names are not available.
         * @throws SAXException
         */
        public void endElement( String namespaceURI,
                                String localName,
                                String rawName)
                throws SAXException {

            if (localName.equals( NAME )) {
                this.name = contentTagBuffer.toString().trim();
                contentTagBuffer = new StringBuffer(64);
            } else if (localName.equals( CATEGORY )) {
                this.category = contentTagBuffer.toString().trim();
                contentTagBuffer = new StringBuffer(64);
            }
        }


        /*
        * Methods that aren't necessary in this parser but need to be
        * declared in order to respect the ContentHandler interface
        */
        public void startDocument() throws SAXException { }
        public void endDocument() throws SAXException { }
        public void startElement( String namespaceURI, String localName, String rawName, Attributes atts ) throws SAXException { }
        public void processingInstruction( String target, String data ) throws SAXException {}
        public void startPrefixMapping( String prefix, String uri ) throws SAXException { }
        public void ignorableWhitespace( char[] text, int start, int length ) throws SAXException { }
        public void endPrefixMapping( String prefix ) throws SAXException { }
        public void skippedEntity( String name ) throws SAXException { }
        public void setDocumentLocator( Locator locator ) { }
    } // GoHandler


    /*
     * Exception class for when a tax id is not found.
     */
    public static class GoIdNotFoundException extends Exception {
        public GoIdNotFoundException(String goId) {
            super("Failed to find a match for " + goId);
        }
    }

} // GoServerProxy