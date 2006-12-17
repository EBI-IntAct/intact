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
package uk.ac.ebi.intact.app.search.mb;

import uk.ac.ebi.intact.persistence.dao.query.impl.SearchableQuery;
import uk.ac.ebi.intact.persistence.dao.SearchableDao;
import uk.ac.ebi.intact.app.search.util.SearchUtil;
import uk.ac.ebi.intact.app.search.data.ResultCount;
import uk.ac.ebi.intact.context.IntactContext;

import javax.faces.event.ActionEvent;
import javax.faces.context.FacesContext;
import javax.faces.component.UIParameter;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SearchBean implements Serializable
{
    private static final Log log = LogFactory.getLog(SearchBean.class);

    private String query;
    private SearchableQuery searchableQuery;

    private List<ResultCount> currentResults;

    private UserHistoryBean userHistory;
    private SearchCacheBean searchCache;

    public SearchBean(){
    }

    public void doSimpleSearch(ActionEvent evt)
    {
        this.searchableQuery = SearchUtil.createSimpleQuery(query);

        if (log.isDebugEnabled())
            log.debug("Simple search: "+searchableQuery);

        doSearch();

        userHistory.addSearch(searchableQuery);
    }

    public void executePreviousSearch(ActionEvent evt)
    {
        this.query = (String) ((UIParameter)evt.getComponent().getChildren().iterator().next()).getValue();

        this.searchableQuery = SearchUtil.createSimpleQuery(query);

        if (log.isDebugEnabled())
            log.debug("Previous search: "+searchableQuery);

        doSearch();
    }

    private void doSearch()
    {
         if (searchCache.getSearchResults().containsKey(searchableQuery))
        {
            currentResults = searchCache.getSearchResults().get(searchableQuery);
        }
        else
        {
            currentResults = SearchUtil.countResults(IntactContext.getCurrentInstance(), searchableQuery, SearchableDao.STANDARD_SEARCHABLES);
            searchCache.getSearchResults().put(searchableQuery, currentResults);
        }
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public SearchableQuery getSearchableQuery()
    {
        return searchableQuery;
    }

    public void setSearchableQuery(SearchableQuery searchableQuery)
    {
        this.searchableQuery = searchableQuery;
    }

    public List<ResultCount> getCurrentResults()
    {
        return currentResults;
    }

    public void setCurrentResults(List<ResultCount> currentResults)
    {
        this.currentResults = currentResults;
    }

    public UserHistoryBean getUserHistory()
    {
        return userHistory;
    }

    public void setUserHistory(UserHistoryBean userHistory)
    {
        this.userHistory = userHistory;
    }

    public SearchCacheBean getSearchCache()
    {
        return searchCache;
    }

    public void setSearchCache(SearchCacheBean searchCache)
    {
        this.searchCache = searchCache;
    }
}
