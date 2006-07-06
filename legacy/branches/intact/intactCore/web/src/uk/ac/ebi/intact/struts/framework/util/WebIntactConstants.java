/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.framework.util;

/**
 * Contains constants required for the webIntact application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface WebIntactConstants {

    /**
     * The key to store an Intact Service object.
     */
    public static final String SERVICE_INTERFACE =
        "uk.ac.ebi.intact.struts.service.IntactService";

    /**
     * The key to store an Intact Service object.
     */
    public static final String AVAILABLE_TOPICS = "topics";

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
     * The key to store the failure bean.
     */
    public static final String FAILURE_BEAN = "failureBean";

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
     * Search by AC.
     */
    public static final String SEARCH_BY_AC = "searchAC";

    /**
     * Table column name for AC field.
     */
    public static final String SEARCH_BY_AC_FIELD = "ac";

    /**
     * Search by Label.
     */
    public static final String SEARCH_BY_LABEL = "searchLabel";

    /**
     * Table column name for label field.
     */
    public static final String SEARCH_BY_LABEL_FIELD = "shortLabel";

    /**
     * The search criteria key to display in the results JSP.
     */
    public static final String SEARCH_CRITERIA = "searchCriteria";

    /**
     * The key to access a user session object.
     */
    public static final String USER_SESSION = "user_session";

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
     * A key to identify an intact object types property file -
     * its value is defined in the web.xml file as a servlet context parameter.
     */
    public static final String INTACT_TYPES_FILE = "intacttypesfile";

    /**
     * The name of an attribute set in the session.
     */
    public static final String ATTRIBUTE_SEARCH = "search";

    /**
     * The annotations are saved under this key.
     */
    public static final String ANNOTATIONS = "annotations";

    /**
     * Cross References are saved under this key.
     */
    public static final String XREFS = "xrefs";

    /**
     * Used as a key to identify the page to add/delete a comment.
     */
    public static final String FORWARD_COMMENT = "comment";

    /**
     * The current annotated bean selected from details page for edit.
     */
    public static final String SELECTED_COMMENT = "selectedComment";

    /**
     * The Intact bean to display.
     */
    //public static final String INTACT_VIEW_BEAN = "CvViewBean";
}
