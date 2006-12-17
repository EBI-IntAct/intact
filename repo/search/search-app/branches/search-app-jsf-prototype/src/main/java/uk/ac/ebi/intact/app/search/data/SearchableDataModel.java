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
package uk.ac.ebi.intact.app.search.data;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Searchable;
import uk.ac.ebi.intact.persistence.dao.query.impl.SearchableQuery;
import uk.ac.ebi.intact.persistence.dao.SearchableDao;
import uk.ac.ebi.intact.context.IntactContext;

import javax.faces.model.DataModel;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SearchableDataModel<T extends Searchable> extends DataModel
{
    private SearchableQuery query;
    private Class<? extends Searchable> searchable;
    private int count;
    private int pageSize;

    private List<T> dataResults;
    private int rowIndex = 0;

    public SearchableDataModel(SearchableQuery query, Class<? extends Searchable> searchable, int pageSize)
    {
        this(query, searchable,
                IntactContext.getCurrentInstance().getDataContext()
                .getDaoFactory().getSearchableDao().countByQuery(searchable, query),
                pageSize);
    }

    public SearchableDataModel(SearchableQuery query, Class<? extends Searchable> searchable, int totalCount, int pageSize)
    {
        this.query = query;
        this.searchable = searchable;
        this.pageSize = pageSize;
        this.count = totalCount;
    }

    private void loadDataResults()
    {
        this.dataResults = (List<T>) IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getSearchableDao().getByQuery(searchable, query, rowIndex, pageSize);
    }

    public int getRowCount()
    {
        return count;
    }

    public Object getRowData()
    {
        if (dataResults == null)
        {
            return null;
        }
        if (!isRowAvailable())
        {
            throw new IllegalArgumentException("row is unavailable");
        }
        return dataResults.get(relativeIndex());
    }

    public int getRowIndex()
    {
        return relativeIndex();
    }

    public Object getWrappedData()
    {
        return dataResults;
    }

    public boolean isRowAvailable()
    {
        if (dataResults == null)
        {
            return false;
        }

        return rowIndex >= 0 && rowIndex < count;
    }

    public void setRowIndex(int rowIndex)
    {
        this.rowIndex = rowIndex;

        loadDataResults();
    }

    public void setWrappedData(Object data)
    {
        dataResults = (List<T>)data;
        int rowIndex = dataResults != null ? 0 : -1;
        setRowIndex(rowIndex);
    }

    private int relativeIndex()
    {
        return rowIndex % pageSize;
    }
}
