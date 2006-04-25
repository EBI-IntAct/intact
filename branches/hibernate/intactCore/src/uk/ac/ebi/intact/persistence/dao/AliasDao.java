/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import uk.ac.ebi.intact.model.Alias;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public class AliasDao extends IntactObjectDao<Alias>
{
    public AliasDao(Session session)
    {
        super(Alias.class, session);
    }

    public Collection<Alias> getByNameLike(String name)
    {
        return getByPropertyNameLike("name", name);
    }
}
