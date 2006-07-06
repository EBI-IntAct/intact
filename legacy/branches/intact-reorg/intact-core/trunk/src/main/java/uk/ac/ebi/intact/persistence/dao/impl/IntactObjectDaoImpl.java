/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.persistence.dao.IntactObjectDao;

import java.util.Collection;
import java.util.List;

/**
 * Basic queries for IntactObjects
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class IntactObjectDaoImpl<T extends IntactObject> extends HibernateBaseDaoImpl<T> implements IntactObjectDao<T>
{
    public IntactObjectDaoImpl(Class<T> entityClass, Session session)
    {
        super(entityClass, session);
    }

    /**
     * Get an item using its AC
     * @param ac the identifier
     * @return the object
     */
    public T getByAc(String ac)
    {
       return (T) getSession().get(getEntityClass(), ac);
    }

    public Collection<T> getByAcLike(String ac)
    {
        return getByPropertyNameLike("ac", ac);
    }


    /**
     * Performs a unique query for an array of ACs. Beware that depending on the database used
     * this query has limitation (for instance, in Oracle it is limited to 1000 items)
     * @param acs The acs to look for
     * @return the collection of entities with those ACs
     */
    public List<T> getByAc(String[] acs)
    {
        if (acs.length == 0)
        {
            throw new HibernateException("At least one AC is needed to query by AC.");
        }

        return getSession().createCriteria(getEntityClass())
                    .add(Restrictions.in("ac", acs))
                    .addOrder(Order.asc("ac")).list();
    }

    public List<T> getByAc(Collection<String> acs)
    {
        return getByAc(acs.toArray(new String[acs.size()]));
    }

    public List<T> getAll()
    {
        return getSession().createCriteria(getEntityClass()).list();
    }

    public void update(T objToUpdate)
    {
       getSession().update(objToUpdate);
    }

    public void persist(T objToPersist)
    {
       getSession().persist(objToPersist);
    }

    public void delete(T objToDelete)
    {
        getSession().delete(objToDelete);
    }

    public void saveOrUpdate(T objToPersist)
    {
        getSession().saveOrUpdate(objToPersist);
    }

    public boolean exists(T obj)
    {
        return (getSession().get(getEntityClass(), obj.getAc()) != null);
    }

    public void refresh(T objToRefresh)
    {
         getSession().refresh(objToRefresh);
    }

}
