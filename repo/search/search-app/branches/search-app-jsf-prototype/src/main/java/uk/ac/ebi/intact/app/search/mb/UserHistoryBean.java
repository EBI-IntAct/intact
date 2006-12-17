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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UserHistoryBean implements Serializable
{
    private static final int MY_SEARCHES_MAX = 10;

    private LinkedList<SearchableQuery> allMySearches;
    private LinkedList<SearchableQuery> myLastSearches;

    public UserHistoryBean()
    {
        allMySearches = new LinkedList<SearchableQuery>();
        myLastSearches = new LinkedList<SearchableQuery>();
    }

    public void addSearch(SearchableQuery searchableQuery)
    {
        allMySearches.addFirst(searchableQuery);
        myLastSearches.addFirst(searchableQuery);

        if (myLastSearches.size() > MY_SEARCHES_MAX)
        {
            myLastSearches.removeLast();
        }
    }

    public List<SearchableQuery> getAllMySearches()
    {
        return allMySearches;
    }

    public void setAllMySearches(LinkedList<SearchableQuery> allMySearches)
    {
        this.allMySearches = allMySearches;
    }

    public List<SearchableQuery> getMyLastSearches()
    {
        return myLastSearches;
    }

    public void setMyLastSearches(LinkedList<SearchableQuery> myLastSearches)
    {
        this.myLastSearches = myLastSearches;
    }
}
