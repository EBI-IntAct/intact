/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

/**
 * Contains constants required for the webIntact application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface EditorConstants {

    /**
     * The name of the logger.
     */
    public static final String LOGGER = "editor";

    /**
     * The key to store an Intact Service object.
     */
    public static final String EDITOR_SERVICE = "service";

    /**
     * To access intact types.
     */
    public static final String EDITOR_TOPICS = "topics";

    /**
     * The key to access an Intact user.
     */
    public static final String INTACT_USER = "user";

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
     * Forwards to the input page.
     */
    public static final String FORWARD_INPUT = "input";

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
     * The name of the CV info form.
     */
    public static final String FORM_CVINFO = "cvInfoForm";

    /**
     * The name of the Xref edit form.
     */
    public static final String FORM_XREF_EDIT = "xrefEditForm";

    /**
     * The name of the Comment (annotation) edit form.
     */
    public static final String FORM_COMMENT_EDIT = "commentEditForm";

    // Forms related to Interaction.

    /**
     * The name of the experiment form in an Interaction.
     */
    public static final String FORM_INTERACTION_EXP = "intExperimentForm";

    /**
     * The name of the experiment hold form in an Interaction.
     */
    public static final String FORM_INTERACTION_EXP_HOLD = "intExperimentHoldForm";

    /**
     * The name of the protein form in an Interaction.
     */
    public static final String FORM_INTERACTION_PROT = "intProteinEditForm";
}
