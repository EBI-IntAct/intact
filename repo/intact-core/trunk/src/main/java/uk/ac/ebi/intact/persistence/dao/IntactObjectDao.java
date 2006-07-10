/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.IntactObject;

import java.util.List;
import java.util.Collection;

import org.hibernate.ScrollableResults;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
public interface IntactObjectDao<T extends IntactObject>
{
    T getByAc(String ac);

    Collection<T> getByAcLike(String ac);

    List<T> getByAc(String[] acs);

    List<T> getByAc(Collection<String> acs);

    List<T> getAll();

    List<T> getAll(int firstResult, int maxResults);

    Collection<T> getColByPropertyName(String propertyName, String value);

    void update(T objToUpdate);

    void persist(T objToPersist);

    void persistAll(Collection<T> objsToPersist);

    void delete(T objToDelete);

    void deleteAll(Collection<T> objsToDelete);

    void saveOrUpdate(T objToPersist);

    boolean exists(T obj);

    void refresh(T objToRefresh);
}
