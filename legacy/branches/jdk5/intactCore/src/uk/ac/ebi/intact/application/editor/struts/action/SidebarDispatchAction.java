/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.ojb.broker.query.Query;
import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.wrappers.ResultRowData;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
 *      name="create"
 *      path="/do/choose"
 *
 * @struts.action-forward
 *      name="secure"
 *      path="/do/secure/edit"
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

        // The class for the topic.
        Class searchClass = Class.forName(getService().getClassName(topic));

        // The array to store queries.
        Query[] queries = getSearchQueries(searchClass, searchString);

        // The results to display.

        List results = super.search(queries, max, request);

        if (results.isEmpty()) {
            // Errors or empty or too large
            return mapping.findForward(FAILURE);
        }

        // Only one instance found?
        if (results.size() == 1) {
            // The search returned only one instance.
            ResultRowData row = (ResultRowData) results.get(0);

            // Try to acquire the lock.
            ActionErrors errors = acquire(row.getAc(), user.getUserName());
            if (errors != null) {
                saveErrors(request, errors);
                return mapping.findForward(FAILURE);
            }
            // Set the attributes in the request for the results action to get them
            request.setAttribute("ac", row.getAc());
            request.setAttribute("type", row.getType());

            // Go through the secure action.
            return mapping.findForward("secure");
        }
        // Multiple results found. Cache the search results.
        user.addToSearchCache(results);

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

        // The class we are about to create
        Class clazz = Class.forName(classname);

        // Set the new object as the current edit object.
        user.setView(clazz);
        
        // Add a no-uniprot-update annotation when a protein is created
        if(clazz.equals(ProteinImpl.class)){
            IntactHelper helper=user.getIntactHelper();

            // The topic for new annotation.
            CvTopic cvTopic = (CvTopic) helper.getObjectByLabel(CvTopic.class,
                    "no-uniprot-update");

            Annotation annotation = new Annotation(getService().getOwner(),
                    cvTopic,cvTopic.getFullName());

            // Add the new annotation as a bean.
            CommentBean cb = new CommentBean(annotation);
            user.getView().addAnnotation(cb);
        }
        return mapping.findForward("create");
    }
}
