/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.framework.util;

/**
 * Contains constants required for the webIntact application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface CvEditConstants {

    /**
     * The key to store an Intact Service object.
     */
    public static final String INTACT_SERVICE = "IntactService";

    /**
     * To access intact types.
     */
    public static final String INTACT_TYPES = "intacttypes";

    /**
     * The key to access an Intact user.
     */
    public static final String INTACT_USER = "intactuser";

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
     * Forward to the CV edit page.
     */
    public static final String FORWARD_EDIT = "edit";

    /**
     * Forward to the CV delete confirm page.
     */
    public static final String FORWARD_DEL_CONFIRM = "delConfirm";

    /**
     * Used as a key to identify a page to display when matches are found
     * from a search.
     */
    public static final String FORWARD_MATCHES = "match";

    /**
     * Forward to create a CV object (short label).
     */
    public static final String FORWARD_CREATE = "add";

    /**
     * Used as a key to identify a page to display when no matches are found
     * from a search.
     */
    public static final String FORWARD_NO_MATCHES = "noMatch";

    /**
     * Forward to refresh the screen.
     */
    public static final String FORWARD_REFRESH = "refresh";

    /**
     * Search by Label.
     */
    public static final String SEARCH_BY_LABEL = "shortLabel";

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
     * The bean to display short labels when the user is adding a new CV obj.
     */
    public static final String SHORT_LABEL_BEAN = "labelBean";

    /**
     * The name of the CV info form.
     */
    public static final String CV_INFO_FORM = "cvinfoForm";

    /**
     * The name of the Xref edit form.
     */
    public static final String XREF_EDIT_FORM = "xrefEditForm";

    /**
     * The name of the Comment (annotation) edit form.
     */
    public static final String COMMENT_EDIT_FORM = "commentEditForm";
}
