/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.controller;

import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.*;

import uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search.business.IntactUserIF;
//import uk.ac.ebi.intact.application.search.struts.view.SearchForm;
import uk.ac.ebi.intact.application.search.struts.view.IntactViewBean;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.business.IntactException;

import uk.ac.ebi.intact.util.*;

/**
 * This class provides the actions required for the search page
 * for intact. The search criteria are obtained from a Form object
 * and then the search is carried out, via the IntactUser functionality,
 * which provides the business logic.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk), modified by Chris Lewington
 * @version $Id$
 */

public class SearchAction extends IntactBaseAction {

   /**
    * Process the specified HTTP request, and create the corresponding
    * HTTP response (or forward to another web component that will create
    * it). Return an ActionForward instance describing where and how
    * control should be forwarded, or null if the response has
    * already been completed.
    *
    * @param mapping - The <code>ActionMapping</code> used to select this instance
    * @param form - The optional <code>ActionForm</code> bean for this request (if any)
    * @param request - The HTTP request we are processing
    * @param response - The HTTP response we are creating
    *
    * @return - represents a destination to which the controller servlet,
    * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
    * or HttpServletResponse.sendRedirect() to, as a result of processing
    * activities of an <code>Action</code> class
    */
    public ActionForward execute (ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        // Clear any previous errors.
        super.clearErrors();

        DynaActionForm dyForm = (DynaActionForm) form;
        String searchValue = ((String) dyForm.get("searchString")).toUpperCase();
//        SearchForm theForm = (SearchForm) form;
//        String searchValue = theForm.getSearchString();

        // Session to access various session objects.
        HttpSession session = super.getSession(request);

       //make sure the view is in a consistent state by clearing the
       //set of previously expanded items...
       Set oldItems = (Set)session.getAttribute(SearchConstants.EXPANDED_AC_SET);
       if (oldItems == null) {
           //first request - set up cache
           session.setAttribute(SearchConstants.EXPANDED_AC_SET, new HashSet());
       }
       else oldItems.clear();

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);

        // The type of search object selected by the user.
        //NB need to change the "user" I/F so it is not CV specific...
        //String searchClass = theForm.getClassName();
       String searchClass = (String) dyForm.get("searchClass");

        // The class name associated with the search request.
        String classname = super.getIntactService().getClassName(searchClass);

        //now need to try searches based on AC, label or name (only
        //ones we will accept for now) and return as soon as we get a result
        //NB obviously can't distinguish between a zero return and a search
        //with garbage input using this approach...

       //get or create (if necessary) an XML builder via the Session
       XmlBuilder builder = (XmlBuilder)session.getAttribute(SearchConstants.XML_BUILDER);
       if(builder == null) {
           //create one and save in session, making sure the builder reuses
           //the current user's helper so the DB access cocnfiguration is correct
           try {
               builder = new XmlBuilder(user.getHelper());
               session.setAttribute(SearchConstants.XML_BUILDER, builder);
           }
           catch(IntactException ie) {

               //build and forward error....
               super.log(ExceptionUtils.getStackTrace(ie));
                // The errors to report back.
                super.addError("error.search", ie.getMessage());
                super.saveErrors(request);
                return mapping.findForward(SearchConstants.FORWARD_FAILURE);
           }
       }


       // The stylesheet for the transformation.
       String xslname = session.getServletContext().getInitParameter(SearchConstants.XSL_FILE);
       String xslfile = session.getServletContext().getRealPath(xslname);

        // Holds the result from the search.
        Collection results = null;

        //now need to try searches based on AC, label or name (only
       //ones we will accept for now) and return as soon as we get a result
       //NB obviously can't distinguish between a zero return and a search
       //with garbage input using this approach...
        super.log("search action: attempting to search by AC first...");
        try {
            //try searching first using all uppercase, then all lower case if it returns nothing...
            //NB this would be better done at the DB level, but keep it here for now
            String upperCaseValue = searchValue;
            results = doLookup(classname, upperCaseValue, user);
            if (results.isEmpty()) {

                //now try all lower case....
                String lowerCaseValue = searchValue.toLowerCase();
                results = doLookup(classname, lowerCaseValue, user);
                if(results.isEmpty()) {
                    //finished all current options, and still nothing - return a failure
                    super.log("No matches were found for the specified search criteria");
                    // Save the search parameters for results page to display.
                    session.setAttribute(SearchConstants.SEARCH_CRITERIA,
                    user.getSearchCritera() + "=" + searchValue);
                    session.setAttribute(SearchConstants.SEARCH_TYPE, searchClass);
                    return mapping.findForward(SearchConstants.FORWARD_NO_MATCHES);
                }

            }
            super.log("search action: search results retrieved OK");

            // ************* Search was a success. ********************************

            // Retrieve the map to add matching results.
            Map idToView = (Map) session.getAttribute(SearchConstants.FORWARD_MATCHES);
            // Remove any previous views.
            idToView.clear();

            // Save the search parameters for results page to display.
            session.setAttribute(SearchConstants.SEARCH_CRITERIA,
                user.getSearchCritera() + "=" + searchValue);

            // If we retrieved one object then we can go straight to edit page.
            if (results.size() == 1) {
                // Found a single match only; save it to determine which page to
                // return from edit.jsp; for example, results jsp or search jsp.
                // The object to display.
                Object obj = results.iterator().next();
                //set up a viewbean to hold the results for display
                IntactViewBean bean = new IntactViewBean(obj, xslfile, builder);
                idToView.put("0", bean);
                bean.createXml();

            }
            else {
                // Found multiple results.
                super.log("multiple results - performing XML conversion...");

                // The counter for view beans.
                int counter = 0;

                // Store the view beans in a map.
                for (Iterator iter = results.iterator(); iter.hasNext(); ++counter) {
                    // Construct a view bean for each search result.
                    IntactViewBean bean = new IntactViewBean(iter.next(), xslfile, builder);
                    // Collect the results together...
                    idToView.put(Integer.toString(counter), bean);
                    bean.createXml();
                    super.log("object marshalled - now building view bean...");
                }
            }
            // Move to the results page.
            return mapping.findForward(SearchConstants.FORWARD_RESULTS);
        }
        catch (IntactException se) {
            // Something failed during search...
            super.log(ExceptionUtils.getStackTrace(se));
            // The errors to report back.
            super.addError("error.search", se.getNestedMessage());
            super.saveErrors(request);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }
        catch (TransformerException te) {
            // Unable to create a transformer for given stylesheet.
            super.log(ExceptionUtils.getStackTrace(te));
            // The errors to report back.
            super.addError("error.search", te.getMessage());
            super.saveErrors(request);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }
       catch (ParserConfigurationException pe) {
            // Unable to create a transformer for given stylesheet.
            super.log(ExceptionUtils.getStackTrace(pe));
            // The errors to report back.
            super.addError("error.search", pe.getMessage());
            super.saveErrors(request);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }
    }

    // Helper methods.

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
     *
     * @return Collection the results of the search - an empty Collection if no results found
     *
     * @exception IntactException thrown if there were any search problems
     */
    private Collection doLookup(String className, String value, IntactUserIF user)
        throws IntactException {

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
