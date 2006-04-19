/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.test;

import com.meterware.httpunit.*;
import junit.framework.TestCase;

/**
 * Super class for all search test cases based on the IntactLarge Postgres Dataset
 * This class provides basic url-based test methods for the core actions of search 3.
 *
 * @author Michael Kleen (mkleen@ebi.ac.uk)
 * @version $Id$
 */
public abstract class SearchTestCase extends TestCase {

    private static final String OUR_HOST = "localhost";
    private static final int OUR_PORT = 8080;
    private static final String ACTION_PATH = "/intact/search/do";

    /**
     * @param Url the url for entering search
     * @return the Content Page based on the tiles definition as webtable
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the the
     *                             webtable.
     */
    protected WebResponse callUrl(String Url) throws Exception {
        String url = "http://" + OUR_HOST + ":" + OUR_PORT + Url;
        WebRequest request = new GetMethodWebRequest(url);
        WebConversation conversation = new WebConversation();
        WebResponse response = conversation.getResponse(request);
        return response;
    }

    /**
     * @param Url the url for entering search
     * @return the Content Page based on the tiles definition as webtable
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the the
     *                             webtable.
     */
    protected WebTable[] getContentbyUrl(String Url) throws Exception {
        WebResponse response = this.callUrl(Url);
        // take all tables from the website and catch the content table
        WebTable[] tables = response.getTables();
        WebTable[] content = tables[0].getTableCell(0, 1).getTables()[0].getTableCell(2, 0)
                .getTables();

        return content;
    }

    /**
     * @param searchValue the searchValue for entering search action
     * @return the Content Page based on the tiles definition as webtable
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the the
     *                             webtable.
     */
    protected WebTable[] getContentbySearchAction(String searchValue) throws Exception {
        return this.getContentbyUrl(ACTION_PATH + "/search?" + searchValue);
    }

    /**
     * @param searchValue the searchValue for entering search action
     * @param searchClass searchclass the searchValue for entering search action
     * @return the Content Page based on the tiles definition as webtable
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the the
     *                             webtable.
     */
    protected WebTable[] getContentbySearchAction(String searchValue, String searchClass)
            throws Exception {
        return this.getContentbySearchAction(searchValue + "&searchClass=" + searchClass);
    }

    /**
     * @param searchValue e the searchValue for entering search action
     * @param searchClass the searchclass for entering search action
     * @param chunk       chunk Value the searchValue for entering search action
     * @return the Content Page based on the tiles definition as webtable
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the the
     *                             webtable.
     */
    protected WebTable[] getContentbySearchAction(String searchValue, String searchClass,
                                                  String chunk)
            throws Exception {
        return this.getContentbySearchAction(searchValue + "&searchClass=" + searchClass);
    }

    /**
     * @param searchValue searchValue the searchValue for entering search action
     * @param searchClass searchClass the searchValue for entering search action
     * @param chunk       chunk value the searchValue for entering search action
     * @param source      source value the searchValue for entering search action
     * @return the Content Page based on the tiles definition as webtable
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the the
     *                             webtable.
     */
    protected WebTable[] getContentbySearchAction(String searchValue, String searchClass,
                                                  String chunk, String source) throws Exception {
        return this.getContentbySearchAction(
                searchValue + "&searchClass=" + searchClass + "&source=" + source);
    }

    /**
     * @param binaryValue binaryValue for entering the binaryAction
     * @return the Content Page based on the tiles definition as webtable
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the the
     *                             webtable
     */
    protected WebTable[] getContentBinaryAction(String binaryValue) throws Exception {
        return this.getContentbySearchAction("binary=" + binaryValue);
    }

    /**
     * @param aProtein1 String as searchValue for the first protein
     * @param aProtein2 String as searchValue for the second protein
     * @return the Content Page basaed on the tiles definition as webtable
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the the
     *                             webtable
     */
    protected WebTable[] getContentBinaryAction(String aProtein1, String aProtein2)
            throws Exception {
        return this.getContentbySearchAction("binary=" + aProtein1 + ",%20" + aProtein2);
    }
}
