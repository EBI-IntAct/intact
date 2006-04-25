/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import org.apache.ojb.broker.accesslayer.LookupException;

import java.sql.SQLException;

import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.NotAnEntityException;

import javax.persistence.Entity;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public abstract class HibernateDao
{
    private Session session;

    public HibernateDao(Session session)
    {
       this.session = session;
    }

    protected Session getSession()
    {
        return session;
    }

    /**
     * Provides the database name that is being connected to.
     *
     * @return String the database name, or an empty String if the query fails
     */
    public String getDbName() throws SQLException
    {
        return session.connection().getMetaData().getDatabaseProductName();
    }

    /**
     * Provides the user name that is connecting to the DB.
     *
     * @return String the user name, or an empty String if the query fails
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
    public static <T extends IntactObject> void validateEntity(Class<T> entity)
    {
        if (entity.getAnnotation(Entity.class) == null)
        {
            throw new NotAnEntityException(entity);
        }
    }
}
