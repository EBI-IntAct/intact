/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.controller;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.*;

//Castor classes needed for XML operations
import org.exolab.castor.xml.*;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;

import uk.ac.ebi.intact.application.search.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.search.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search.business.IntactUserIF;
import uk.ac.ebi.intact.application.search.struts.view.SearchForm;
import uk.ac.ebi.intact.application.search.struts.view.IntactViewBean;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.business.IntactException;

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
    public ActionForward perform (ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        // Clear any previous errors.
        super.clearErrors();

        // Set up variables used during searching
        //default the param to AC
        String searchParam = "ac";
        String searchValue = null;

        SearchForm theForm = (SearchForm) form;

        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);

        // The type of search object selected by the user.
        //NB need to change the "user" I/F so it is not CV specific...
        String searchClass = theForm.getClassName();

        // The class name associated with the topic.
        String classname = super.getIntactService().getClassName(searchClass);

        //now need to try searches based on AC, label or name (only
        //ones we will accept for now) and return as soon as we get a result
        //NB obviously can't distinguish between a zero return and a search
        //with garbage input using this approach...

        //convert to uppercase to be on the safe side...
        searchValue = theForm.getSearchString().toUpperCase();

        //set up the Cator XML mapping resources...
        Mapping xmlMapping = new Mapping(getClass().getClassLoader());
        String mappingFile = servlet.getServletContext().getInitParameter(
                WebIntactConstants.XML_MAPPING_FILE);
        super.log("setting up XML marshalling resources - using mapping file " + mappingFile);
        try {
            xmlMapping.loadMapping(getClass().getResource(mappingFile));
        }
        catch (Exception e) {
            super.log("Search [ERROR] unable to load XML mapping file" + mappingFile);
            super.log(ExceptionUtils.getStackTrace(e));
        }

       // The stylesheet for the transformation.
       String xslname =  session.getServletContext().getInitParameter(WebIntactConstants.XSL_FILE);
       String xslfile =  session.getServletContext().getRealPath(xslname);

        //define the XML to be written to a String so we can store it simply in the view bean...
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = null;
        try {
           db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException pce) {
            super.log("Search [ERROR] unable to create a document builder");
            super.log(ExceptionUtils.getStackTrace(pce));
        }
        // Holds the result from the search.
        Collection results = null;

        super.log("search action: attempting to search by AC first...");
        try {
            results = user.search(classname, searchParam, searchValue);
            if (results.isEmpty()) {
                // No matches found - try a search by label now...
                super.log("now searching for class " + classname + " with label " + searchValue);
                results = user.search(classname, "shortLabel", searchValue);
                if (results.isEmpty()) {
                    //no match on label - try by xref....
                    super.log("no match on label - looking for: " + classname + " with primary xref ID " + searchValue);
                    Collection xrefs = user.search(Xref.class.getName(), "primaryId", searchValue);

                    //could get more than one xref, eg if the primary id is a wildcard search value -
                    //then need to go through each xref found and accumulate the results...
                    Iterator it = xrefs.iterator();
                    Collection partialResults = new ArrayList();
                    while (it.hasNext()) {
                        partialResults = user.search(classname, "ac", ((Xref) it.next()).getParentAc());
                        results.addAll(partialResults);
                    }

                    if (results.isEmpty()) {
                        //no match by xref - try finally by name....
                        super.log("no matches found using ac, shortlabel or xref - trying fullname...");
                        results = user.search(classname, "fullName", searchValue);
                        if (results.isEmpty()) {
                            //finished all current options - return a failure
                            super.log("No matches were found for the specified search criteria");
                            // Save the search parameters for results page to display.
                            session.setAttribute(WebIntactConstants.SEARCH_CRITERIA,
                            searchParam + "=" + searchValue);
                            session.setAttribute(WebIntactConstants.SEARCH_TYPE, searchClass);
                            return mapping.findForward(WebIntactConstants.FORWARD_NO_MATCHES);
                        }
                        else {
                            //got a match on name - flag it for info later
                            searchParam = "name";
                        }
                    }
                    else {
                        //got a match on xref - save for later info
                        searchParam = "primary ID";
                    }
                }
                else {
                    //matched on a label - flag it for later info
                    searchParam = "label";
                }
            }
            super.log("search action: search results retrieved OK");

            // ************* Search was a success. ********************************

            // Retrieve the map to add matching results.
            Map idToView = (Map) session.getAttribute(WebIntactConstants.FORWARD_MATCHES);
            // Remove any previous views.
            idToView.clear();

            // Save the search parameters for results page to display.
            session.setAttribute(WebIntactConstants.SEARCH_CRITERIA,
                searchParam + "=" + searchValue);

            // If we retrieved one object then we can go straight to edit page.
            if (results.size() == 1) {
                // Found a single match only; save it to determine which page to
                // return from edit.jsp; for example, results jsp or search jsp.
                session.setAttribute(WebIntactConstants.SINGLE_MATCH, Boolean.TRUE);
                // The object to display.
                Object obj = results.iterator().next();
                //set up a viewbean to hold the results for display
                IntactViewBean bean = new IntactViewBean(obj);
                bean.setStylesheet(xslfile);
                idToView.put("0", bean);
                marshall(bean, xmlMapping, db);
                bean.addStatusNodes();
            }
            else {
                // Found multiple results.
                super.log("multiple results - performing XML conversion...");
                session.setAttribute(WebIntactConstants.SINGLE_MATCH, Boolean.FALSE);

                // The counter for view beans.
                int counter = 0;

                // The collection to hold our List objects for display tag API.
                for (Iterator iter = results.iterator(); iter.hasNext(); ++counter) {
                    // Construct a view bean for each search resul.
                    IntactViewBean bean = new IntactViewBean(iter.next());
                    bean.setStylesheet(xslfile);
                    // Collect the results together...
                    idToView.put(Integer.toString(counter), bean);
                    marshall(bean, xmlMapping, db);
                    bean.addStatusNodes();
                    super.log("object marshalled - now building view bean...");
                }
            }
            // Move to the results page.
            return mapping.findForward(WebIntactConstants.FORWARD_RESULTS);
        }
        catch (IntactException se) {
            // Something failed during search...
            super.log(ExceptionUtils.getStackTrace(se));
            // The errors to report back.
            super.addError("error.search", se.getNestedMessage());
            super.saveErrors(request);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
    }

    // Helper methods.

    /**
     * Helper method to marshal an object.
     * @param bean the bean to marshall.
     * @param mapping the XML mapping file.
     * @param db the builder to create a Document node.
     */
    private void marshall(IntactViewBean bean, Mapping mapping, DocumentBuilder db) {
        try {
            bean.marshall(mapping, db);
        }
        catch (MappingException me){
            super.log("Search [ERROR] unable to initialise the XML marshaller");
            super.log(ExceptionUtils.getStackTrace(me));
        }
        catch (MarshalException e){
            super.log("SearchAction [ERROR] failed to marshal results into XML format!");
            super.log(ExceptionUtils.getStackTrace(e));
        }
        catch (ValidationException ve){
            super.log("SearchAction [ERROR] marshalling failed - validation problem...");
            super.log(ExceptionUtils.getStackTrace(ve));
        }
    }
}
