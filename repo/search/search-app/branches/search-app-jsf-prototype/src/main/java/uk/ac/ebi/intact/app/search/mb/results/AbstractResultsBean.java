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
package uk.ac.ebi.intact.app.search.mb.results;

import uk.ac.ebi.intact.app.search.mb.SearchBean;
import uk.ac.ebi.intact.app.search.data.ResultCount;
import uk.ac.ebi.intact.app.search.data.SearchableDataModel;
import uk.ac.ebi.intact.app.search.util.SearchUtil;
import uk.ac.ebi.intact.model.Searchable;
import uk.ac.ebi.intact.model.Experiment;

import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractResultsBean implements Serializable
{
    private SearchBean searchBean;
    private SearchableDataModel dataModel;

    public AbstractResultsBean()
    {
        FacesContext context = FacesContext.getCurrentInstance();

        searchBean = (SearchBean) context.getApplication().getVariableResolver().resolveVariable(context, "searchBean"); 
        this.dataModel = createDataModel();
    }

    public SearchableDataModel getDataModel()
    {
        return dataModel;
    }

    public void setDataModel(SearchableDataModel dataModel)
    {
        this.dataModel = dataModel;
    }

    public SearchBean getSearchBean()
    {
        return searchBean;
    }

    public void setSearchBean(SearchBean searchBean)
    {
        this.searchBean = searchBean;
    }

    protected abstract Class<? extends Searchable> getSearchable();

    private SearchableDataModel createDataModel()
    {
        int totalCount = SearchUtil.totalForSearchableInCurrentResults(getSearchable(), getSearchBean().getCurrentResults());

        return new SearchableDataModel(getSearchBean().getSearchableQuery(), getSearchable(), totalCount, 50);
    }
}
