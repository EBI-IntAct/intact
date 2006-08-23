/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.persistence.dao.AnnotatedObjectDao;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class AnnotatedObjectDaoImpl<T extends AnnotatedObject> extends IntactObjectDaoImpl<T> implements AnnotatedObjectDao<T>
{

    public AnnotatedObjectDaoImpl(Class<T> entityClass, Session session)
    {
        super(entityClass, session);
    }

    public T getByShortLabel(String value)
    {
        return getByShortLabel(value, true);
    }

    public T getByShortLabel(String value, boolean ignoreCase)
    {
        return getByPropertyName("shortLabel", value, ignoreCase);
    }

    public Collection<T> getByShortLabelLike(String value)
    {
       return getByPropertyNameLike("shortLabel", value);
    }

    public Collection<T> getByShortLabelLike(String value, boolean ignoreCase)
    {
       return getByPropertyNameLike("shortLabel", value, ignoreCase);
    }

    public T getByXref(String primaryId)
    {
        return (T) getSession().createCriteria(getEntityClass())
                .createCriteria("xrefs", "xref")
                .add(Restrictions.eq("xref.primaryId", primaryId)).uniqueResult();
    }

    public List<T> getByXrefLike(String primaryId)
    {
        return getSession().createCriteria(getEntityClass())
                .createCriteria("xrefs", "xref")
                .add(Restrictions.like("xref.primaryId", primaryId)).list();
    }

    public List<T> getByXrefLike(CvDatabase database, String primaryId)
    {
        return getSession().createCriteria(getEntityClass())
                .createCriteria("xrefs", "xref")
                .add(Restrictions.like("xref.primaryId", primaryId))
                .add(Restrictions.eq("xref.cvDatabase", database)).list();
    }

    public List<T> getByXrefLike(CvDatabase database, CvXrefQualifier qualifier, String primaryId)
    {
        return getSession().createCriteria(getEntityClass())
                .createCriteria("xrefs", "xref")
                .add(Restrictions.like("xref.primaryId", primaryId))
                .add(Restrictions.eq("xref.cvDatabase", database))
                .add(Restrictions.eq("xref.cvXrefQualifier", qualifier)).list();

    }

    /**
     * Return a collection of annotated object of type <T> being annotated with an annotation having
     * its ac equal to the ac given in parameter of the method.
     * @param ac
     * @return  a list of annotated objects.
     */
    public List<T> getByAnnotationAc(String ac)
    {
        return getSession().createCriteria(getEntityClass())
                .createAlias("annotations", "annot")
                .add(Restrictions.eq("annot.ac", ac)).list();
    }

    /**
     * Return a collection of annotated object of type <T> being annotated with an annotation having
     * a topic equal to the topic given in parameter and the description equal to the description given
     * in parameter.
     * @param topic
     * @param description
     * @return  a list of annotated objects.
     */
    public List<T> getByAnnotationTopicAndDescription(CvTopic topic, String description){
        return getSession().createCriteria(getEntityClass()).createAlias("annotations","annot")
                .add(Restrictions.eq("annot.cvTopic",topic))
                .add(Restrictions.eq("annot.annotationText",description )).list();
    }

    /**
     * Gets all the CVs for the current entity
     * @param excludeObsolete if true exclude the obsolete CVs
     * @param excludeHidden if true exclude the hidden CVs
     * @return the list of CVs
     */
    public List<T> getAll(boolean excludeObsolete, boolean excludeHidden)
    {
        Criteria crit = getSession().createCriteria(getEntityClass());

        if (excludeObsolete || excludeHidden)
        {
            crit.createAlias("annotations", "annot")
                .createAlias("annot.cvTopic", "annotTopic");
        }

        if (excludeObsolete && excludeHidden)
        {
            crit.add(Restrictions.disjunction()
                    .add(Restrictions.ne("annotTopic.shortLabel", CvTopic.OBSOLETE))
                    .add(Restrictions.ne("annotTopic.shortLabel", CvTopic.HIDDEN)));
        }
        else if (excludeObsolete && !excludeHidden)
        {
            crit.add(Restrictions.ne("annotTopic.shortLabel", CvTopic.OBSOLETE));
        }
        else if (!excludeObsolete && excludeHidden)
        {
            crit.add(Restrictions.ne("annotTopic.shortLabel", CvTopic.HIDDEN));
        }

        return crit.list();
    }
    /**
     * This method will search in the database an AnnotatedObject of type T having it's shortlabel or it's
     * ac like the searchString given in argument.
     * @param searchString (ex : "butkevitch-2006-%", "butkevitch-%-%", "EBI-12345%"
     * @return a List of AnnotatedObject having their ac or shortlabel like the searchString
     */
    public List<T> getByShortlabelOrAcLike(String searchString)
    {
        return getSession().createCriteria(getEntityClass())
                .add(Restrictions.or(
                        Restrictions.like("ac",searchString).ignoreCase(),
                        Restrictions.like("shortLabel",searchString).ignoreCase())).list();
    }

}
