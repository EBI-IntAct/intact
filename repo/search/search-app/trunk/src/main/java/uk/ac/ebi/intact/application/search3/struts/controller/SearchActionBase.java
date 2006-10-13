/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.application.search3.struts.controller;

import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.SearchEnvironment;
import uk.ac.ebi.intact.application.search3.SearchWebappContext;
import uk.ac.ebi.intact.application.commons.util.UrlUtil;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.query.SearchableQuery;
import uk.ac.ebi.intact.persistence.dao.SearchableDao;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.model.Searchable;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.AnnotatedObjectImpl;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.searchengine.SearchClass;
import uk.ac.ebi.intact.util.DebugUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

/**
 * Abstraction of the IntactBaseAction. Gives access to the IntActUser.
 *
 * @author Michael Kleen
 * @version IntactSearchAction.java Date: Feb 17, 2005 Time: 4:13:44 PM
 */
public abstract class SearchActionBase extends IntactSearchAction
{

    private static final Log log = LogFactory.getLog(SearchActionBase.class);

    private boolean paginatedSearch;

    private IntactContext intactContext;
    private ActionMapping mapping;
    private ActionForm form;

    private HttpServletRequest request;
    private HttpServletResponse response;

    private int page;

     @Override
    public ActionForward execute( ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response )
            throws IOException, ServletException
    {
        this.intactContext = IntactContext.getCurrentInstance();
        this.mapping = mapping;
        this.form = form;
        this.request = request;
        this.response = response;

        SearchWebappContext webappContext = SearchWebappContext.getCurrentInstance(intactContext);
        /*
        try
        {
            return executeSearch(mapping, form, request, response);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new ServletException(e);
        } */

        log.info( "in SearchActionBase");
        // Clear any previous errors.
        super.clearErrors();

        //clear the error message
        getIntactContext().getSession().setAttribute(SearchConstants.ERROR_MESSAGE, "");

        request.setAttribute("search", form);

        // page
        String strPage = request.getParameter("page");
        this.page = 0;

        if (strPage != null && strPage.length() != 0)
        {
            webappContext.setPaginatedSearch(true);
            page = Integer.valueOf(strPage);
        }

        if (log.isDebugEnabled() && isPaginatedSearch())
        {
            log.debug("Performing paginated search. Page: "+page);
        }

        // starting query
        SearchWebappContext.getCurrentInstance(getIntactContext())
                .setSearchableQuery(getSearchableQuery());

        ActionForward af = checkNumberOfResults();

        if (af != null)
        {
            return af;
        }

        return searchUsingQuery();

    }

    public abstract ActionForward executeSearch( ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response )
            throws SearchException;

    public abstract SearchableQuery createSearchableQuery();

    public abstract Class<? extends Searchable>[] getSearchableTypes();

    /**
     * Search using a <code>SearchableQuery</code> and not the lucene index
     */
    public ActionForward checkNumberOfResults()
    {
        log.debug("Checking number of results");

        int maxResults = SearchWebappContext.getCurrentInstance().getResultsPerPage();
        
        SearchableDao dao = getIntactContext().getDataContext()
                .getDaoFactory().getSearchableDao();

        // count the results
        Map<Class<? extends Searchable>, Integer> resultInfo;

        if (getSearchableTypes() != null)
        {
            resultInfo = dao.countByQuery(getSearchableTypes(), getSearchableQuery());
        }
        else
        {
            resultInfo = dao.countByQuery(getSearchableQuery());
        }

        int size = 0;

        for (int num : resultInfo.values())
        {
            size += num;
        }

        log.debug("Results found: " + size);

        SearchWebappContext.getCurrentInstance(getIntactContext()).setResultsInfo(resultInfo);
        SearchWebappContext.getCurrentInstance(getIntactContext()).setTotalResults(size);

        if (resultInfo.size() == 1)
        {
            SearchWebappContext.getCurrentInstance(intactContext).setPaginatedSearch(true);
        }

        if (size > maxResults && !isPaginatedSearch())
        {

            log.debug("Results set is too Large for the specified search criteria");
            //request.setAttribute( SearchConstants.SEARCH_CRITERIA, "'" + searchValue + "'" );
            getIntactContext().getSession().setRequestAttribute(SearchConstants.RESULT_INFO, resultInfo);
            return mapping.findForward(SearchConstants.FORWARD_TOO_LARGE);

        }
        if (size == 0)
        {
            //finished all current options, and still nothing - return a failure
            log.debug("No matches were found for the specified search criteria");
            return mapping.findForward(SearchConstants.FORWARD_NO_MATCHES);
        }

        return null;
    }

