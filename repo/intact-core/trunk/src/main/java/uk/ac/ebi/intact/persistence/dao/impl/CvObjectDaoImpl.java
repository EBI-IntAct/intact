/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.persistence.dao.impl.AnnotatedObjectDaoImpl;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;

import java.util.List;

/**
 * Dao to play with CVs
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


    /**
     * Gets all the CVs for the current entity
     * @param excludeObsolete if true exclude the obsolete CVs
     * @param excludeHidden if true exclude the hidden CVs
     * @return the list of CVs
     */
    public List<T> getAll(boolean excludeObsolete, boolean excludeHidden)
    {
        Criteria crit = getSession().createCriteria(getEntityClass());

        if (excludeObsolete || excludeHidden)
        {
            crit.createAlias("annotations", "annot")
                .createAlias("annot.cvTopic", "annotTopic");
        }

        if (excludeObsolete && excludeHidden)
        {
            crit.add(Restrictions.disjunction()
                    .add(Restrictions.ne("annotTopic.shortLabel", CvTopic.OBSOLETE))
                    .add(Restrictions.ne("annotTopic.shortLabel", CvTopic.HIDDEN)));
        }
        else if (excludeObsolete && !excludeHidden)
        {
            crit.add(Restrictions.ne("annotTopic.shortLabel", CvTopic.OBSOLETE));
        }
        else if (!excludeObsolete && excludeHidden)
        {
            crit.add(Restrictions.ne("annotTopic.shortLabel", CvTopic.HIDDEN));
        }

        return crit.list();
    }
}
