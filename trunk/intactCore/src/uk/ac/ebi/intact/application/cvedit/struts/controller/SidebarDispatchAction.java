/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;

import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractDispatchAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractAction;
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
public class SidebarDispatchAction extends CvAbstractDispatchAction {

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
     * no matches if the search failed to find any records; edit mapping if the
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
        IntactUserIF user = super.getIntactUser(request);

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
            // try searching first using all uppercase, then all lower case if it returns nothing...
            // NB this would be better done at the DB level, but keep it here for now
            String upperCaseValue = searchString.toUpperCase();
            results = doLookup(classname, upperCaseValue, user);
            if (results.isEmpty()) {
                // now try all lower case....
                String lowerCaseValue = searchString.toLowerCase();
                results = doLookup(classname, lowerCaseValue, user);
            }
        }
        catch (SearchException ie) {
            // Something failed during search...
            super.log(ExceptionUtils.getStackTrace(ie));
            // The errors to report back.
            // Error with updating.
            ActionErrors errors = new ActionErrors();
            errors.add(CvAbstractAction.INTACT_ERROR,
                    new ActionError("error.search", ie.getNestedMessage()));
            super.saveErrors(request, errors);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }
        if (results.isEmpty()) {
            // No matches found - forward to a suitable page
            super.log("No matches were found for the specified search criteria");
            return mapping.findForward(CvEditConstants.FORWARD_NO_MATCHES);
        }
        // Sets the result status type.
        user.setSearchResultStatus(results.size());

        // If we retrieved one object then we can go strainght to edit page.
        if (results.size() == 1) {
            // The object to edit.
            CvObject cvobj = (CvObject) results.iterator().next();
            user.setCurrentEditObject(cvobj);

            // Straight to the edit jsp.
            return mapping.findForward(CvEditConstants.FORWARD_EDIT);
        }
        // Cache the search results.
        user.cacheSearchResult(results);

        // Move to the results page.
        return mapping.findForward(CvEditConstants.FORWARD_RESULTS);
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
        IntactUserIF user = super.getIntactUser(request);

        DynaActionForm theForm = (DynaActionForm) form;
        String  topic = (String) theForm.get("topic");
        String label = (String) theForm.get("shortLabel");

        // Validate the short label.
        if (SL_RE.matcher(label).find()) {
            ActionErrors errors = new ActionErrors();
            errors.add(CvAbstractAction.INTACT_ERROR,
                    new ActionError("error.create.shortLabel"));
            super.saveErrors(request, errors);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }

        // The class name associated with the topic.
        String classname = super.getIntactService().getClassName(topic);

        // The owner of the object we are editing.
        Institution owner = null;

        // Holds the result from the search.
        Collection result = null;

        try {
            owner = user.getInstitution();
            result = user.search(classname,
                CvEditConstants.SEARCH_BY_LABEL, label);
        }
        catch (SearchException se) {
            // Can't query the database.
            super.log(ExceptionUtils.getStackTrace(se));
            ActionErrors errors = new ActionErrors();
            errors.add(CvAbstractAction.INTACT_ERROR,
                    new ActionError("error.search", se.getNestedMessage()));
            super.saveErrors(request, errors);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }
        // result is not empty if we have this label on the database.
        if (!result.isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add(CvAbstractAction.INTACT_ERROR,
                    new ActionError("error.create",
                label + " you entered is not unique! Label must be unique."));
            super.saveErrors(request, errors);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }
        // Found a unique short label and topic combination; create a new CV.
        CvObject cvobj = createNew(classname);
        cvobj.setShortLabel(label);
        cvobj.setOwner(owner);

        try {
            // Begin the transaction.
            user.begin();
            // Create the new object on the persistence system.
            user.create(cvobj);
            // Commit all the changes.
            user.commit();
            // Set the new object as the current edit object.
            user.setCurrentEditObject(cvobj);
            // Added a new CV object; update the drop down list.
            user.refreshList();
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
            errors.add(CvAbstractAction.INTACT_ERROR,
                    new ActionError("error.create", ie1.getMessage()));
            super.saveErrors(request, errors);
        }
        catch (SearchException se) {
            // Log the stack trace.
            super.log(ExceptionUtils.getStackTrace(se));
            // Error with updating the drop down lists.
            ActionErrors errors = new ActionErrors();
            errors.add(CvAbstractAction.INTACT_ERROR,
                    new ActionError("error.search.list", se.getMessage()));
            super.saveErrors(request, errors);
        }
        // To the edit patge.
        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }

    // Helper methods

    /**
     * Construct a new instance for given class name using Java reflection.
     * @param classname the name of the class.
     * @return <code>CvObj</code> constructed form <code>classname</code>.
     */
    private CvObject createNew(String classname) {
        // Create an instance of given classname by useing reflection.
        CvObject cvobj = null;
        try {
            Class clazz = Class.forName(classname);
            cvobj = (CvObject) clazz.newInstance();
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
        return cvobj;
    }

    /**
     * utility method to handle the logic for lookup, ie trying AC, label etc.
     * Isolating it here allows us to change initial strategy if we want to.
     * NB this will probably be refactored out into the IntactHelper class later on.
     *
     * @param className the intact type to search on
     * @param value the user-specified value
     * @param user The object holding the IntactHelper for a given user/session
     * (passed as a parameter to avoid using an instance variable, which may
     *  cause thread problems).
     * @return Collection the results of the search - an empty Collection if no results found
     * @exception uk.ac.ebi.intact.persistence.SearchException thrown if there were any search problems
     */
    private Collection doLookup(String className, String value, IntactUserIF user)
        throws SearchException {

        Collection results = new ArrayList();

        //try search on AC first...
        results = user.search(className, "ac", value);
        if (results.isEmpty()) {
            // No matches found - try a search by label now...
            super.log("now searching for class " + className + " with label " + value);
            results = user.search(className, "shortLabel", value);
            if (results.isEmpty()) {
                //no match on label - try by xref....
                super.log("no match on label - looking for: " + className + " with primary xref ID " + value);
                Collection xrefs = user.search(Xref.class.getName(), "primaryId", value);

                //could get more than one xref, eg if the primary id is a wildcard search value -
                //then need to go through each xref found and accumulate the results...
                Iterator it = xrefs.iterator();
                Collection partialResults = new ArrayList();
                while (it.hasNext()) {
                    partialResults = user.search(className, "ac", ((Xref) it.next()).getParentAc());
                    results.addAll(partialResults);
                }

                if (results.isEmpty()) {
                    //no match by xref - try finally by name....
                    super.log("no matches found using ac, shortlabel or xref - trying fullname...");
                    results = user.search(className, "fullName", value);
                }
            }
        }
        return results;
    }
}
