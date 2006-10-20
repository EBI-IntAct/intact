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
package uk.ac.ebi.intact.webapp.search.struts.controller;

import org.apache.struts.action.DynaActionForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.query.SearchableQuery;
import uk.ac.ebi.intact.searchengine.SearchClass;

import java.util.Arrays;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

/**
 * Execute simple searches
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:NewSearchAction.java 6452 2006-10-16 17:09:42 +0100 (Mon, 16 Oct 2006) baranda $
 */
public class NewSearchAction extends SearchActionBase
{

    private static final Log log = LogFactory.getLog(NewSearchAction.class);

    public SearchableQuery createSearchableQuery()
    {
        String searchValue;

        searchValue = getParameterFromUrl("searchString");

        if (searchValue == null)
        {
            DynaActionForm dyForm = (DynaActionForm) getForm();
            searchValue = (String) dyForm.get( "searchString" );

            if (log.isDebugEnabled()) log.debug("Getting 'searchString' from form: "+searchValue);
        }
        else
        {
            if (log.isDebugEnabled()) log.debug("Getting 'searchString' from parameter: "+searchValue);
        }

        SearchableQuery query;

        if (SearchableQuery.isSearchableQuery(searchValue))
        {
            query = SearchableQuery.parseSearchableQuery(searchValue);
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

    private String getParameterFromUrl(String paramName)
    {
        String url = getRequest().getQueryString();

        String[] params = url.split("&");

        for (String param : params)
        {
            String[] nameAndValue = param.split("=", 2);

            if (nameAndValue.length < 2)
            {
                return null;
            }

            String name = nameAndValue[0];
            String value = nameAndValue[1];

            if (name.equals(paramName))
            {
                try
                {
                    return URLDecoder.decode(value, "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
