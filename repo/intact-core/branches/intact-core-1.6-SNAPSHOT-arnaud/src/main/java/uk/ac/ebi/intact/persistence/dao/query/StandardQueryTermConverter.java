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
package uk.ac.ebi.intact.persistence.dao.query;

/**
 * Convert a string to a term and viceversa
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since
 */
public class StandardQueryTermConverter implements QueryTermConverter<String>
{

    public QueryTerm objectToTerm(String value) throws QueryPhraseException
    {
        return null;
    }

    public String termToObject(QueryTerm phrase) throws QueryPhraseException
    {
        return null;
    }

    private QueryModifier identifyModifier(String value)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    private String removeModifierExceptWildcards(String value, QueryModifier modifier)
    {
        String valueWithoutModifier = value;

        switch (modifier)
        {
            case EXCLUDE:
                valueWithoutModifier = value.substring(1);
                break;
            case INCLUDE:
                valueWithoutModifier = value.substring(1);
                break;
            case PHRASE_DELIM:
                valueWithoutModifier = value.substring(1, value.length()-1);
                break;
        }

        return valueWithoutModifier.trim();
    }
}
