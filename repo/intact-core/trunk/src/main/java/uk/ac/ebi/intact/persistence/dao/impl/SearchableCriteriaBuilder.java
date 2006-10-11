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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoUtils;
import uk.ac.ebi.intact.persistence.dao.query.SearchableQuery;

import java.util.*;

/**
 * Allows to create a criteria from an {@link uk.ac.ebi.intact.persistence.dao.query.SearchableQuery}
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10-Oct-2006</pre>
 */
public class SearchableCriteriaBuilder
{
    private static final Log log = LogFactory.getLog(SearchableCriteriaBuilder.class);

    private SearchableQuery query;
    private Set<String> aliasesCreated;

    private Map<String,String> aliasesMap;

    private static final String SHORT_LABEL_PROPERTY = "shortLabel";
    private static final String FULL_NAME_PROPERTY = "fullName";
    private static final String AC_PROPERTY = "ac";
    private static final String PRIMARY_ID_PROPERTY = "primaryId";
    private static final String CV_OBJCLASS_PROPERTY = "objClass";

    public SearchableCriteriaBuilder(SearchableQuery query)
    {
        this.query = query;
        aliasesCreated = new HashSet<String>();

        aliasesMap = new HashMap<String,String>();
    }

    public DetachedCriteria createCriteria(Class<? extends Searchable> searchableClass)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(searchableClass);

        Junction junction;

        if (query.isDisjunction())
        {
            junction = Restrictions.disjunction();
        }
        else
        {
            junction = Restrictions.conjunction();
        }

        // ac
        addRestriction(junction, AC_PROPERTY, query.getAc(), true);

        // shortLabel
        addRestriction(junction, SHORT_LABEL_PROPERTY, query.getShortLabel(), false);

        // description
        addRestriction(junction, FULL_NAME_PROPERTY, query.getDescription(), false);

        // full-text
        addFullTextRestriction(searchableClass, criteria, query.getFullText(), true);

        // xref - database
        String xrefPrimaryId = query.getXref();
        String xrefDb = query.getCvDatabaseLabel();

        Junction jXref = Restrictions.conjunction();

        if (xrefPrimaryId != null)
        {
            addRestriction(jXref, xrefProperty(criteria, PRIMARY_ID_PROPERTY), xrefPrimaryId, true);
        }
        if (xrefDb != null)
        {
            addRestriction(jXref, xrefCvDatabaseProperty(criteria, SHORT_LABEL_PROPERTY), xrefDb, false);
        }
        if (xrefPrimaryId != null && xrefDb != null)
        {
            junction.add(jXref);
        }


        // annotation
        String annotText = query.getAnnotationText();
        String annotTopic = query.getCvTopicLabel();

        Junction jAnnot = Restrictions.conjunction();

        if (annotText != null)
        {
            addRestriction(jAnnot, annotationProperty(criteria, "annotationText"), annotText, false);
        }
        if (annotTopic != null)
        {
            addRestriction(jAnnot, annotationCvTopicProperty(criteria, SHORT_LABEL_PROPERTY), annotTopic, false);
        }
        if (annotText != null || annotTopic != null)
        {
            junction.add(jAnnot);
        }

        if (searchableClass.isAssignableFrom(Interactor.class))
        {
            // cvInteractionType
            Disjunction disjCvInteractionType = Restrictions.disjunction();

            addRestriction(disjCvInteractionType, cvInteractionTypeProperty(criteria, SHORT_LABEL_PROPERTY), query.getCvInteractionTypeLabel(), true);

            if (query.isIncludeCvInteractionTypeChildren())
            {
                Junction childJunct = getChildrenDisjunctionForCvInteractionType(criteria,
                                        cvInteractionTypeProperty(criteria, SHORT_LABEL_PROPERTY),
                                        cvInteractionTypeProperty(criteria, CV_OBJCLASS_PROPERTY),
                                        CvInteractionType.class, query.getCvInteractionTypeLabel());

                if (childJunct != null)
                {
                    disjCvInteractionType.add(childJunct);
                }
            }

            junction.add(disjCvInteractionType);
        }


        if (searchableClass.isAssignableFrom(Experiment.class))
        {
            log.debug("Experiment search");

            Disjunction disjCvIdentification = Restrictions.disjunction();
            
            // cvInteractionDetection
            addRestriction(disjCvIdentification, cvIdentificationProperty(criteria, SHORT_LABEL_PROPERTY), query.getCvIdentificationLabel(), true);

            if (query.isIncludeCvIdentificationChildren())
            {
                Junction childJunct = getChildrenDisjunctionForCvInteractionType(criteria,
                                            cvIdentificationProperty(criteria, SHORT_LABEL_PROPERTY),
                                            cvIdentificationProperty(criteria, CV_OBJCLASS_PROPERTY),
                                            CvIdentification.class, query.getCvIdentificationLabel());

                if (childJunct != null)
                {
                    disjCvIdentification.add(childJunct);
                }
            }

            junction.add(disjCvIdentification);

            Disjunction disjCvInteraction = Restrictions.disjunction();
    
            // cvInteractionDetection
            addRestriction(disjCvInteraction, cvInteractionProperty(criteria, SHORT_LABEL_PROPERTY), query.getCvInteractionLabel(), true);

            if (query.isIncludeCvInteractionChildren())
            {
                Junction childJunct = getChildrenDisjunctionForCvInteractionType(criteria,
                                        cvInteractionProperty(criteria, SHORT_LABEL_PROPERTY),
                                        cvInteractionProperty(criteria, CV_OBJCLASS_PROPERTY),
                                        CvInteraction.class, query.getCvInteractionLabel());

                if (childJunct != null)
                {
                    disjCvInteraction.add(childJunct);
                }
            }

            junction.add(disjCvInteraction);
        }

