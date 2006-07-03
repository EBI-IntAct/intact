/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.intact.model.AnnotatedObject;
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
                .add(Restrictions.eq("xref.ac", primaryId)).uniqueResult();
    }

    public List<T> getByXrefLike(String primaryId)
    {
        return getSession().createCriteria(getEntityClass())
                .createCriteria("xrefs", "xref")
                .add(Restrictions.like("xref.ac", primaryId)).list();
    }


}
