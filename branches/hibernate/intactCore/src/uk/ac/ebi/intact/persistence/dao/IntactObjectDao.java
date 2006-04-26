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

import java.util.Collection;

import uk.ac.ebi.intact.model.IntactObject;

/**
 * TODO comment this
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

    public T getByAc(String ac)
    {
       return (T) getSession().get(getEntityClass(), ac);
    }


}
