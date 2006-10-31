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
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.DaoUtils;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class StandardSearchValueConverter implements SearchValueConverter
{

    private static final String REPLACED_SPACE = "&nbsp;";
    private static final String REPLACED_COMMA = "&comma;";
    private static final String MODIFIER_PLUS = "+";
    private static final String MODIFIER_MINUS = "-";

    public Criterion valueToCriterion(String propertyName, String value)
    {
        if (value == null)
        {
            throw new NullPointerException("Value cannot be null");
        }

        Conjunction conjunction = Restrictions.conjunction();
        Disjunction disjunction = Restrictions.disjunction();

        // if the value contains quotes, transform any space or comma inside the quotes
        // so they won't be used to separate the different tokens for the value
        if (value.contains("\""))
        {
             value = replaceSymbolsInPhrases(value);
        }

        value = value.trim();

        // split the value in tokens by space or comma 
        String[] tokens = value.split("\\s|,");

        for (String token : tokens)
        {
            token = token.trim();
            token = replacedToValue(token);

            token = addEndPercentIfNecessary(token);
            token = removeQuotesIfNecessary(token);

            if (isTermPrefixedByModifier(token, MODIFIER_PLUS))
            {
                token = removeModifierFromTerm(token, MODIFIER_PLUS);

                if (DaoUtils.isValueForLike(value))
                {
                    conjunction.add(Restrictions.like(propertyName, token).ignoreCase());
                }
                else
                {
                    conjunction.add(Restrictions.eq(propertyName, token).ignoreCase());
                }
            }
            else if (isTermPrefixedByModifier(token, MODIFIER_MINUS))
            {
                token = removeModifierFromTerm(token, MODIFIER_MINUS);

                if (DaoUtils.isValueForLike(value))
                {
                    conjunction.add(Restrictions.not(Restrictions.like(propertyName, token).ignoreCase()));
                }
                else
                {
                    conjunction.add(Restrictions.ne(propertyName, token).ignoreCase());
                }
            }
            else
            {
                if (DaoUtils.isValueForLike(value))
                {
                    disjunction.add(Restrictions.like(propertyName, token).ignoreCase());
                }
                else
                {
                    disjunction.add(Restrictions.eq(propertyName, token).ignoreCase());
                }
            }
        }

        conjunction.add(disjunction);

        return conjunction;
    }

    private static String replaceSymbolsInPhrases(String value)
    {
        boolean isInsidePhrase = false;

        StringBuffer replacedValue = new StringBuffer();
        StringBuffer currentPhrase = new StringBuffer();

        char[] valueChars = value.toCharArray();

        for (int i=0; i<valueChars.length; i++)
        {
            char c = valueChars[i];

            if (c == '"')
            {
                if (isInsidePhrase)
                {
                    isInsidePhrase = false;

                    String replacedPhrase = valueToReplaced(currentPhrase.toString());
                    replacedValue.append(replacedPhrase);
                }
                else
                {
                    isInsidePhrase = true;
                }
            }

            if (isInsidePhrase)
            {
                currentPhrase.append(c);
            }
            else
            {
                replacedValue.append(c);
            }
        }

        return replacedValue.toString();
    }

    private static String valueToReplaced(String value)
    {
        String replaced = value.replaceAll("\\s", REPLACED_SPACE);
        replaced = replaced.replaceAll(",", REPLACED_COMMA);

        return replaced;
    }

    private static String replacedToValue(String replaced)
    {
        String value = replaced.replaceAll(REPLACED_SPACE, " ");
        value = value.replaceAll(REPLACED_COMMA, ",");

        return value;
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

    private static String removeQuotesIfNecessary(String value)
    {
        if (!value.contains("\""))
        {
            return value;
        }

        boolean initialPercent = value.startsWith("%");
        boolean endPercent = value.endsWith("%");

        if (initialPercent)
        {
            value = value.substring(1);
        }

        if (endPercent)
        {
            value = value.substring(0, value.length()-1);
        }

        if (value.startsWith("\""))
        {
            value = value.substring(1);
        }

        if (value.endsWith("\""))
        {
            value = value.substring(0, value.length()-1);
        }

        if (initialPercent) value = "%"+value;
        if (endPercent) value = value+"%";

        return value;
    }

    /**
     * check if a term has a modifier
     */
    private static boolean isTermPrefixedByModifier(String term, String modifier)
    {
        return (term.startsWith(modifier)
                || term.startsWith("%"+modifier)
                || term.startsWith(modifier+" "));
    }

    /**
     * Remove any modifier present in the first two chars of a term
     */
    private static String removeModifierFromTerm(String term, String modifier)
    {
        String firstTwo;
        String remaining;

        if (term.length() >=2)
        {
            firstTwo = term.substring(0,2);
            remaining = term.substring(2, term.length());
        }
        else
        {
            firstTwo = term;
            remaining = "";
        }

        firstTwo = firstTwo.replace(modifier, "");

        return firstTwo+remaining;
    }
}
