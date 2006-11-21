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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
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
 * @since 1.5
 */
public class SearchableCriteriaBuilder
{
    private static final Log log = LogFactory.getLog(SearchableCriteriaBuilder.class);

    private SearchableQuery query;
    private Set<String> aliasesCreated;

    private static final String SHORT_LABEL_PROPERTY = "shortLabel";
    private static final String FULL_NAME_PROPERTY = "fullName";
    private static final String AC_PROPERTY = "ac";
    private static final String PRIMARY_ID_PROPERTY = "primaryId";
    private static final String CV_OBJCLASS_PROPERTY = "objClass";

    private SearchValueConverter searchValueConverter;

    public SearchableCriteriaBuilder(SearchableQuery query)
    {
        this.query = query;
        aliasesCreated = new HashSet<String>();
        this.searchValueConverter = new StandardSearchValueConverter();
    }

    public SearchableCriteriaBuilder(SearchableQuery query, SearchValueConverter searchValueConverter)
    {
        this.query = query;
        aliasesCreated = new HashSet<String>();
        this.searchValueConverter = searchValueConverter;
    }

    /**
     * Completes a provided criteria with the necessary restrictions.
     * @param searchableClass the class to search
     * @param session a hibernate session
     * @return
     */
    public Criteria createCriteria(Class<? extends Searchable> searchableClass, Session session)
    {
        if (query == null)
        {
            throw new NullPointerException("SearchableQuery cannot be null");
        }

        if (log.isDebugEnabled())
        {
            log.debug("Search for: "+searchableClass+", with query: "+query);
        }

        Criteria criteria = session.createCriteria(searchableClass);

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
        addRestriction(junction, AC_PROPERTY, query.getAc());

        // shortLabel
        addRestriction(junction, SHORT_LABEL_PROPERTY, query.getShortLabel());

        // description
        addRestriction(junction, FULL_NAME_PROPERTY, query.getDescription());

        // full-text
        addFullTextRestriction(searchableClass, criteria, query.getFullText(), true);

        // xref - database
        String xrefPrimaryId = query.getXref();
        String xrefDb = query.getCvDatabaseLabel();

        Junction jXref = Restrictions.conjunction();

        if (isValueValid(xrefPrimaryId))
        {
            addRestriction(jXref, xrefProperty(criteria, PRIMARY_ID_PROPERTY), xrefPrimaryId);
        }
        if (isValueValid(xrefDb))
        {
            addRestriction(jXref, xrefCvDatabaseProperty(criteria, SHORT_LABEL_PROPERTY), xrefDb);
        }
        if (isValueValid(xrefPrimaryId) || isValueValid(xrefDb))
        {
            junction.add(jXref);
        }

        // annotation
        String annotText = query.getAnnotationText();
        String annotTopic = query.getCvTopicLabel();

        Junction jAnnot = Restrictions.conjunction();

        if (isValueValid(annotText))
        {
            addRestriction(jAnnot, annotationProperty(criteria, "annotationText"), annotText);
        }
        if (isValueValid(annotTopic))
        {
            addRestriction(jAnnot, annotationCvTopicProperty(criteria, SHORT_LABEL_PROPERTY), annotTopic);
        }
        if (isValueValid(annotText) || isValueValid(annotTopic))
        {
            junction.add(jAnnot);
        }

        // searchable is an interactor
        if (searchableClass.isAssignableFrom(Interactor.class))
        {
            // cvInteractionType
            if (isValueValid(query.getCvInteractionTypeLabel()))
            {
                Conjunction objAndLabelConj = Restrictions.conjunction();

                addRestriction(objAndLabelConj, cvInteractionTypeProperty(criteria, SHORT_LABEL_PROPERTY), query.getCvInteractionTypeLabel(), true);
                objAndLabelConj.add(Restrictions.eq(cvIdentificationProperty(criteria, CV_OBJCLASS_PROPERTY), CvInteractionType.class.getName()));

                if (query.isIncludeCvInteractionTypeChildren())
                {
                    Junction childJunct = getChildrenDisjunctionForCv(criteria,
                            cvInteractionTypeProperty(criteria, SHORT_LABEL_PROPERTY),
                            cvInteractionTypeProperty(criteria, CV_OBJCLASS_PROPERTY),
                            CvInteractionType.class, query.getCvInteractionTypeLabel());

                    if (childJunct != null) // children found, create the disjunction
                    {
                        Disjunction disjCvInteractionType = Restrictions.disjunction();
                        disjCvInteractionType.add(objAndLabelConj);
                        disjCvInteractionType.add(childJunct);
                        junction.add(disjCvInteractionType);
                    }
                    else
                    {
                         junction.add(objAndLabelConj);
                    }
                }
                else
                {
                    junction.add(objAndLabelConj);
                }
            }
        }

        // searchable is a experiment
        if (searchableClass.isAssignableFrom(Experiment.class))
        {
            // cvInteractionDetection
            if (isValueValid(query.getCvIdentificationLabel()))
            {
                Conjunction objAndLabelConj = Restrictions.conjunction();
                objAndLabelConj.add(Restrictions.eq(cvIdentificationProperty(criteria, CV_OBJCLASS_PROPERTY), CvIdentification.class.getName()));

                addRestriction(objAndLabelConj, cvIdentificationProperty(criteria, SHORT_LABEL_PROPERTY), query.getCvIdentificationLabel(), true);

                if (query.isIncludeCvIdentificationChildren())
                {
                    Junction childJunct = getChildrenDisjunctionForCv(criteria,
                            cvIdentificationProperty(criteria, SHORT_LABEL_PROPERTY),
                            cvIdentificationProperty(criteria, CV_OBJCLASS_PROPERTY),
                            CvIdentification.class, query.getCvIdentificationLabel());

                    if (childJunct != null) // children found, create the disjunction
                    {
                        Disjunction disjCvIdentification = Restrictions.disjunction();
                        disjCvIdentification.add(objAndLabelConj);
                        disjCvIdentification.add(childJunct);
                        junction.add(disjCvIdentification);
                    }
                    else
                    {
                         junction.add(objAndLabelConj);
                    }
                }
                else
                {
                    junction.add(objAndLabelConj);
                }
            }

            // cvInteractionDetection
            if (isValueValid(query.getCvInteractionLabel()))
            {
                Conjunction objAndLabelConj = Restrictions.conjunction();
                objAndLabelConj.add(Restrictions.eq(cvInteractionProperty(criteria, CV_OBJCLASS_PROPERTY), CvInteraction.class.getName()));

                addRestriction(objAndLabelConj, cvInteractionProperty(criteria, SHORT_LABEL_PROPERTY), query.getCvInteractionLabel(), true);

                if (query.isIncludeCvInteractionChildren())
                {
                    Junction childJunct = getChildrenDisjunctionForCv(criteria,
                            cvInteractionProperty(criteria, SHORT_LABEL_PROPERTY),
                            cvInteractionProperty(criteria, CV_OBJCLASS_PROPERTY),
                            CvInteraction.class, query.getCvInteractionLabel());

                    if (childJunct != null)  // children found, create the disjunction
                    {
                        Disjunction disjCvInteraction = Restrictions.disjunction();
                        disjCvInteraction.add(childJunct);
                        disjCvInteraction.add(objAndLabelConj);
                        junction.add(disjCvInteraction);
                    }
                    else
                    {
                        junction.add(objAndLabelConj);
                    }
                }
                else
                {
                    junction.add(objAndLabelConj);
                }
            }
        }

        // finally add the junction created by all the properties to the main criteria
        criteria.add(junction);

        return criteria;
    }

