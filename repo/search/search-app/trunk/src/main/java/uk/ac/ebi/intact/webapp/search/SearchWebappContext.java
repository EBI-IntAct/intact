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
package uk.ac.ebi.intact.webapp.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.WebappSession;
import uk.ac.ebi.intact.persistence.dao.query.SearchableQuery;
import uk.ac.ebi.intact.webapp.search.struts.util.SearchConstants;
import uk.ac.ebi.intact.model.Searchable;

import java.util.Map;

/**
 * Convenience class to access all stored variables in a clear way
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:SearchWebappContext.java 6452 2006-10-16 17:09:42 +0100 (Mon, 16 Oct 2006) baranda $
 * @since 1.5
 */
public class SearchWebappContext
{
    private static final Log log = LogFactory.getLog(SearchWebappContext.class);

    private static final String SEARCHABLE_QUERY_ATT_NAME = "uk.ac.ebi.intact.search.internal.SEARCHABLE_QUERY";
    private static final String RESULTS_INFO_ATT_NAME = "uk.ac.ebi.intact.search.internal.RESULTS_INFO";
    private static final String TOTAL_RESULTS_ATT_NAME = "uk.ac.ebi.intact.search.internal.TOTAL_RESULTS";
    private static final String CURRENT_PAGE_ATT_NAME = "uk.ac.ebi.intact.search.internal.CURRENT_PAGE_ATT_NAME";
    private static final String IS_PAGINATED_SEARCH_ATT_NAME = "uk.ac.ebi.intact.search.internal.PAGINATED_SEARCH";
    private static final String RESULTS_PER_PAGE = "uk.ac.ebi.intact.search.internal.RESULTS_PER_PAGE";

    private IntactSession session;

    private String helpLink;
    private String searchUrl;
    private String hierarchViewUrl;
    private String mineUrl;

    private Map<Class<? extends Searchable>, Integer> currentResultCount;

    private SearchWebappContext()
    {
    }

    public static SearchWebappContext getCurrentInstance()
    {
        return getCurrentInstance(IntactContext.getCurrentInstance());
    }

    public static SearchWebappContext getCurrentInstance(IntactContext context)
    {
        SearchWebappContext swc = currentInstance.get();

        if (swc == null)
        {
            swc = new SearchWebappContext();
            currentInstance.set(swc);
        }

        swc.setSession(context.getSession());

        return swc;
    }

    private static ThreadLocal<SearchWebappContext> currentInstance = new ThreadLocal<SearchWebappContext>()
    {
        protected SearchWebappContext initialValue()
        {
            return null;
        }
    };

    public SearchableQuery getCurrentSearch()
    {
        return (SearchableQuery) session.getRequestAttribute(SEARCHABLE_QUERY_ATT_NAME);
    }

    public void setSearchableQuery(SearchableQuery query)
    {
        session.setRequestAttribute(SEARCHABLE_QUERY_ATT_NAME, query);

        //TODO will be eliminated eventually
        session.setAttribute(SearchConstants.SEARCH_CRITERIA, query);
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
        Integer page = (Integer) session.getRequestAttribute(CURRENT_PAGE_ATT_NAME);

        if (page == null)
        {
            log.debug("Current page is null. Setting to 0");
            page = 0;
        }

        return page;
    }

    public void setCurrentPage(Integer currentPage)
    {
        log.debug("Current page set to: "+currentPage);
        session.setRequestAttribute(CURRENT_PAGE_ATT_NAME, currentPage);
    }

    public Boolean isPaginatedSearch()
    {
        Boolean isPaginated = (Boolean) session.getRequestAttribute(IS_PAGINATED_SEARCH_ATT_NAME);

        if (isPaginated == null)
        {
            isPaginated = false;
        }

        return isPaginated;
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
        String absPathWithoutContext = absolutePathWithoutContext(((WebappSession)session));

        helpLink = absPathWithoutContext.concat(relativeHelpLink);

        return helpLink;
    }

    public String getSearchUrl()
    {
        if (searchUrl != null)
        {
            return searchUrl;
        }

        String appPath = session.getInitParam(SearchEnvironment.SEARCH_LINK);

        searchUrl = ((WebappSession)session).getRequest().getContextPath().concat(appPath);

        return searchUrl;
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

    public String getAbsolutePathWithoutContext()
    {
        return absolutePathWithoutContext((WebappSession)session);
    }

    public String getHierarchViewAbsoluteUrl()
    {
        if (hierarchViewUrl != null)
        {
            return hierarchViewUrl;
        }

        hierarchViewUrl = getAbsolutePathWithoutContext().concat(
            session.getInitParam(SearchEnvironment.HIERARCH_VIEW_URL));

        return hierarchViewUrl;
    }

    public String getHierarchViewMethod()
    {
        return session.getInitParam(SearchEnvironment.HIERARCH_VIEW_METHOD);
    }

    public String getHierarchViewDepth()
    {
        return session.getInitParam(SearchEnvironment.HIERARCH_VIEW_DEPTH);
    }

    public String getMineAbsoluteUrl()
    {
        if (mineUrl != null)
        {
            return mineUrl;
        }

        mineUrl = getAbsolutePathWithoutContext().concat(
                session.getInitParam(SearchEnvironment.MINE_URL));

        return mineUrl;
    }


    public Map<Class<? extends Searchable>, Integer> getCurrentResultCount()
    {
        return currentResultCount;
    }

    public void setCurrentResultCount(Map<Class<? extends Searchable>, Integer> currentResultCount)
    {
        this.currentResultCount = currentResultCount;
    }

    public Integer getResultCountFor(Class<? extends Searchable> searchable)
    {
        if (currentResultCount == null)
        {
            return 0;
        }

        return currentResultCount.get(searchable);
    }

    /**
     * Gets the absolute path stripping off the context name from the URL
     * @return The absolute path without the context part
     */
    private static String absolutePathWithoutContext(WebappSession session)
    {
        String ctxtPath = session.getRequest().getContextPath();
        String absolutePathWithoutContext = ctxtPath.substring(0, ctxtPath.lastIndexOf( "/" )+1);

        return absolutePathWithoutContext;
    }
}
