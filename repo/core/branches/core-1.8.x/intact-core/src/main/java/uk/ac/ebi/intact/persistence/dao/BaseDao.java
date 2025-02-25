/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import uk.ac.ebi.intact.annotation.Mockable;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Base DAO, which any DAO has to implement
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
@Mockable
public interface BaseDao<T> {

    Session getSession();

    void flushCurrentSession();

    String getDbName() throws SQLException;

    String getDbUserName() throws SQLException;

    List<T> getAll();

    Iterator<T> getAllIterator();

    List<T> getAll( int firstResult, int maxResults );

    public int countAll();

    void update( T objToUpdate );

    void persist( T objToPersist );

    void persistAll( Collection<T> objsToPersist );

    void delete( T objToDelete );

    void deleteAll( Collection<T> objsToDelete );

    void saveOrUpdate( T objToPersist );

    void refresh( T obj );

    void evict( T objToEvict );

    /**
     * Persist the state of the given detached instance, reusing the current identifier value. This operation cascades
     * to associated instances if the association is mapped with cascade="replicate".
     * @param objToReplicate
     */
    void replicate( T objToReplicate );

    /**
     * Persist the state of the given detached instance, reusing the current identifier value. This operation cascades
     * to associated instances if the association is mapped with cascade="replicate".
     * @param objToReplicate
     * @param ignoreIfExisting
     */
    void replicate(T objToReplicate, boolean ignoreIfExisting);

    void merge( T objToReplicate );

    boolean isTransient(T object);

}
