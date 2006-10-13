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
package uk.ac.ebi.intact.application.search3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.application.commons.util.UrlUtil;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.WebappSession;
import uk.ac.ebi.intact.persistence.dao.query.SearchableQuery;

import java.io.Serializable;
import java.util.Map;

/**
 * Convenience class to access all stored variables in a clear way
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12-Oct-2006</pre>
 */
public class SearchWebappContext implements Serializable
{
    private static final Log log = LogFactory.getLog(SearchWebappContext.class);

    private static final String SEARCH_ATT_NAME = "uk.ac.ebi.intact.search.internal.SEARCH_WEBAPP_CONTEXT";

    private static final String SEARCHABLE_QUERY_ATT_NAME = "uk.ac.ebi.intact.search.internal.SEARCHABLE_QUERY";
    private static final String RESULTS_INFO_ATT_NAME = "uk.ac.ebi.intact.search.internal.RESULTS_INFO";
    private static final String TOTAL_RESULTS_ATT_NAME = "uk.ac.ebi.intact.search.internal.TOTAL_RESULTS";
    private static final String CURRENT_PAGE = "uk.ac.ebi.intact.search.internal.CURRENT_PAGE";
    private static final String IS_PAGINATED_SEARCH_ATT_NAME = "uk.ac.ebi.intact.search.internal.PAGINATED_SEARCH";
    private static final String RESULTS_PER_PAGE = "uk.ac.ebi.intact.search.internal.RESULTS_PER_PAGE";

    private IntactSession session;

    private String helpLink;

    private SearchWebappContext()
    {
    }

    public static SearchWebappContext getCurrentInstance()
    {
        return getCurrentInstance(IntactContext.getCurrentInstance());
    }

    public static SearchWebappContext getCurrentInstance(IntactContext context)
    {
        SearchWebappContext swc = (SearchWebappContext) context.getSession().getAttribute(SEARCH_ATT_NAME);

        if (swc == null)
        {
            swc = new SearchWebappContext();
            context.getSession().setAttribute(SEARCH_ATT_NAME, swc);
        }

        swc.setSession(context.getSession());

        return swc;
    }

    public SearchableQuery getSearchablequery()
    {
        return (SearchableQuery) session.getRequestAttribute(SEARCHABLE_QUERY_ATT_NAME);
    }

    public void setSearchableQuery(SearchableQuery query)
    {
        session.setRequestAttribute(SEARCHABLE_QUERY_ATT_NAME, query);

        //TODO will be eliminated eventually
        session.setAttribute(SearchConstants.SEARCH_CRITERIA, query);
    }

    public Map<?,Integer> getResultsInfo()
    {
        return (Map) session.getAttribute(RESULTS_INFO_ATT_NAME);
    }

    public void setResultsInfo(Map<?,Integer> resultsInfo)
    {
        session.setRequestAttribute(RESULTS_INFO_ATT_NAME, resultsInfo);
    }

    public Integer getTotalResults()
    {
        return (Integer) session.getRequestAttribute(TOTAL_RESULTS_ATT_NAME);
    }

    public void setTotalResults(Integer totalResults)
    {
        session.setRequestAttribute(TOTAL_RESULTS_ATT_NAME, totalResults);
    }

    public Integer getResultsPerPage()
    {
        Integer resPage = (Integer) session.getRequestAttribute(RESULTS_PER_PAGE);

        if (resPage == null)
        {
            resPage = getMaxResultsPerPage();
        }

        return resPage;
    }

    public void setResultsPerPage(Integer resultsPerPage)
    {
        session.setRequestAttribute(RESULTS_PER_PAGE, resultsPerPage);
    }

    public Integer getCurrentPage()
    {
        Integer page = (Integer) session.getRequestAttribute(CURRENT_PAGE);

        if (page == null)
        {
            log.debug("Current page is null. Setting to 0");
            page = Integer.valueOf(0);
        }

        return 0;
    }

    public void setCurrentPage(Integer currentPage)
    {
        log.debug("Current page set to: "+currentPage);
        session.setRequestAttribute(CURRENT_PAGE, currentPage);
    }

    public Boolean isPaginatedSearch()
    {
        return (Boolean) session.getRequestAttribute(IS_PAGINATED_SEARCH_ATT_NAME);
    }

    public void setPaginatedSearch(Boolean paginatedSearch)
    {
        session.setRequestAttribute(IS_PAGINATED_SEARCH_ATT_NAME, paginatedSearch);
    }
    
    public String getHelpLink()
    {
        if (helpLink != null)
        {
            return helpLink;
        }

        String relativeHelpLink = session.getInitParam(SearchEnvironment.HELP_LINK);

        //build the help link out of the context path - strip off the 'search' bit...
        String absPathWithoutContext = UrlUtil.absolutePathWithoutContext(((WebappSession)session).getRequest());

        return absPathWithoutContext.concat(relativeHelpLink);
    }

    public Integer getMaxResultsPerPage()
    {
        String strMax = IntactContext.getCurrentInstance()
                .getSession().getInitParam(SearchEnvironment.MAX_RESULTS_PER_PAGE);

        if (strMax != null)
        {
            return Integer.valueOf(strMax);
        }

        return null;
    }


    public void setSession(IntactSession session)
    {
        this.session = session;
    }
}
