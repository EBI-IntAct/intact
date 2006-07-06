/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util;

import org.apache.commons.collections.map.LRUMap;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * The proxy to the Newt server. An example for the use of this class:
 * <pre>
 * URL url = new URL("http://web7-node1.ebi.ac.uk:9120/newt/display");
 * // The server to connect to.
 * NewtServerProxy server = new NewtServerProxy(url);
 * NewtServerProxy.NewtResponse response = server.query(45009);
 * // response.getShortLabel() or response.getFullName().
 * </pre>
 *
 * @see uk.ac.ebi.intact.util.test.NewtServerProxyTest
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class NewtServerProxy {

    public static final int MAX_CACHED_ITEM = 200;

    // Inner Classes
    // ------------------------------------------------------------------------
    private static class NewtLRUCache {
        /**
         * The cache which hold a maximum number of NewtResponse.
         */
        private LRUMap lru = null;

        public NewtLRUCache () {
              this (MAX_CACHED_ITEM);
        }

        public NewtLRUCache (int size) {
            if (size > 0) lru = new LRUMap (size);
            else throw new IllegalArgumentException("The cache size must be positive!");
        }

        public void clear () {
            lru.clear();
        }

        public void store (int taxid, NewtResponse response) {
            lru.put (taxid+"", response);
        }

        public NewtResponse get (int taxid) {
            return (NewtResponse) lru.get (taxid+"");
        }

        public int size() {
            return lru.size();
        }
    } // NewtLRUCache


    public static class NewtResponse {
        private int myTaxId;
        private String myShortLabel;
        private String myFullName;

        private NewtResponse(String taxid, String shortLabel, String fullName) {
            myTaxId = Integer.parseInt(taxid);
            myShortLabel= shortLabel;
            myFullName = fullName;
        }

        // True if the response has a short label.
        public boolean hasShortLabel() {
            return myShortLabel.length() != 0;
        }

        // Only getter methods.

        public int getTaxId() {
            return myTaxId;
        }

        /**
         * @return return the shortLabel or the taxId if the short label is not found.
         */
        public String getShortLabel() {
            if (myShortLabel == null || myShortLabel.equals("")) {
                return (myTaxId + "");
            } else {
                return myShortLabel;
            }
        }

        public String getFullName() {
            return myFullName;
        }
    }


    // Exception class for when a tax id is not found.
    public static class TaxIdNotFoundException extends Exception {
        public TaxIdNotFoundException(int taxid) {
            super("Failed to find a match for " + taxid);
        }
    }

    // ------------------------------------------------------------------------

    // Class Data

    private NewtLRUCache cache = null;

    public void enableCaching () {
        cache = new NewtLRUCache();
    }

    public void enableCaching (int maxElement) {
        cache = new NewtLRUCache(maxElement);
    }

    public void disableCaching () {
        cache = null;
    }



    /**
     * Regular expression to extract short label and fullname. The pattern is
     * -- number|short_label|full_name|ignore other text
     */
    private static final Pattern REG_EXP =
            Pattern.compile("(\\d+)\\|(.*?)\\|(.*?)\\|.*");

    /**
     * The prefix to append to the query.
     */
    private static String SEARCH_PREFIX = "mode=IntAct&search=";

    // Instance Data

    /**
     * The URL to connect to Newt server.
     */
    private URL myURL;

    /**
     * Deafult constructor. Uses the public newt url.
     * @exception MalformedURLException for invalid URL. This should never happen.
     */
    public NewtServerProxy() throws MalformedURLException {
        this(new URL("http://www.ebi.ac.uk/newt/display"));
    }

    /**
     * Constructs an instance of this class using the URL to connect to the
     * Newt server.
     *
     * @param url the URL to connect to the server.
     */
    public NewtServerProxy(URL url) {
        myURL = url;
    }

    /**
     * Queries the Newt server with given tax id.
     * @param taxid the tax id to query the Newt server.
     * @return an array with two elements. The first element contains
     * the short label and the second contains the full name (scientific name).
     * It is possible for the server to return empty values for both.
     * @exception IOException for network errors.
     * @exception TaxIdNotFoundException thrown when the server fails to find
     * a response for tax id.
     */
    public NewtResponse query (int taxid) throws IOException,
            TaxIdNotFoundException {

        NewtResponse newtRes = null;

        if (cache != null) {
            newtRes = cache.get (taxid);
            if (newtRes != null) return newtRes;
        }

        // Query the Newt server.
        String response = getNewtResponse(SEARCH_PREFIX + taxid + "\r\n");
        // Parse the newt response.

        if (response == null) {
            System.out.println("... Response from Newt("+ taxid +") is NULL ...");
            throw new TaxIdNotFoundException ( taxid );
        }

        Matcher matcher = REG_EXP.matcher(response);
        if (!matcher.matches()) {
            throw new TaxIdNotFoundException(taxid);
        }
        // Values from newt stored in
        newtRes = new NewtResponse (matcher.group(1),
                                    matcher.group(2),
                                    matcher.group(3));

        if (cache != null) cache.store (taxid, newtRes);

        return newtRes;
    }

    // Helper methods

    private String getNewtResponse(String query) throws IOException {
        URLConnection servletConnection = myURL.openConnection();
        // Turn off caching
        servletConnection.setUseCaches(false);

        // Wrting to the server.
        servletConnection.setDoOutput(true);

        // Write the taxid to the server.
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(servletConnection.getOutputStream()));
            // Send the query and flush it.
            writer.write(query);
            writer.flush();
            writer.close();
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException ioe) {
                }
            }
        }
        // The reader to read response from the server.
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(servletConnection.getInputStream()));
            // We are expcting a single line from the server.
            return reader.readLine();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ioe) {
                }
            }
        }
    }
}
