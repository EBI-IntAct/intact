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
package uk.ac.ebi.intact.persistence.svc.impl;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Searchable;
import uk.ac.ebi.intact.persistence.dao.DaoUtils;
import uk.ac.ebi.intact.persistence.dao.SearchableDao;
import uk.ac.ebi.intact.persistence.dao.query.QueryPhrase;
import uk.ac.ebi.intact.persistence.dao.query.impl.SearchableQuery;
import uk.ac.ebi.intact.persistence.dao.query.impl.StandardQueryPhraseConverter;
import uk.ac.ebi.intact.persistence.svc.SearchService;

import java.util.List;

/**
 * Search behaviour: first search using the exact query provided. If no results are returned
 * encapsulate each of the query terms with wildcards so potentially more results can be returned.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public class SimpleSearchService implements SearchService
{
    public SimpleSearchService()
    {
    }

    public int count(Class<? extends Searchable> searchable, String query)
    {
        return count(searchable, createSimpleQuery(query));
    }

    public int count(Class<? extends Searchable> searchable, SearchableQuery query)
    {
        int count = getDao().countByQuery(searchable, query);

        if (count > 0)
        {
            return count;
        }

        return getDao().countByQuery(searchable, createSimpleQueryWithWildcards(query));
    }

    public <S extends Searchable> List<S> search(Class<S> searchable, String query, Integer firstResult, Integer maxResults)
    {
        return search(searchable, createSimpleQuery(query), firstResult, maxResults);
    }

    public <S extends Searchable> List<S> search(Class<S> searchable, SearchableQuery query, Integer firstResult, Integer maxResults)
    {
        if (firstResult == null) firstResult = 0;
        if (maxResults == null) maxResults = Integer.MAX_VALUE;

        List<S> results = getDao().getByQuery(searchable, query, firstResult, maxResults);

        if (results.size() > 0)
        {
            return results;
        }

        return getDao().getByQuery(searchable, createSimpleQueryWithWildcards(query), firstResult, maxResults);
    }

    private SearchableDao getDao()
    {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getSearchableDao();
    }

    private SearchableQuery createSimpleQuery(String query)
    {
        QueryPhrase phrase = new StandardQueryPhraseConverter().objectToPhrase(query);

        SearchableQuery sq = new SearchableQuery();
        sq.setAc(phrase);
        sq.setShortLabel(phrase);
        sq.setFullText(phrase);
        sq.setAnnotationText(phrase);
        sq.setXref(phrase);
        sq.setDisjunction(true);

        return sq;
    }

    private SearchableQuery createSimpleQueryWithWildcards(SearchableQuery query)
    {
        return DaoUtils.autoAddWildcards(query);
    }
}
