/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.model.AnnotatedObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Action class for sidebar events. Actions are dispatched
 * according to 'dispatch' parameter.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SidebarDispatchAction extends AbstractEditorDispatchAction {

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

        LOGGER.info("search action: topic is " + topic);

        // The class name associated with the topic.
        String classname = getIntactService().getClassName(topic);

        // Try searching as it is.
        Collection results = user.lookup(classname, searchString, true);
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
        if (results.isEmpty()) {
            // No matches found - forward to a suitable page
            LOGGER.info("No matches were found for the specified search criteria");
            return mapping.findForward(EditorConstants.FORWARD_NO_MATCHES);
        }
        // If we retrieved one object then we can go strainght to edit page.
        if (results.size() == 1) {
            // The object to edit.
            AnnotatedObject annobj = (AnnotatedObject) results.iterator().next();
            user.updateView(annobj);

            // Straight to the edit jsp.
            return mapping.findForward(FORWARD_SUCCESS);
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

        // The class name associated with the topic.
        String classname = super.getIntactService().getClassName(topic);
        // The current topic.
        user.setSelectedTopic(topic);

        // The object we are about to edit.
        AnnotatedObject annobj = createNew(classname);
        annobj.setOwner(user.getInstitution());

        // Set the new object as the current edit object. This has to be
        // done before removeMenu (it relies on current cv object).
        user.updateView(annobj);
        // Need to load the current menu from the database.
//        user.getView().removeMenu();
        // Add to the view page.
//        user.addToSearchCache(annobj);
        // Started creating a new record.
//        user.setEditNew(true);
        return mapping.findForward("created");
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
