/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.NotAnEntityException;
import uk.ac.ebi.intact.persistence.dao.BaseDao;

import javax.persistence.Entity;
import java.sql.SQLException;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public abstract class HibernateBaseDaoImpl<T> implements BaseDao<Session>
{
    private Class<T> entityClass;
    private Session session;

    public HibernateBaseDaoImpl(Class<T> entityClass, Session session)
    {
        this.entityClass = entityClass;
       this.session = session;
    }

    public Session getSession()
    {
        return session;
    }

    public void flushCurrentSession()
    {
        session.flush();
    }

    /**
     * Provides the database name that is being connected to.
     *
     * @return String the database name, or an empty String if the query fails
     */
    public String getDbName() throws SQLException
    {
        String url =  session.connection().getMetaData().getURL();

        if (url.contains(":"))
        {
            url = url.substring(url.lastIndexOf(":")+1, url.length());
        }
        return url;
    }

    /**
     * Provides the user name that is connecting to the DB.
     *
     * @return String the user name
     *
     * @throws SQLException thrown if the metatdata can't be obtained
     */
    public String getDbUserName() throws SQLException {
        return session.connection().getMetaData().getUserName();
    }

    /**
     * Checks if the class passed as an argument has the annotation <code>@javax.persistence.Entity</code>.
     * If not, this methods throws a <code>NotAnEntityException</code>
     * @param entity The entity to validate
     */
    public static void validateEntity(Class<? extends IntactObject> entity)
    {
        if (entity.getAnnotation(Entity.class) == null)
        {
            throw new NotAnEntityException(entity);
        }
    }

     protected T getByPropertyName(String propertyName, String value)
    {
       return getByPropertyName(propertyName, value, true);
    }

    protected T getByPropertyName(String propertyName, String value, boolean ignoreCase)
    {
        return (T) getCriteriaByPropertyName(propertyName, value, ignoreCase).uniqueResult();
    }

    public Collection<T> getColByPropertyName(String propertyName, String value)
    {
         return getColByPropertyName(propertyName, value, true);
    }

    protected Collection<T> getColByPropertyName(String propertyName, String value, boolean ignoreCase)
    {
        if (value.startsWith("%") || value.endsWith("%"))
        {
            return getByPropertyNameLike(propertyName, value, ignoreCase);
        }

         return getCriteriaByPropertyName(propertyName, value, ignoreCase).list();
    }

    protected Collection<T> getByPropertyNameLike(String propertyName, String value)
    {
        return getByPropertyNameLike(propertyName, value, true);
    }

    protected Collection<T> getByPropertyNameLike(String propertyName, String value, boolean ignoreCase)
    {
        Criteria criteria = getSession().createCriteria(entityClass);
        
        SimpleExpression rest = Restrictions.like(propertyName, value);

        if (ignoreCase)
        {
            rest.ignoreCase();
        }

        criteria.add(rest);

        return criteria.list();
    }

    protected Disjunction disjunctionForArray(String propertyName, String[] values)
    {
        Disjunction disj = Restrictions.disjunction();

        for (String value : values)
        {
            if (value.contains("%"))
            {
                disj.add(Restrictions.like(propertyName, value));
            }
            else
            {
                disj.add(Restrictions.eq(propertyName, value));
            }
        }

        return disj;
    }

    public Class<T> getEntityClass()
    {
        return entityClass;
    }

    private Criteria getCriteriaByPropertyName(String propertyName, String value, boolean ignoreCase)
    {
        Criteria criteria = getSession().createCriteria(entityClass);

        SimpleExpression restriction = Restrictions.eq(propertyName, value);

        if (ignoreCase)
        {
           restriction.ignoreCase();
        }

        criteria.add(restriction);

        return criteria;
    }
}