    /**
     * Search using a <code>SearchableQuery</code> and not the lucene index
     */
    public ActionForward searchUsingQuery()
    {
        log.debug("Search using query: "+getSearchableQuery());

        Integer firstResult = getFirstResult();
        Integer maxResults = SearchWebappContext.getCurrentInstance().getResultsPerPage();

        log.debug("Paginated query: firstResult="+firstResult+" maxResults="+maxResults);
        
        SearchableDao dao = IntactContext.getCurrentInstance().getDataContext()
                .getDaoFactory().getSearchableDao();

        List<? extends Searchable> results;

        if (getSearchableTypes() != null)
        {
            results = dao.getByQuery(getSearchableTypes(), getSearchableQuery(), firstResult, maxResults);
        }
        else
        {
            results = dao.getByQuery(getSearchableQuery(), firstResult, maxResults);
        }

        if (results.isEmpty())
        {
            //finished all current options, and still nothing - return a failure
            log.debug("No matches were found for the specified search criteria");
            return mapping.findForward(SearchConstants.FORWARD_NO_MATCHES);
        }

        // ************* Search was a success. ********************************

        log.debug("found results - forwarding to relevant Action for processing...");

        //determine the shortlabel highlighting list for display...
        log.debug("building highlight list...");

        List<String> labelList = new ArrayList<String>();
        List<AnnotatedObject> aoResults = new ArrayList<AnnotatedObject>(results.size());

        for (Searchable searchable : results)
        {
            AnnotatedObject ao = (AnnotatedObject)searchable;
            aoResults.add(ao);
            labelList.add(ao.getShortLabel());
        }

        //put both the results and also a list of the shortlabels for highlighting into the request

        getIntactContext().getSession().setRequestAttribute(SearchConstants.SEARCH_RESULTS, results);
        getIntactContext().getSession().setRequestAttribute(SearchConstants.HIGHLIGHT_LABELS_LIST, labelList);

        //set the original search criteria into the session for use by
        //the view action - needed because if the 'back' button is used from
        //single object views, the original search beans are lost

        getIntactContext().getSession().setAttribute(SearchConstants.LAST_VALID_SEARCH, getSearchableQuery());

        log.warn("HELP LINK COMMENTED");
        //String relativeHelpLink = getIntactContext().getSession()
        //        .getInitParam(SearchEnvironment.HELP_LINK);

        //build the help link out of the context path - strip off the 'search' bit...
        //String absPathWithoutContext = UrlUtil.absolutePathWithoutContext(getRequest());
        //String helpLink = absPathWithoutContext.concat(relativeHelpLink);
        //getIntactUser().setHelpLink(helpLink);

        return mapping.findForward(SearchConstants.FORWARD_DISPATCHER_ACTION);
    }


    public boolean isPaginatedSearch()
    {
        return SearchWebappContext.getCurrentInstance(getIntactContext()).getCurrentPage() != null;
    }

    public IntactContext getIntactContext()
    {
        return intactContext;
    }

    public HttpServletRequest getRequest()
    {
        return request;
    }

    public HttpServletResponse getResponse()
    {
        return response;
    }

    public ActionForm getForm()
    {
        return form;
    }

    public ActionMapping getMapping()
    {
        return mapping;
    }

    protected Integer getFirstResult()
    {
        Integer firstResult = 0;

        if (SearchWebappContext.getCurrentInstance().isPaginatedSearch())
        {
            Integer resultsPerPage = SearchWebappContext.getCurrentInstance().getResultsPerPage();
            Integer currentPage = SearchWebappContext.getCurrentInstance().getCurrentPage();

            if (currentPage == null)
            {
                throw new IntactException("Current page is null, but search is set to be paginated");
            }

            firstResult = (currentPage-1)* resultsPerPage;
        }

        return firstResult;
    }


    public SearchableQuery getSearchableQuery()
    {
        SearchableQuery searchableQuery = (SearchableQuery) getIntactContext().getSession().getRequestAttribute("uk.ac.ebi.intact.search.internal.SEARCHABLE_QUERY");
        if (searchableQuery == null)
        {
            log.debug("Creating new searchable query");
            searchableQuery = createSearchableQuery();

            getIntactContext().getSession().setRequestAttribute("uk.ac.ebi.intact.search.internal.SEARCHABLE_QUERY", searchableQuery);
        }
        return searchableQuery;
    }

    protected String[] commaSeparatedListToArray(String commaSeparatedValue)
    {
        String[] arr = commaSeparatedValue.split(",");

        for (int i=0; i<arr.length; i++)
        {
            arr[i] = arr[i].trim();
        }

        return arr;
    }
}
