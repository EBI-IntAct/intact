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

import org.apache.commons.beanutils.BeanUtils;
import uk.ac.ebi.intact.business.IntactException;

import java.io.Serializable;

/**
 * Class to be used as query value for searches. It is used
 * in the {@link uk.ac.ebi.intact.persistence.dao.SearchItemDao}
 * 
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Oct-2006</pre>
 */
public class SearchableQuery implements Serializable
{
    private String ac;
    private String shortLabel;
    private String description;
    private String fullText;
    private String xref;
    private String cvDatabaseLabel;
    private String annotationText;
    private String cvTopicLabel;
    private String cvIdentificationLabel;
    private String cvInteractionLabel;
    private String cvInteractionTypeLabel;
    private boolean includeCvInteractionChildren;
    private boolean includeCvIdentificationChildren;
    private boolean includeCvInteractionTypeChildren;
    private boolean disjunction;

    public SearchableQuery()
    {
    }

    public String getAc()
    {
        return ac;
    }

    public void setAc(String ac)
    {
        this.ac = ac;
    }

    public String getShortLabel()
    {
        return shortLabel;
    }

    public void setShortLabel(String shortLabel)
    {
        this.shortLabel = shortLabel;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getFullText()
    {
        return fullText;
    }

    public void setFullText(String fullText)
    {
        this.fullText = fullText;
    }

    public String getXref()
    {
        return xref;
    }

    public void setXref(String xref)
    {
        this.xref = xref;
    }

    public String getCvDatabaseLabel()
    {
        return cvDatabaseLabel;
    }

    public void setCvDatabaseLabel(String cvDatabaseLabel)
    {
        this.cvDatabaseLabel = cvDatabaseLabel;
    }

    public String getAnnotationText()
    {
        return annotationText;
    }

    public void setAnnotationText(String annotationText)
    {
        this.annotationText = annotationText;
    }

    public String getCvTopicLabel()
    {
        return cvTopicLabel;
    }

    public void setCvTopicLabel(String cvTopicLabel)
    {
        this.cvTopicLabel = cvTopicLabel;
    }

    public String getCvIdentificationLabel()
    {
        return cvIdentificationLabel;
    }

    public void setCvIdentificationLabel(String cvIdentificationLabel)
    {
        this.cvIdentificationLabel = cvIdentificationLabel;
    }

    public String getCvInteractionLabel()
    {
        return cvInteractionLabel;
    }

    public void setCvInteractionLabel(String cvInteractionLabel)
    {
        this.cvInteractionLabel = cvInteractionLabel;
    }

    public String getCvInteractionTypeLabel()
    {
        return cvInteractionTypeLabel;
    }

    public void setCvInteractionTypeLabel(String cvInteractionTypeLabel)
    {
        this.cvInteractionTypeLabel = cvInteractionTypeLabel;
    }

    public boolean isIncludeCvInteractionChildren()
    {
        return includeCvInteractionChildren;
    }

    public void setIncludeCvInteractionChildren(boolean includeCvInteractionChildren)
    {
        this.includeCvInteractionChildren = includeCvInteractionChildren;
    }

    public boolean isIncludeCvIdentificationChildren()
    {
        return includeCvIdentificationChildren;
    }

    public void setIncludeCvIdentificationChildren(boolean includeCvIdentificationChildren)
    {
        this.includeCvIdentificationChildren = includeCvIdentificationChildren;
    }

    public boolean isIncludeCvInteractionTypeChildren()
    {
        return includeCvInteractionTypeChildren;
    }

    public void setIncludeCvInteractionTypeChildren(boolean includeCvInteractionTypeChildren)
    {
        this.includeCvInteractionTypeChildren = includeCvInteractionTypeChildren;
    }

    public boolean isDisjunction()
    {
        return disjunction;
    }

    public void setDisjunction(boolean disjunction)
    {
        this.disjunction = disjunction;
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer(256);
        sb.append("{");

        appendValueToStringBuffer(sb, "ac", ac);
        appendValueToStringBuffer(sb, "shortLabel", shortLabel);
        appendValueToStringBuffer(sb, "description", description);
        appendValueToStringBuffer(sb, "fullText", fullText);
        appendValueToStringBuffer(sb, "xref", xref);
        appendValueToStringBuffer(sb, "cvDatabaseLabel", cvDatabaseLabel);
        appendValueToStringBuffer(sb, "annotationText", annotationText);
        appendValueToStringBuffer(sb, "cvTopicLabel", cvTopicLabel);
        appendValueToStringBuffer(sb, "cvIdentificationLabel", cvIdentificationLabel);
        appendValueToStringBuffer(sb, "cvInteractionLabel", cvInteractionLabel);
        appendValueToStringBuffer(sb, "cvInteractionTypeLabel", cvInteractionTypeLabel);

        if (includeCvInteractionChildren)
            appendValueToStringBuffer(sb, "includeCvInteractionChildren", includeCvInteractionChildren);

        if (includeCvIdentificationChildren)
            appendValueToStringBuffer(sb, "includeCvIdentificationChildren", includeCvIdentificationChildren);

        if (includeCvInteractionTypeChildren)
            appendValueToStringBuffer(sb, "includeCvInteractionTypeChildren", includeCvInteractionTypeChildren);

        if (disjunction)
            appendValueToStringBuffer(sb, "disjunction", disjunction);

        sb.deleteCharAt(sb.length()-1);
        
        sb.append('}');

        return sb.toString();
    }

    private static void appendValueToStringBuffer(StringBuffer sb, String prop, Object value)
    {
        if (value != null && value.toString().length() > 0)
        {
            sb.append(prop+"='").append(value).append('\'').append(";");
        }
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        SearchableQuery that = (SearchableQuery) o;

        if (disjunction != that.disjunction)
        {
            return false;
        }
        if (includeCvIdentificationChildren != that.includeCvIdentificationChildren)
        {
            return false;
        }
        if (includeCvInteractionChildren != that.includeCvInteractionChildren)
        {
            return false;
        }
        if (includeCvInteractionTypeChildren != that.includeCvInteractionTypeChildren)
        {
            return false;
        }
        if (ac != null ? !ac.equals(that.ac) : that.ac != null)
        {
            return false;
        }
        if (annotationText != null ? !annotationText.equals(that.annotationText) : that.annotationText != null)
        {
            return false;
        }
        if (cvDatabaseLabel != null ? !cvDatabaseLabel.equals(that.cvDatabaseLabel) : that.cvDatabaseLabel != null)
        {
            return false;
        }
        if (cvIdentificationLabel != null ? !cvIdentificationLabel.equals(that.cvIdentificationLabel) : that.cvIdentificationLabel != null)
        {
            return false;
        }
        if (cvInteractionLabel != null ? !cvInteractionLabel.equals(that.cvInteractionLabel) : that.cvInteractionLabel != null)
        {
            return false;
        }
        if (cvInteractionTypeLabel != null ? !cvInteractionTypeLabel.equals(that.cvInteractionTypeLabel) : that.cvInteractionTypeLabel != null)
        {
            return false;
        }
        if (cvTopicLabel != null ? !cvTopicLabel.equals(that.cvTopicLabel) : that.cvTopicLabel != null)
        {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null)
        {
            return false;
        }
        if (fullText != null ? !fullText.equals(that.fullText) : that.fullText != null)
        {
            return false;
        }
        if (shortLabel != null ? !shortLabel.equals(that.shortLabel) : that.shortLabel != null)
        {
            return false;
        }
        if (xref != null ? !xref.equals(that.xref) : that.xref != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        result = (ac != null ? ac.hashCode() : 0);
        result = 31 * result + (shortLabel != null ? shortLabel.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (fullText != null ? fullText.hashCode() : 0);
        result = 31 * result + (xref != null ? xref.hashCode() : 0);
        result = 31 * result + (cvDatabaseLabel != null ? cvDatabaseLabel.hashCode() : 0);
        result = 31 * result + (annotationText != null ? annotationText.hashCode() : 0);
        result = 31 * result + (cvTopicLabel != null ? cvTopicLabel.hashCode() : 0);
        result = 31 * result + (cvIdentificationLabel != null ? cvIdentificationLabel.hashCode() : 0);
        result = 31 * result + (cvInteractionLabel != null ? cvInteractionLabel.hashCode() : 0);
        result = 31 * result + (cvInteractionTypeLabel != null ? cvInteractionTypeLabel.hashCode() : 0);
        result = 31 * result + (includeCvInteractionChildren ? 1 : 0);
        result = 31 * result + (includeCvIdentificationChildren ? 1 : 0);
        result = 31 * result + (includeCvInteractionTypeChildren ? 1 : 0);
        result = 31 * result + (disjunction ? 1 : 0);
        return result;
    }


    /**
     * Create a <code>SearchableQuery</code> from a String. Using a regex pattern, gets the properties
     * and values from the expression and creates the query object using reflection
     */
    public static SearchableQuery paseSearchableQuery(String searchableQueryStr)
    {
        if (!isSearchableQuery(searchableQueryStr))
        {
            throw new IntactException("Not a parseable SearchableQuery: "+searchableQueryStr);
        }

        SearchableQuery query = new SearchableQuery();

        searchableQueryStr = searchableQueryStr.substring(1, searchableQueryStr.length()-1);

        String[] tokens = searchableQueryStr.split(";");

        for (String token : tokens)
        {
            String[] propAndValue = token.split("=");

            String propName = propAndValue[0];

            String propValue = propAndValue[1];

            // remove quotes if needed
            if (propValue.startsWith("'") && propValue.endsWith("'"))
            {
                propValue = propValue.substring(1,propValue.length()-1);
            }

             // check if the value is a boolean
            try
            {
                if (propValue.equals(Boolean.TRUE.toString()) || propValue.equals(Boolean.FALSE.toString()))
                {
                    addPropertyWithReflection(query, propName, Boolean.valueOf(propValue));
                }
                else
                {
                    addPropertyWithReflection(query, propName, propValue);
                }
            }
            catch (Exception e)
            {
                throw new IntactException("Exception parsing SearchQuery from String: "+searchableQueryStr);
            }
        }

        return query;
    }

    private static void addPropertyWithReflection(SearchableQuery query, String propName, Object value)
            throws Exception
    {
        BeanUtils.setProperty(query, propName, value);
    }

    public static boolean isSearchableQuery(String searchableQueryStr)
    {
        if (searchableQueryStr == null)
        {
            return false;
        }

        searchableQueryStr = searchableQueryStr.trim();

        return searchableQueryStr.startsWith("{") && searchableQueryStr.endsWith("}");
    }



}
