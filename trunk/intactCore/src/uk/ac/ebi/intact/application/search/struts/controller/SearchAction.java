/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.controller;

import java.util.*;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;

//Castor classes needed for XML operations
import org.exolab.castor.xml.*;
import org.exolab.castor.mapping.*;

import uk.ac.ebi.intact.application.search.struts.framework.util.*;
import uk.ac.ebi.intact.application.search.struts.framework.*;
import uk.ac.ebi.intact.application.search.business.*;
import uk.ac.ebi.intact.application.search.struts.view.*;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.business.*;

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
        Marshaller marshaller = null;
        String mappingFile = servlet.getServletContext().getInitParameter(WebIntactConstants.XML_MAPPING_FILE);
        super.log("setting up XML marshalling resources - using mapping file " + mappingFile);
        try{
            xmlMapping.loadMapping(getClass().getResource(mappingFile));
        }
        catch(Exception e) {
            super.log("Search [ERROR] unable to load XML mapping file" + mappingFile);
            super.log(ExceptionUtils.getStackTrace(e));
        }

        //define the XML to be written to a String so we can store it simply in the view bean...
        StringWriter writer = new StringWriter();
        try{
            marshaller = new Marshaller(writer);
            marshaller.setMapping(xmlMapping);
        }
        catch (Exception e){
            super.log("Search [ERROR] unable to initialise the XML marshaller");
            super.log(ExceptionUtils.getStackTrace(e));
        }



        // Holds the result from the search.
        Collection results = null;

        super.log("search action: attempting to search by AC first...");
        try {
            results = user.search(classname, searchParam, searchValue);
            if (results.isEmpty()) {
                // No matches found - try a search by label now...
                Object result = null;
                try {
                    result = user.getObjectByLabel(Class.forName(classname), searchValue);
                }
                catch(ClassNotFoundException ce) {
                    super.log("error - can't find class " + classname + ce.toString());
                }

                if(result == null) {

                    //no match on a label - try by xref (primary id)...
                    try {
                        super.log("looking for: " + classname + " with primary xref ID " + searchValue);
                    result = user.getObjectByXref(Class.forName(classname), searchValue);
                    }
                    catch(ClassNotFoundException ce) {
                        super.log("error - can't find class " + classname + ce.toString());
                    }
                    catch (IntactException se) {
                        // Something failed - maybe teh xref doesn't link back to the object?
                        super.log(ExceptionUtils.getStackTrace(se));
                        // The errors to report back.
                        super.addError("error.search", "the error [" + se.getMessage() + "] occurred. There may be no reference to " + searchClass + " objects from an xref");
                        super.saveErrors(request);
                        return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
                    }
                    if(result == null) {

                        //no match by xref - try finally by name....
                        results = user.search(classname, "fullName", searchValue);
                        if(results.isEmpty()) {

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

                //whatever way we got an object, add it to the collection
                if(result != null) {
                    results.add(result);
                }
            }
            super.log("search action: search results retrieved OK");

            //set up a viewbean to hold the results for display
            IntactViewBean viewbean = super.getViewBean(session);

            // If we retrieved one object then we can go straight to edit page.
            if (results.size() == 1) {
                // Found a single match only; save it to determine which page to
                // return from edit.jsp; for example, results jsp or search jsp.
                session.setAttribute(WebIntactConstants.SINGLE_MATCH, Boolean.TRUE);

                // The object to display.
                Object obj = results.iterator().next();
                viewbean.initialise(obj);

                //marshal the results into XML and put the data into the view bean
                if(marshaller != null) {
                    try{

                        /*super.log("Object about to be converted to XML");
                        super.log(viewbean.getWrappedObject().toString());*/

                        marshaller.marshal(viewbean.getWrappedObject());

                        //now get the XML result from the writer and log it...
                        StringBuffer buf = writer.getBuffer();

                        /*super.log("XML data built:");
                        //strip out the xml header and add a few breaks first
                        String tmp = buf.toString();
                        String data = tmp.replaceAll("><", ">\n<");
                        super.log(data);*/

                        //set the raw XML data into the bean
                        viewbean.setAsXml(buf.toString());

                        /*super.log("XML generated: ");
                        super.log(viewbean.getAsXml());*/


                    }
                    catch(MarshalException e){
                        super.log("SearchAction [ERROR] failed to marshal results into XML format!");
                        super.log(ExceptionUtils.getStackTrace(e));
                    }
                    catch(org.exolab.castor.xml.ValidationException ve){
                        super.log("SearchAction [ERROR] marshalling failed - validation problem...");
                        super.log(ExceptionUtils.getStackTrace(ve));
                    }
                }
                else {

                    //no marshaller - just write the object as a modified string
                    super.log("no marshaller to write as XML - will use as string instead..");

                }

                //put the view bean into the session for use by the JSPs
                session.setAttribute(WebIntactConstants.VIEW_BEAN, viewbean);
                // Save the search parameters for results page to display.
                session.setAttribute(WebIntactConstants.SEARCH_CRITERIA,
                searchParam + "=" + searchValue);
                return mapping.findForward(WebIntactConstants.FORWARD_RESULTS);
            }
            else {
                // Found multiple results.
                super.log("multiple results - performing XML conversion...");
                session.setAttribute(WebIntactConstants.SINGLE_MATCH, Boolean.FALSE);

                // The collection to hold our List objects for display tag API.
                Collection container = new ArrayList();
                for (Iterator iter = results.iterator(); iter.hasNext();) {

                    //for each search result, convert it to XML then put into
                    // a viewbean and collect them together....
                    //NB move the null check to outside!
                    if(marshaller != null) {
                        try{

                            Object resultItem = iter.next();

                            /*super.log("Object about to be converted to XML");
                            super.log(resultItem.toString());*/

                            //NB have to use the same Writer object every time, as
                            //Castor's method to use other writers is static and
                            //will ignore the mapping file!!

                            //first we need to 'flush' the Writer if necessary - as
                            //StringWriter simply wraps a StringBuffer, just get the buffer
                            //and empty it (because the StringWriter flush method is a no-op)
                            if(writer.getBuffer().length() > 0) {

                                //need to 'flush' to avoid memory problems
                                writer.getBuffer().delete(0, writer.getBuffer().length()-1);
                            }
                            marshaller.marshal(resultItem);

                            super.log("object marshalled - now building view bean...");
                            //now get the XML result from the writer and put it into a bean...
                            StringBuffer buffer = writer.getBuffer();
                            if(buffer.charAt(0) == '>') {

                                //Annoying Castor bug which starts the XML with a
                                //closed bracket when using the same marshaller more
                                //than once. It needs to be removed otherwise parsing
                                //will fail later!!
                                buffer.delete(0,1);
                            }
                            IntactViewBean bean = new IntactViewBean();
                            bean.initialise(resultItem);
                            bean.setAsXml(buffer.toString());


                            /*super.log("XML created:");
                            super.log(bean.getAsXml());
                            super.log("");
*/
                            //collect the results together...
                            container.add(bean);

                        }
                        catch(MarshalException e){
                            super.log("SearchAction [ERROR] failed to marshal results into XML format!");
                            super.log(ExceptionUtils.getStackTrace(e));
                        }
                        catch(org.exolab.castor.xml.ValidationException ve){
                            super.log("SearchAction [ERROR] marshalling failed - validation problem...");
                            super.log(ExceptionUtils.getStackTrace(ve));
                        }
                    }
                    else {

                        //no marshaller - just write the object as a modified string
                        super.log("no marshaller to write as XML - will use as string instead..");

                    }

                }
                // Save it in a session for a JSP to display.
                session.setAttribute(WebIntactConstants.FORWARD_MATCHES, container);

                // Move to the results page.
                // Save the search parameters for results page to display.
                session.setAttribute(WebIntactConstants.SEARCH_CRITERIA,
                searchParam + "=" + searchValue);
                return mapping.findForward(WebIntactConstants.FORWARD_RESULTS);
            }
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

}