        criteria.add(junction);

        return criteria;
    }

    private void addRestriction(Junction junction, String property, String value, boolean exactEquality)
    {
        if (value != null && !value.trim().equals(""))
        {
            log.debug("Restriction: "+property+"="+value);
            
            if (exactEquality)
            {
                junction.add(Restrictions.eq(property, value).ignoreCase());
            }
            else
            {
                junction.add(Restrictions.like(property, DaoUtils.replaceWildcardsByPercent(value)).ignoreCase());
            }
        }
    }

    private void addFullTextRestriction(Class<? extends Searchable> searchableClass, DetachedCriteria criteria, String value, boolean autoAddWildcards)
    {
        if (value != null && !value.trim().equals(""))
        {
            if (autoAddWildcards)
            {
                value = DaoUtils.addPercents(value);
            }

            Junction disj = Restrictions.disjunction();

            addRestriction(disj, AC_PROPERTY, value, false);
            addRestriction(disj, SHORT_LABEL_PROPERTY, value, false);
            addRestriction(disj, FULL_NAME_PROPERTY, value, false);
            addRestriction(disj, xrefProperty(criteria, PRIMARY_ID_PROPERTY), value, true);
            addRestriction(disj, xrefCvDatabaseProperty(criteria, SHORT_LABEL_PROPERTY), value, false);
            addRestriction(disj, annotationProperty(criteria, "annotationText"), value, false);
            addRestriction(disj, annotationCvTopicProperty(criteria, SHORT_LABEL_PROPERTY), value, false);

            if (searchableClass.isAssignableFrom(Interactor.class))
            {
                addRestriction(disj, cvInteractionTypeProperty(criteria, SHORT_LABEL_PROPERTY), value, false);
            }

            if (searchableClass.isAssignableFrom(Experiment.class))
            {
                addRestriction(disj, cvIdentificationProperty(criteria, SHORT_LABEL_PROPERTY), value, false);
                addRestriction(disj, cvInteractionProperty(criteria, SHORT_LABEL_PROPERTY), value, false);
            }

            criteria.add(disj);
        }
    }

    private Junction getChildrenDisjunctionForCvInteractionType(DetachedCriteria criteria, String shortLabelProperty, String objClassProperty, Class<? extends CvObject> cvType, String cvShortLabel)
    {
        log.debug("\tGetting children for: " + cvShortLabel);

        if (cvShortLabel == null)
        {
            return null;
        }

        CvDagObject cvDagObject = (CvDagObject) IntactContext.getCurrentInstance().getCvContext()
                .getByLabel(cvType, cvShortLabel);

        if (cvDagObject == null)
        {
            throw new IntactException("No CvDagObject with label '" + cvShortLabel + "' could be found");
        }

        Collection<CvDagObject> children = cvDagObject.getChildren();

        if (children.isEmpty())
        {
            return null;
        }

        Junction disj = Restrictions.disjunction();

        for (CvDagObject child : children)
        {
            String childLabel = child.getShortLabel();

            Junction cvConj = Restrictions.conjunction();

            addRestriction(cvConj, shortLabelProperty, child.getShortLabel(), true);
            addRestriction(cvConj, objClassProperty, child.getObjClass(), true);

            disj.add(cvConj);

            Junction childJunct = getChildrenDisjunctionForCvInteractionType(criteria, shortLabelProperty, objClassProperty, cvType, childLabel);

            if (childJunct != null)
            {
                disj.add(childJunct);
            }
        }

        return disj;
    }

    private String annotationProperty(DetachedCriteria criteria, String property)
    {
        String aliasName = "annotation";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias("annotations", aliasName);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String xrefProperty(DetachedCriteria criteria, String property)
    {
        String aliasName = "xref";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias("xrefs", aliasName);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String annotationCvTopicProperty(DetachedCriteria criteria, String property)
    {
        String aliasName = "annotCvTopic";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias(annotationProperty(criteria, "cvTopic"), aliasName);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String xrefCvDatabaseProperty(DetachedCriteria criteria, String property)
    {
        String aliasName = "xrefCvDatabase";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias(xrefProperty(criteria, "cvDatabase"), aliasName);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String cvInteractionTypeProperty(DetachedCriteria criteria, String property)
    {
        String aliasName = "cvInteractionType";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias("cvInteractionType", aliasName);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String cvIdentificationProperty(DetachedCriteria criteria, String property)
    {
        String aliasName = "cvIdentification";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias("cvIdentification", aliasName);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String cvInteractionProperty(DetachedCriteria criteria, String property)
    {
        String aliasName = "cvInteraction";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias("cvInteraction", aliasName);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }


}