    private void addRestriction(Junction junction, String property, String value, boolean allValueIsPhrase)
    {
        if (isValueValid(value))
        {
            if (allValueIsPhrase)
            {
                if (!value.startsWith("\""))
                {
                    value = "\""+value;
                }

                if (!value.endsWith("\""))
                {
                    value = value+"\"";
                }
            }

            Criterion criterion = searchValueConverter.valueToCriterion(property, value);
            junction.add(criterion);
        }
    }

    private void addRestriction(Junction junction, String property, String value)
    {
        if (isValueValid(value))
        {
            Criterion criterion = searchValueConverter.valueToCriterion(property, value);
            junction.add(criterion);
        }
    }

    private void addFullTextRestriction(Class<? extends Searchable> searchableClass, Criteria criteria, String value, boolean autoAddWildcards)
    {
        if (isValueValid(value))
        {
            if (autoAddWildcards)
            {
                value = DaoUtils.addPercents(value);
            }

            Junction disj = Restrictions.disjunction();

            addRestriction(disj, AC_PROPERTY, value);
            addRestriction(disj, SHORT_LABEL_PROPERTY, value);
            addRestriction(disj, FULL_NAME_PROPERTY, value);
            addRestriction(disj, xrefProperty(criteria, PRIMARY_ID_PROPERTY), value);
            addRestriction(disj, xrefCvDatabaseProperty(criteria, SHORT_LABEL_PROPERTY), value);
            addRestriction(disj, annotationProperty(criteria, "annotationText"), value);
            addRestriction(disj, annotationCvTopicProperty(criteria, SHORT_LABEL_PROPERTY), value);

            if (searchableClass.isAssignableFrom(Interactor.class))
            {
                addRestriction(disj, cvInteractionTypeProperty(criteria, SHORT_LABEL_PROPERTY), value);
            }

            if (searchableClass.isAssignableFrom(Experiment.class))
            {
                addRestriction(disj, cvIdentificationProperty(criteria, SHORT_LABEL_PROPERTY), value);
                addRestriction(disj, cvInteractionProperty(criteria, SHORT_LABEL_PROPERTY), value);
            }

            criteria.add(disj);
        }
    }

