/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.persistence.util.HibernateUtil;
import org.hibernate.Session;

/**
 * Factory for all the intact DAOs
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public class DaoFactory
{
    private DaoFactory(){}

    public static <T extends IntactObject> IntactObjectDao<T> getGenericDao(Class<T> entityType)
    {
        HibernateDao.validateEntity(entityType);

        return new IntactObjectDao<T>(entityType, getCurrentSession());
    }

    public static AliasDao getAliasDao()
    {
        return new AliasDao(getCurrentSession());
    }

    private static Session getCurrentSession()
    {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }
}
