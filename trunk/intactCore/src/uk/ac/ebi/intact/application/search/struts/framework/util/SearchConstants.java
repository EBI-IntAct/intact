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
     * Forward to the results page.
     */
    public static final String FORWARD_SESSION_LOST = "sessionLost";

    /**
     * Forward to the Action responsible for setting up a single item view
     */
    public static final String FORWARD_SINGLE_ACTION = "singleItemAction";

    /**
     * Forward to the ACtion responsible for setting up a detailed view
     */
    public static final String FORWARD_DETAIL_ACTION = "detailAction";

    /**
     * Forward to the Action responsible for processing request from a
     * special Protein view.
     */
    public static final String FORWARD_PROTEIN_ACTION = "proteinAction";

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

    /**
     * The name of the file containing the XSL stylesheet for the
     * Protein Partners view
     */
    public static final String PROTEIN_XSL_FILE = "proteinXslFile";


    /**
     * The map of items in a view page and their current mode (eg expanded or contracted)
     */
    public static final String VIEW_MODE_MAP = "modeMap";

    /**
     * To access intact types
     */
    public static final String INTACT_TYPES = "intacttypes";

    /**
     * The Protein View state.
     */
    public static final String PROTEIN_VIEW_BUTTON = "proteinView";

    /**
     * The view bean for a single CvObject.
     */
    public static final String SINGLE_OBJ_VIEW_BEAN = "cvViewBean";

    /**
     * The most recent user-defined search (ie not from a CvObject link).
     */
    public static final String LAST_VALID_SEARCH = "lastValidSearch";

    /**
     * The label used to identify the search form in a session (specified in struts-config.xml)
     */
    public static final String SEARCH_FORM = "sidebarForm";

    /**
     * The label used to identify the search results Collection (often saved in a request)
     */
    public static final String SEARCH_RESULTS = "searchResults";

    /**
     * The label used to identify a Map from search item ACs to lists of experiment ACs. Used
     * in a request passed on the ProteinAction.
     */
    public static final String RESULT_EXP_MAP = "resultExpMap";

    /**
     * The label used to identify a Map of experiment ACs to experiment view beans. Usually
     * used in a request passed on to ProteinAction.
     */
    public static final String EXP_BEAN_MAP = "expBeanMap";

    /**
     * The label used to identify a Set of beans filtered by some criteria
     */
    public static final String BEAN_FILTER = "BeanFilter";

    /**
     * The label used to identify a String of bean IDs - used mainly by protein partner view
     * This will appear as an attribute in the XML tree.
     */
    public static final String BEAN_LIST = "beanList";

    /**
     * The label used to identify a map of IDs to Protein partner beans (different display)
     */
    public static final String PARTNER_BEAN_MAP = "partnerBeanMap";

    /**
     * The label used to identify a request parameter from a protein partner
     * view link for Experiment details
     */
    public static final String VIEW_EXPS_LINK = "viewExpsLink";

}
