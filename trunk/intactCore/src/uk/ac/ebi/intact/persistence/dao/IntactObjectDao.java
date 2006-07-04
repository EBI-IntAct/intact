/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.IntactObject;

import java.util.List;
import java.util.Collection;

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

    Collection<T> getColByPropertyName(String propertyName, String value);

    void update(T objToUpdate);
}
