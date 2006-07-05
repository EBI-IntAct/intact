/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.persistence.dao.impl.IntactObjectDaoImpl;
import uk.ac.ebi.intact.persistence.dao.XrefDao;
import org.hibernate.Session;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>03-May-2006</pre>
 */
public class XrefDaoImpl extends IntactObjectDaoImpl<Xref> implements XrefDao
{
    public XrefDaoImpl(Session session)
    {
        super(Xref.class, session);
    }

    public Collection<Xref> getByPrimaryId(String primaryId)
    {
        return getColByPropertyName("primaryId", primaryId);
    }

    public Collection<Xref> getByPrimaryIdLike(String primaryId)
    {
        return getByPropertyNameLike("primaryId", primaryId);
    }
}
