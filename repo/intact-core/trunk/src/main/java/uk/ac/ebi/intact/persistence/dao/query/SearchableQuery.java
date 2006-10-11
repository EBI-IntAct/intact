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
}
