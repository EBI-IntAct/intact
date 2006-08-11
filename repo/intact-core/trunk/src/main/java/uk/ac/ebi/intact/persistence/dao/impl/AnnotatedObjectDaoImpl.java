/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Property;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
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

    public Collection<T> getByShortLabelLike(String value, int firstResult, int maxResults)
    {
        return getByPropertyNameLike("shortLabel", value, true, firstResult, maxResults);
    }

    public Collection<T> getByShortLabelLike(String value, boolean ignoreCase)
    {
       return getByPropertyNameLike("shortLabel", value, ignoreCase, -1, -1);
    }

    public Collection<T> getByShortLabelLike(String value, boolean ignoreCase, int firstResult, int maxResults)
    {
        return getByPropertyNameLike("shortLabel", value, ignoreCase, firstResult, maxResults);
    }

    public Collection<T> getByShortLabelLike(String value, boolean ignoreCase, int firstResult, int maxResults, boolean orderAsc)
    {
        return getByPropertyNameLike("shortLabel", value, ignoreCase, firstResult, maxResults, orderAsc);
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

    public String getPrimaryIdByAc(String ac, String cvDatabaseShortLabel)
    {
       return (String) getSession().createCriteria(getEntityClass())
               .add(Restrictions.idEq(ac))
               .createAlias("xrefs", "xref")
               .createAlias("xref.cvDatabase", "cvDatabase")
               .add(Restrictions.like("cvDatabase.shortLabel", cvDatabaseShortLabel))
               .setProjection(Property.forName("xref.primaryId")).uniqueResult();

    }


}
