/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.commons.search.ResultWrapper;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.AnnotatedObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Action class for sidebar events. Actions are dispatched
 * according to 'dispatch' parameter.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 *
 * @struts.action
 *      path="/sidebar"
 *      name="sidebarForm"
 *      validate="false"
 *      parameter="dispatch"
 *
 * @struts.action-forward
 *      name="single"
 *      path="/do/choose"
 *
 * @struts.action-forward
 *      name="create"
 *      path="/do/choose"
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

        // The topic and the search para selected by the user.
        String topic = (String) theForm.get("topic");
        String searchString = (String) theForm.get("searchString");

        // The current topic, so the sidebar displays this as the currently
        // selected type.
        user.setSelectedTopic(topic);

        LOGGER.info("The current topic is " + topic);

        // The maximum number of items to retrieve.
        int max = getService().getInteger("search.max");

        // The wrapper to hold lookup result.
        ResultWrapper rw = null;
        try {
            rw = user.lookup(getService().getClassName(topic), searchString, max);
        }
        catch (IntactException ie) {
            // This can only happen when problems with creating an internal helper
            // This error is already logged from the User class.
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.intact"));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }

        // Check the size
        if (rw.isTooLarge()) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.search.large",
                            Integer.toString(rw.getPossibleResultSize())));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }

        if (rw.isEmpty()) {
            // No matches found - forward to a suitable page
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.search.nomatch",
                            user.getSearchCriteria().getQuery(), user.getSelectedTopic()));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }

        // If we retrieved one object then we can go straight to edit page.
        if (rw.getResult().size() == 1) {
            // The object to edit.
            AnnotatedObject annobj = (AnnotatedObject) rw.getResult().iterator().next();

            // The ac of the object about to edit.
            String ac = annobj.getAc();

            // Try to acuire the lock.
            ActionErrors errors = acquire(ac, user.getUserName());
            if (errors != null) {
                saveErrors(request, errors);
                return mapping.findForward(FAILURE);
            }
            // Set the view to this annotated object.
            user.setView(annobj);

            // Single item found
            return mapping.findForward("single");
        }
        // Cache the search results.
        user.addToSearchCache(rw.getResult());

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

        // Set the topic as the selected topic.
        DynaActionForm theForm = (DynaActionForm) form;
        String topic = (String) theForm.get("topic");
        user.setSelectedTopic(topic);

        // The class name associated with the topic.
        String classname = getService().getClassName(topic);

        // Set the new object as the current edit object.
        user.setView(Class.forName(classname));
        return mapping.findForward("create");
    }
}
