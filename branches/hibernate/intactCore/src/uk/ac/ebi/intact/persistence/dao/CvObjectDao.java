/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import uk.ac.ebi.intact.model.CvObject;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02-May-2006</pre>
 */
public class CvObjectDao<T extends CvObject> extends AnnotatedObjectDao<T>
{
    public CvObjectDao(Class<T> entityClass, Session session)
    {
        super(entityClass, session);
    }
}
