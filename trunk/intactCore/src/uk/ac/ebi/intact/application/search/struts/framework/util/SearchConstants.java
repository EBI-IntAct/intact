/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.framework.util;

/**
 * Contains constants required for the Search application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface SearchConstants {

    /**
     * The key to store an Intact Service object.
     */
    public static final String INTACT_SERVICE = "IntactService";

    /**
     * To access intact types.
     */
    public static final String INTACT_TYPES = "intacttypes";

    /**
     * The key to access a user session object.
     */
    public static final String INTACT_USER = "IntactUser";

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
     * Forward to the search page.
     */
    public static final String FORWARD_SEARCH = "search";

    /**
     * Forward to the results page.
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
     * Where to forward when hierarchView is requested.
     */
    public static final String FORWARD_HV = "hv";

    /**
     * The search criteria key to display in the results JSP.
     */
    public static final String SEARCH_CRITERIA = "searchCriteria";

    /**
     * The intact type seearched for. Typically used in error messages.
     */
    public static final String SEARCH_TYPE = "searchType";

    /**
     * The key to access configuration directory.
     */
    public static final String CONFIG_DIR = "configdir";

    /**
     * The name of the Intact Types properties file.
     */
    public static final String INTACT_TYPE_PROPS = "IntactTypes";

    /**
     * The name of the hierarchView properties file.
     */
    public static final String HV_PROPS = "HierarchView";

    /**
     * Used as a key to identify a mapping filename (for Castor).
     * the value is defined in the web.xml file
     */
    public static final String MAPPING_FILE = "mappingfile";

    /**
     * Used as a key to identify a datasource class - its value
     * is deifned in the web.xml file as a servlet context parameter
     */
    public static final String DATA_SOURCE = "datasource";

    /**
     * The name of the file containing the java to XML mapping
     */
    public static final String XML_MAPPING_FILE = "xmlMappingFile";

    /**
     * The name of the file containing the XSL stylesheet
     */
    public static final String XSL_FILE = "xslFile";
}