    private Junction getChildrenDisjunctionForCv(Criteria criteria, String shortLabelProperty, String objClassProperty, Class<? extends CvObject> cvType, String cvShortLabel)
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
            throw new IntactException("No CvDagObject with label '" + cvShortLabel + "' and type '"+cvType+"' could be found");
        }

        Disjunction mainDisjunction = Restrictions.disjunction();

        Map<Class<? extends CvObject>,Set<String>> classifiedChildren = getChildrenCvClassified(cvDagObject);

        if (classifiedChildren.isEmpty())
        {
            return null;
        }

        for (Map.Entry<Class<? extends CvObject>,Set<String>> childEntry : classifiedChildren.entrySet())
        {
            Junction entryConjunction = Restrictions.conjunction()
                    .add(Restrictions.eq(objClassProperty, childEntry.getKey().getName()));

            Disjunction disj  = Restrictions.disjunction();

            for (String label : childEntry.getValue())
            {
                addRestriction(disj, shortLabelProperty, label, true);
            }

            entryConjunction.add(disj);
            mainDisjunction.add(entryConjunction);
        }

        return mainDisjunction;
    }

    private Map<Class<? extends CvObject>,Set<String>> getChildrenCvClassified(CvDagObject parentCv)
    {
        if (parentCv == null)
        {
            throw new IntactException("CvDagObject is null");
        }

        Map<Class<? extends CvObject>,Set<String>> cvsMap = new HashMap<Class<? extends CvObject>,Set<String>>();

        List<CvDagObject> children = new ArrayList<CvDagObject>();
        fillChildrenRecursively(parentCv, children);

        for (CvDagObject child : children)
        {
            addCvObjectToMap(cvsMap, child.getClass(), child.getShortLabel());
        }

        return cvsMap;
    }

    private void addCvObjectToMap(Map<Class<? extends CvObject>, Set<String>> cvsMap, Class cvType, String label)
    {
        if (cvsMap.containsKey(cvType))
        {
            cvsMap.get(cvType).add(label);
        }
        else
        {
            Set<String> labels = new HashSet<String>();
            labels.add(label);
            cvsMap.put(cvType, labels);
        }
    }

    private void fillChildrenRecursively(CvDagObject parentCv, Collection<CvDagObject> children)
    {
        Collection<CvDagObject> parentChildren = parentCv.getChildren();

        children.addAll(parentChildren);

        for (CvDagObject child : parentChildren)
        {
            fillChildrenRecursively(child, children);
        }
    }

    private String annotationProperty(Criteria criteria, String property)
    {
        String aliasName = "annotation";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias("annotations", aliasName);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String xrefProperty(Criteria criteria, String property)
    {
        String aliasName = "xref";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias("xrefs", aliasName, CriteriaSpecification.LEFT_JOIN);

            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String annotationCvTopicProperty(Criteria criteria, String property)
    {
        String aliasName = "annotCvTopic";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias(annotationProperty(criteria, "cvTopic"), aliasName, CriteriaSpecification.LEFT_JOIN);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String xrefCvDatabaseProperty(Criteria criteria, String property)
    {
        String aliasName = "xrefCvDatabase";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias(xrefProperty(criteria, "cvDatabase"), aliasName, CriteriaSpecification.LEFT_JOIN);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String cvInteractionTypeProperty(Criteria criteria, String property)
    {
        String aliasName = "cvInteractionType";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias("cvInteractionType", aliasName, CriteriaSpecification.LEFT_JOIN);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String cvIdentificationProperty(Criteria criteria, String property)
    {
        String aliasName = "cvIdentification";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias("cvIdentification", aliasName, CriteriaSpecification.LEFT_JOIN);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private String cvInteractionProperty(Criteria criteria, String property)
    {
        String aliasName = "cvInteraction";

        if (!aliasesCreated.contains(aliasName))
        {
            criteria.createAlias("cvInteraction", aliasName, CriteriaSpecification.LEFT_JOIN);
            aliasesCreated.add(aliasName);
        }

        return aliasName + "." + property;
    }

    private static boolean isValueValid(String value)
    {
        return value != null && value.trim().length() > 0;
    }


}
