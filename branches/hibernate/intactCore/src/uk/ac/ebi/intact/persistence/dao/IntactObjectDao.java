/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.SimpleExpression;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class IntactObjectDao<T> extends HibernateDao
{
    private Class<T> entityClass;

    public IntactObjectDao(Class<T> entityClass, Session session)
    {
        super(session);
        this.entityClass = entityClass;
    }

    public T getByAc(String ac)
    {
       return (T) getSession().get(entityClass, ac);
    }

    public T getByShortLabel(String value)
    {
        return getByShortLabel(value, true);
    }

    public T getByShortLabel(String value, boolean ignoreCase)
    {
        return getByShortLabel(value, ignoreCase);
    }

    public Collection<T> getByShortLabelLike(String value, boolean ignoreCase, MatchMode matchMode)
    {
       return getByPropertyNameLike("shortLabel", value, ignoreCase, matchMode);
    }

    protected T getByPropertyName(String propertyName, String value)
    {
       return getByPropertyName(propertyName, value, true);
    }

    protected T getByPropertyName(String propertyName, String value, boolean ignoreCase)
    {
       Criteria criteria = getSession().createCriteria(entityClass);

        SimpleExpression restriction = Restrictions.eq(propertyName, value);

        if (ignoreCase)
        {
           restriction.ignoreCase();
        }

        criteria.add(restriction);

        return (T) criteria.uniqueResult();
    }

    protected Collection<T> getByPropertyNameLike(String propertyName, String value)
    {
        Criteria criteria = getSession().createCriteria(entityClass)
                .add(Restrictions.like(propertyName, value).ignoreCase());

        return criteria.list();
    }

    protected Collection<T> getByPropertyNameLike(String propertyName, String value,  boolean ignoreCase, MatchMode matchMode)
    {
       Criteria criteria = getSession().createCriteria(entityClass);

        SimpleExpression restriction = Restrictions.like(propertyName, value, matchMode);

        if (ignoreCase)
        {
           restriction.ignoreCase();
        }

        criteria.add(restriction);

        return criteria.list();
    }

}
