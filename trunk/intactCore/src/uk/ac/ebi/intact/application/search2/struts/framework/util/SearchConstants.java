/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.framework.util;

/**
 * Contains constants required for the Search application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface SearchConstants {

    ////////////////////////////////////////////////////
    // Configuration keys (web.xml, properties files)
    ////////////////////////////////////////////////////

    /**
     * The key to access configuration directory.
     */
    public static final String CONFIG_DIR = "configdir";

    /**
     * The name of the hierarchView properties file.
     */
    public static final String HV_PROPS = "HierarchView";

    /**
     * Used as a key to identify a datasource class - its value
     * is deifned in the web.xml file as a servlet context parameter
     */
    public static final String DATA_SOURCE = "datasource";



    ///////////////////////
    // Session keys
    ///////////////////////

    /**
     * The key to store an Intact Service object.
     */
    public static final String INTACT_SERVICE = "IntactService";

    /**
     * The key to access a user session object.
     */
    public static final String INTACT_USER = "user";

    /**
     * The search criteria key to display in the results JSP.
     */
    public static final String SEARCH_CRITERIA = "searchCriteria";

    /**
     * The view bean for a single CvObject.
     */
    public static final String VIEW_BEAN = "viewBean";

    /**
     * The most recent user-defined search (ie not from a CvObject link).
     */
    public static final String LAST_VALID_SEARCH = "lastValidSearch";

    /**
     * The label used to identify the search results Collection (often saved in a request)
     */
    public static final String SEARCH_RESULTS = "searchResults";



    ///////////////////////
    // STRUTS forwards
    ///////////////////////

    /**
     * The key to success action.
     */
    public static final String FORWARD_SUCCESS = "success";

    /**
     * Used in various action classes to define where to forward
     * to on different conditions.  See the struts-config.xml file
     * to see where the page that is using this forwards to.
     */
    public static final String FORWARD_FAILURE = "failure";

    /**
     * Forward to the results page.
     */
    public static final String FORWARD_SESSION_LOST = "sessionLost";

    /**
     * Key to the dispatcher action.
     */
    public static final String FORWARD_DISPATCHER_ACTION = "dispatcher";

    /**
     * Forward to the Action responsible for setting up a view when using the search box
     */
    public static final String FORWARD_DETAILS_ACTION = "details";

    /**
     * Forward to the Action responsible for setting up a view when using the search box
     */
    public static final String FORWARD_BINARY_ACTION = "binary";

    /**
     * Forward to the Action responsible for setting up a view when clicking on a link
     */
    public static final String FORWARD_SINGLE_ACTION = "single";

    /**
     * Forward to the Action responsible for setting up a view when clicking on a link
     */
    public static final String FORWARD_RESULTS = "results";

    /**
     * Used as a key to identify a page to display when matches are found
     * from a search.
     */
    public static final String FORWARD_MATCHES = "match";

    /**
     * Used as a key to identify a page to display when no matches are found
     * from a search.
     */
    public static final String FORWARD_NO_MATCHES = "noMatch";

    /**
     * Used as a key to identify a page to display when warning are raised
     * from a search.
     */
    public static final String FORWARD_WARNING    = "warning";
}
