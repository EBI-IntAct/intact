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

import org.apache.struts.action.DynaActionForm;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.query.SearchableQuery;
import uk.ac.ebi.intact.searchengine.SearchClass;

/**
 * Execute simple searches
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class NewSearchAction extends SearchActionBase
{

    public SearchableQuery createSearchableQuery()
    {
        DynaActionForm dyForm = (DynaActionForm) getForm();

        String searchValue = (String) dyForm.get( "searchString" );

        SearchableQuery query = null;

        if (SearchableQuery.isSearchableQuery(searchValue))
        {
            query = SearchableQuery.paseSearchableQuery(searchValue);
        }
        else
        {
            query = new SearchableQuery();
            query.setDisjunction(true);
            query.setAc(searchValue);
            query.setShortLabel(searchValue);
            query.setDescription(searchValue);
            query.setXref(searchValue);
        }

        return query;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Searchable>[] getSearchableTypes()
    {
        DynaActionForm dyForm = (DynaActionForm) getForm();

        String searchClassName = (String)dyForm.get("searchClass");

        if (searchClassName == null)
        {
            return defaultSearchableTypes();
        }

        SearchClass searchClass = SearchClass.valueOfShortName(searchClassName);

        if (searchClass == SearchClass.NOSPECIFIED)
        {
            return defaultSearchableTypes();
        }

        return new Class[] { searchClass.getMappedClass() };
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends Searchable>[] defaultSearchableTypes()
    {
        return new Class[] {Experiment.class,
                            InteractionImpl.class,
                            ProteinImpl.class,
                            NucleicAcidImpl.class,
                            CvObject.class };
    }
}
