/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;

import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactException;

/**
 * Action class for sidebar events. Actions are dispatched
 * according to 'dispatch' parameter.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SidebarDispatchAction extends AbstractEditorDispatchAction {

    /**
     * Only letters and numbers are allowed.
     */
    private static final Pattern SL_RE = Pattern.compile("\\W+");

    // Implements super's abstract methods.

    /**
     * Provides the mapping from resource key to method name.
     * @return Resource key / method name map.
     */
    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("button.search", "search");
        map.put("button.create", "create");
        return map;
    }

    /**
     * Searches the CV database according to search criteria from the Form
     * object.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in searching the CV database;
     * no matches if the search failed to find any records; success if the
     * search produced only a single match; finally, results mapping if the
     * search produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward search(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = super.getIntactUser(request);

        // The form to access input data.
        DynaActionForm theForm = (DynaActionForm) form;

        // The topic selected by the user.
        String topic = (String) theForm.get("topic");
        user.setSelectedTopic(topic);

        String searchString = (String) theForm.get("searchString");

        super.log("search action: topic is " + topic);

        // The class name associated with the topic.
        String classname = super.getIntactService().getClassName(topic);

        // Holds the result from the search.
        Collection results = null;

        try {
            // Try searching as it is.
            results = user.lookup(classname, searchString, true);
            if (results.isEmpty()) {
                // try searching first using all uppercase, then all lower case
                // if it returns nothing...
                // NB this would be better done at the DB level, but keep it here for now
                results = user.lookup(classname, searchString.toUpperCase(), true);
                if (results.isEmpty()) {
                    // now try all lower case....
                    results = user.lookup(classname, searchString.toLowerCase(), true);
                }
            }
        }
        catch (SearchException ie) {
            // Something failed during search...
            super.log(ExceptionUtils.getStackTrace(ie));
            // The errors to report back.
            // Error with updating.
            ActionErrors errors = new ActionErrors();
            errors.add(AbstractEditorAction.EDITOR_ERROR,
                    new ActionError("error.search", ie.getNestedMessage()));
            super.saveErrors(request, errors);
            return mapping.findForward(EditorConstants.FORWARD_FAILURE);
        }
        if (results.isEmpty()) {
            // No matches found - forward to a suitable page
            super.log("No matches were found for the specified search criteria");
            return mapping.findForward(EditorConstants.FORWARD_NO_MATCHES);
        }
        // If we retrieved one object then we can go strainght to edit page.
        if (results.size() == 1) {
            // The object to edit.
            AnnotatedObject annobj = (AnnotatedObject) results.iterator().next();
            user.updateView(annobj);

            // Straight to the edit jsp.
            return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
        }
        // Cache the search results.
        user.addToSearchCache(results);

        // Move to the results page.
        return mapping.findForward(EditorConstants.FORWARD_RESULTS);
    }

    /**
     * Creates a new CV object for a topic. The topic is obtained from the form.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in searching the CV database for
     * the topic; success mapping if a CV object was created successfully.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward create(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = super.getIntactUser(request);

        DynaActionForm theForm = (DynaActionForm) form;
        String topic = (String) theForm.get("topic");
        String label = (String) theForm.get("shortLabel");

        // Validate the short label.
        if (SL_RE.matcher(label).find()) {
            ActionErrors errors = new ActionErrors();
            errors.add(AbstractEditorAction.EDITOR_ERROR,
                    new ActionError("error.create.shortLabel"));
            super.saveErrors(request, errors);
            return mapping.findForward(EditorConstants.FORWARD_FAILURE);
        }

        // The class name associated with the topic.
        String classname = super.getIntactService().getClassName(topic);
        // The current topic.
        user.setSelectedTopic(topic);

        // The owner of the object we are editing.
        Institution owner = null;

        // Holds the result from the search.
        Collection result = null;

        try {
            owner = user.getInstitution();
            result = user.search(classname, "shortLabel", label);
        }
        catch (SearchException se) {
            // Can't query the database.
            super.log(ExceptionUtils.getStackTrace(se));
            ActionErrors errors = new ActionErrors();
            errors.add(AbstractEditorAction.EDITOR_ERROR,
                    new ActionError("error.search", se.getNestedMessage()));
            super.saveErrors(request, errors);
            return mapping.findForward(EditorConstants.FORWARD_FAILURE);
        }
        // result is not empty if we have this label on the database.
        if (!result.isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add(AbstractEditorAction.EDITOR_ERROR,
                    new ActionError("error.create",
                            label + " you entered is not unique! Label must be unique."));
            super.saveErrors(request, errors);
            return mapping.findForward(EditorConstants.FORWARD_FAILURE);
        }
        // Found a unique short label and topic combination; create a new instance.
        AnnotatedObject annobj = createNew(classname);
        annobj.setShortLabel(label);
        annobj.setOwner(owner);

        try {
            // Begin the transaction.
            user.begin();
            // Create the new object on the persistence system.
            user.create(annobj);
            // Commit all the changes.
            user.commit();
        }
        catch (IntactException ie1) {
            try {
                user.rollback();
            }
            catch (IntactException ie2) {
                // Oops! Problems with rollback; ignore this as this
                // error is reported via the main exception (ie1).
            }
            // Log the stack trace.
            super.log(ExceptionUtils.getStackTrace(ie1));
            // Error with creating the new CV object changes.
            ActionErrors errors = new ActionErrors();
            errors.add(AbstractEditorAction.EDITOR_ERROR,
                    new ActionError("error.create", ie1.getMessage()));
            super.saveErrors(request, errors);
            return mapping.findForward(EditorConstants.FORWARD_FAILURE);
        }
        // Set the new object as the current edit object. This has to be
        // done before removeMenu (it relies on current cv object).
        user.updateView(annobj);
        // Need to load the current menu from the database.
        user.getView().removeMenu();
        // Add to the view page.
        user.addToSearchCache(annobj);
        return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
    }

    // Helper methods

    /**
     * Construct a new instance for given class name using Java reflection.
     * @param classname the name of the class.
     * @return <code>AnnotatedObject</code> constructed form
     * <code>classname</code>.
     */
    private AnnotatedObject createNew(String classname) {
        // Create an instance of given classname by useing reflection.
        AnnotatedObject annobj = null;
        try {
            Class clazz = Class.forName(classname);
            annobj = (AnnotatedObject) clazz.newInstance();
        }
        catch (IllegalAccessException iae) {
            // Shouldn't happen.
            assert false;
        }
        catch (ClassNotFoundException cnfe) {
            // Shouldn't happen.
            assert false;
        }
        catch (InstantiationException ie) {
            // Shouldn't happen.
            assert false;
        }
        return annobj;
    }
}
