/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.persistence.dao.impl.AnnotatedObjectDaoImpl;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02-May-2006</pre>
 */
public class CvObjectDaoImpl<T extends CvObject> extends AnnotatedObjectDaoImpl<T> implements CvObjectDao<T>
{
    public CvObjectDaoImpl(Class<T> entityClass, Session session)
    {
        super(entityClass, session);
    }
}
