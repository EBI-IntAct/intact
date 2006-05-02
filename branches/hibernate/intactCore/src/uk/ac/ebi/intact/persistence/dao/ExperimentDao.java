/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.Interaction;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>26-Apr-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class ExperimentDao extends AnnotatedObjectDao<Experiment>
{
    public ExperimentDao(Session session)
    {
        super(Experiment.class, session);
    }

    public int countInteractionsForExperimentWithAc(String ac)
    {
//        return (Integer) getSession().createCriteria(Experiment.class)
//                    .add(Restrictions.idEq(ac))
//                    .createAlias("interactions", "int")
//                    .setProjection(Projections.countDistinct("int.ac")).uniqueResult();

        // this one performs slightly better
        return (Integer) getSession().createCriteria(InteractionImpl.class)
                    .createAlias("experiments", "exp")
                    .add(Restrictions.eq("exp.ac", ac))
                    .setProjection(Projections.rowCount()).uniqueResult();
    }

    public List<Interaction> getInteractionsForExpereimentWithAc(String ac, int firstResult, int maxResults)
    {
        return getSession().createCriteria(InteractionImpl.class)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .createCriteria("experiments")
                .add(Restrictions.idEq(ac)).list();
    }
}
