/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;

import java.util.Collection;
import java.util.List;

/**
 * Dao to play with CVs
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02-May-2006</pre>
 */
@SuppressWarnings("unchecked")
public class CvObjectDaoImpl<T extends CvObject> extends AnnotatedObjectDaoImpl<T> implements CvObjectDao<T>
{
    public CvObjectDaoImpl(Class<T> entityClass, Session session, IntactSession intactSession)
    {
        super(entityClass, session, intactSession);
    }

     public List<T> getByPsiMiRefCollection(Collection<String> psiMis)
    {
       return getSession().createCriteria(getEntityClass()).createAlias("xrefs","xref")
               .createAlias("xref.cvDatabase", "cvDb")
               .createAlias("cvDb.xrefs", "cvDbXref")
                .add(Restrictions.eq("cvDbXref.primaryId", CvDatabase.PSI_MI_MI_REF))
                .add(Restrictions.in("xref.primaryId", psiMis)).list();
    }
}
