/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.mine.struts.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import uk.ac.ebi.intact.application.commons.search.SearchHelper;
import uk.ac.ebi.intact.application.commons.search.SearchHelperI;
import uk.ac.ebi.intact.application.mine.business.Constants;
import uk.ac.ebi.intact.application.mine.business.IntactUser;
import uk.ac.ebi.intact.application.mine.business.IntactUserI;
import uk.ac.ebi.intact.application.mine.struts.view.AmbiguousBean;
import uk.ac.ebi.intact.application.mine.struts.view.ErrorForm;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Interactor;

/**
* Performs a database search for the given search phrases. If the results are
* ambiguous the application is forwarded to a page to display all results.
* Otherwise the application is forwarded to start the algorithm.
* 
* @author Andreas Groscurth
*/
public class SearchAction extends Action {
   public ActionForward execute(ActionMapping mapping, ActionForm form,
           HttpServletRequest request, HttpServletResponse response) {
       HttpSession session = request.getSession(true);
       IntactUserI user = (IntactUserI) session.getAttribute(Constants.USER);
       try {
           // if no user exists in the session a new user is created
           if (user == null) {
               user = new IntactUser();
               session.setAttribute(Constants.USER, user);
           }
           user.clearAll();
           Constants.LOGGER.info("created user");
           // a list to store the ac numbers to search for
           List searchAc = new ArrayList();
           List notSearchAc = new ArrayList();
           // all given parameters are fetched
           Map parameters = request.getParameterMap();
           Object key;
           String[] values;
           StringTokenizer tok;

           for (Iterator iter = parameters.keySet().iterator(); iter.hasNext();) {
               key = iter.next();
               values = (String[]) parameters.get(key);
               // if a checkbox with a protein was checked the protein ac
               // number is the key and stored in the list
               if ("on".equals(values[0])) {
                   notSearchAc.add(key.toString().trim());
               }
               // the default parameter 'AC' is given
               else if (key.equals(Constants.PARAMETER)) {
                   tok = new StringTokenizer(values[0], ",");
                   while (tok.hasMoreTokens()) {
                       searchAc.add(tok.nextToken().trim());
                   }
               }
           }

           //TODO: FORWARD TO AMBIGUOUS PAGE !!!!!!
           if (searchAc.size() > Constants.MAX_SEARCH_NUMBER) {
               request.setAttribute(Constants.ERROR, new ErrorForm(
                       "The number of selected proteins " + "is greater than "
                               + Constants.MAX_SEARCH_NUMBER
                               + " !<br>Please select less proteins !"));
               return mapping.findForward(Constants.ERROR);
           }

           boolean ambiguous = false;
           // the map stores the ac as key and its search results as value.
           // This is needed to store the information for the ambiguous page.
           //Map map = new HashMap();
           Collection ambiguousResults = new ArrayList();
           
           String ac;
           AmbiguousBean ab;
           Collection toSearchFor = new HashSet();

           // the search helper provides the search for the ac numbers
           SearchHelper sh = new SearchHelper(Constants.LOGGER);
           // for every ac number of the list a search is done
           for (int i = 0, n = searchAc.size(); i < n; i++) {
               ac = searchAc.get(i).toString();
               ab = searchFor(ac, sh, user);

               // if the search returned a ambiguous result
               if (ab.hasAmbiguousResult()) {
                   ambiguous = true;
               }
               else {
                   for (Iterator iter = ab.getProteins().iterator(); iter
                           .hasNext();) {
                       toSearchFor.add(((Interactor) iter.next()).getAc());
                   }
               }
               ab.setSearchAc(ac);
               ambiguousResults.add(ab);
           }

           // if the results are ambiguous the application is forwarded to a
           // special page to display all search results.
           if (ambiguous) {
               for (int i = 0, n = notSearchAc.size(); i < n; i++) {
                   ac = notSearchAc.get(i).toString();
                   ab = searchFor(ac, sh, user);
                   ab.setSearchAc(ac);
                   ambiguousResults.add(ab);
               }
               request.setAttribute(Constants.AMBIGOUS, ambiguousResults);
               return mapping.findForward(Constants.AMBIGOUS);
           }
           else {
               toSearchFor.addAll(notSearchAc);
           }
           user.setSearch(toSearchFor);
           return mapping.findForward(Constants.SUCCESS);
       }
       catch (Exception e) {
           request.setAttribute(Constants.ERROR, new ErrorForm(e
                   .getLocalizedMessage()));
           return mapping.findForward(Constants.ERROR);
       }
   }

   private AmbiguousBean searchFor(String ac, SearchHelperI sh,
           IntactUserI user) throws IntactException {
       AmbiguousBean ab = new AmbiguousBean();
       ab.setProteins(sh.doLookup("Protein", ac, user));
       ab.setInteractions(sh.doLookup("Interaction", ac, user));
       ab.setExperiments(sh.doLookup("Experiment", ac, user));
       return ab;
   }
}