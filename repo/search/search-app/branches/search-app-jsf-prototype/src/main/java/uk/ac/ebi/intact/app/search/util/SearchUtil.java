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
package uk.ac.ebi.intact.app.search.util;

import uk.ac.ebi.intact.persistence.dao.query.QueryPhrase;
import uk.ac.ebi.intact.persistence.dao.query.impl.SearchableQuery;
import uk.ac.ebi.intact.persistence.dao.query.impl.StandardQueryPhraseConverter;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.app.search.data.ResultCount;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SearchUtil
{
    private static final Log log = LogFactory.getLog(SearchUtil.class);

    private SearchUtil()
    {

    }

    public static SearchableQuery createSimpleQuery(String value)
    {
        StandardQueryPhraseConverter phraseConverter = new StandardQueryPhraseConverter();
        QueryPhrase phrase = phraseConverter.objectToPhrase(value);

        SearchableQuery query = new SearchableQuery();
        query.setAc(phrase);
        query.setShortLabel(phrase);
        query.setXref(phrase);
        query.setDescription(phrase);
        query.setDisjunction(true);

        return query;
    }

    public static List<ResultCount> countResults(IntactContext context, SearchableQuery query, Class<? extends Searchable> ... searchables)
    {
        List<ResultCount> results = new ArrayList<ResultCount>();

        Map<Class<? extends Searchable>,Integer> resultMap =
                context.getDataContext().getDaoFactory().getSearchableDao().countByQuery(searchables, query);

        if (log.isDebugEnabled())
            log.debug("Results: "+resultMap.size());

        for (Map.Entry<Class<? extends Searchable>,Integer> entry : resultMap.entrySet())
        {
            int count = entry.getValue();

            if (count > 0)
            {
                results.add(createResultCount(entry.getKey(), count));
            }
        }

        return results;
    }

    private static ResultCount createResultCount(Class<? extends Searchable> searchable, Integer count)
    {
        String label;

        if (searchable == Experiment.class)
        {
            label = "Experiments";
        }
        else if (searchable == InteractionImpl.class)
        {
            label = "Interactions";
        }
        else if (searchable == ProteinImpl.class)
        {
            label = "Proteins";
        }
        else if (searchable == NucleicAcidImpl.class)
        {
            label = "Nucleic Acids";
        }
        else if (searchable == CvObject.class)
        {
            label = "Controlled Vocabulary Objects";
        }
        else
        {
            label = "Unknown";
        }

        return new ResultCount(searchable.getName(), label, count);
    }

    public static String readableQuery(SearchableQuery query)
    {
        String readableQuery = null;

        // simple query?
        if (query.getAc().equals(query.getShortLabel()) &&
                query.getShortLabel().equals(query.getDescription()))
        {
            readableQuery = new StandardQueryPhraseConverter()
                    .phraseToObject(query.getAc()).toString();
        }

        if (readableQuery != null && readableQuery.trim().length() > 0)
        {
            return readableQuery;
        }

        return "Unknown";
    }

    public static int totalForSearchableInCurrentResults(Class<? extends Searchable> searchable, List<ResultCount> results)
    {
        for (ResultCount rc : results)
        {
            if (searchable.getClass().getName().equals(rc.getObjClass()))
            {
                return rc.getCount();
            }
        }

        return 0;
    }
}
