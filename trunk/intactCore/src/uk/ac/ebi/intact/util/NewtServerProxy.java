/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util;

import java.net.URL;
import java.net.URLConnection;
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

    // Inner Classes
    // ------------------------------------------------------------------------
    public static class NewtResponse {
        private String myShortLabel;
        private String myFullName;

        private NewtResponse(String shortLabel, String fullName) {
            myShortLabel= shortLabel;
            myFullName = fullName;
        }

        // True if the response has a short label.
        public boolean hasShortLabel() {
            return myShortLabel.length() != 0;
        }

        // Only getter methods.
        public String getShortLabel() {
            return myShortLabel;
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
    public NewtResponse query(int taxid) throws IOException,
            TaxIdNotFoundException {
        // Query the Newt server.
        String response = getNewtResponse(SEARCH_PREFIX + taxid + "\r\n");
        // Response can be null for some tax id (e.g., 38081).
        if (response == null) {
           throw new TaxIdNotFoundException(taxid);
        }
        // Parse the newt response.
        Matcher matcher = REG_EXP.matcher(response);
        if (!matcher.matches()) {
            throw new TaxIdNotFoundException(taxid);
        }
        // Values from newt stored in
        NewtResponse newtRes = new NewtResponse(matcher.group(2), matcher.group(3));
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
