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
package uk.ac.ebi.intact.persistence.dao.query.impl;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.DaoUtils;
import uk.ac.ebi.intact.persistence.dao.query.QueryPhrase;
import uk.ac.ebi.intact.persistence.dao.query.QueryTerm;
import uk.ac.ebi.intact.persistence.dao.query.QueryTermConverter;

/**
 * Standard implementation of the AutoAddWildCardConverter
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public class StandardAutoAddWildcardConverter implements AutoAddWildcardConverter
{

    public SearchableQuery autoAddWildCards(SearchableQuery query)
    {
        query.setAc(addAutomaticWildcardsToPhrase(query.getAc()));
        query.setShortLabel(addAutomaticWildcardsToPhrase(query.getShortLabel()));
        query.setDescription(addAutomaticWildcardsToPhrase(query.getDescription()));
        query.setAnnotationText(addAutomaticWildcardsToPhrase(query.getAnnotationText()));
        query.setFullText(addAutomaticWildcardsToPhrase(query.getFullText()));
        query.setXref(addAutomaticWildcardsToPhrase(query.getXref()));

        return query;
    }

    private QueryPhrase addAutomaticWildcardsToPhrase(QueryPhrase originalPhrase)
    {
        if (originalPhrase == null) return null;
        
        QueryPhrase phrase = new QueryPhrase();
        QueryTermConverter termConverter = new StandardQueryTermConverter();

        for (QueryTerm term : originalPhrase.getTerms())
        {
            String originalValue = termConverter.termToString(term);
            String valWithPercents = addStartAndEndPercentIfNecessary(originalValue);
            phrase.getTerms().add(termConverter.stringToTerm(valWithPercents));
        }

        return phrase;
    }

    /**
     * Feature Request #1485467 : Add a wildcard at the end of the value, when necessary
     * @param value the value to change
     * @return a String with the wildcard added, if necessary
     */
    private static String addEndPercentIfNecessary(String value)
    {
        value = DaoUtils.replaceWildcardsByPercent(value);

        String acPrefix = IntactContext.getCurrentInstance().getConfig().getAcPrefix();

        if (!value.endsWith("%") && !value.toLowerCase().startsWith(acPrefix.toLowerCase())
                && !value.startsWith("\"") && !value.endsWith("\""))
        {
            value = value+"%";
        }

        return value;
    }

     private static String addStartAndEndPercentIfNecessary(String value)
    {
        value = DaoUtils.replaceWildcardsByPercent(value);

        String acPrefix = IntactContext.getCurrentInstance().getConfig().getAcPrefix();

        if (!value.startsWith("%") && !value.toLowerCase().startsWith(acPrefix.toLowerCase())
                && !value.startsWith("\"") && !value.endsWith("\""))
        {
            value = "%"+value+"%";
        }

        if (!value.endsWith("%") && !value.toLowerCase().startsWith(acPrefix.toLowerCase())
                && !value.startsWith("\"") && !value.endsWith("\""))
        {
            value = value+"%";
        }

        return value;
    }
}
