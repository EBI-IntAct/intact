/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Order;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.intact.model.IntactObject;

/**
 * Basic queries for IntactObjects
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class IntactObjectDao<T extends IntactObject> extends HibernateDao<T>
{
    public IntactObjectDao(Class<T> entityClass, Session session)
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

    /**
     * Performs a unique query for an array of ACs. Beware that depending on the database used
     * this query has limitation (for instance, in Oracle it is limited to 1000 items)
     * @param acs The acs to look for
     * @return the collection of entities with those ACs
     */
    public List<T> getByAc(String[] acs)
    {
        return getSession().createCriteria(getEntityClass())
                    .add(Restrictions.in("ac", acs))
                    .addOrder(Order.asc("ac")).list();
    }


}
