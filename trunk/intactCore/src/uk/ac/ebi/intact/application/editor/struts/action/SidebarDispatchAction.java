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
import uk.ac.ebi.intact.model.AnnotatedObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

        // Remove any locks held by the user.
        user.releaseLock(getLockManager());

        // The form to access input data.
        DynaActionForm theForm = (DynaActionForm) form;

        // The topic selected by the user.
        String topic = (String) theForm.get("topic");
        user.setSelectedTopic(topic);

        String searchString = (String) theForm.get("searchString");

        LOGGER.info("search action: topic is " + topic);

        // Try searching as it is.
        Collection results = user.lookup(topic, searchString);
        if (results.isEmpty()) {
            // No matches found - forward to a suitable page
            LOGGER.info("No matches were found for the specified search criteria");
            return mapping.findForward(NO_MATCH);
        }
        // If we retrieved one object then we can go strainght to edit page.
        if (results.size() == 1) {
            // The object to edit.
            AnnotatedObject annobj = (AnnotatedObject) results.iterator().next();
            user.updateView(annobj);

            // Single item found
            return mapping.findForward("single");
        }
        // Cache the search results.
        user.addToSearchCache(results, getLockManager());

        // Move to the results page.
        return mapping.findForward(RESULT);
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
        EditUserI user = getIntactUser(request);

        // Remove any locks held by the user.
        user.releaseLock(getLockManager());

        DynaActionForm theForm = (DynaActionForm) form;
        String topic = (String) theForm.get("topic");

        // The class name associated with the topic.
        String classname = getService().getClassName(topic);
        // The current topic.
        user.setSelectedTopic(topic);

        // The object we are about to edit.
        AnnotatedObject annobj = createNew(classname);
        annobj.setOwner(user.getInstitution());

        // Set the new object as the current edit object. This has to be
        // done before removeMenu (it relies on current cv object).
        user.updateView(annobj);
        return mapping.findForward("create");
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
            Constructor[] ctrs = clazz.getDeclaredConstructors();
            Constructor noargctr = null;
            for (int i = 0; i < ctrs.length; i++) {
                if (ctrs[i].getParameterTypes().length == 0) {
                    noargctr = ctrs[i];
                    break;
                }
            }
            if (noargctr != null) {
                noargctr.setAccessible(true);
                annobj = (AnnotatedObject) noargctr.newInstance(null);
            }
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
        catch (InvocationTargetException e) {
            // Thrown from the constructing the object.
            assert false;
        }
        return annobj;
    }
}
