/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.InteractorImpl;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27-Apr-2006</pre>
 */
public class InteractorDao<T extends InteractorImpl> extends AnnotatedObjectDao<T>
{
    public InteractorDao(Class<T> entityClass, Session session)
    {
        super(entityClass, session);
    }

    public int countInteractionsForInteractorWithAc(String ac)
    {
        return (Integer) getSession().createCriteria(Component.class)
                    .createAlias("interactor", "interactor")
                    .createAlias("interaction", "interaction")
                    .add(Restrictions.eq("interactor.ac", ac))
                    .setProjection(Projections.countDistinct("interaction.ac")).uniqueResult();
    }

    public Collection test()
    {
        return getSession().createCriteria(Component.class)
                    .createAlias("interactor", "interactor")
                    .createAlias("interaction", "interaction")
                    .setProjection(Projections.projectionList()
                        .add(Projections.countDistinct("interaction.ac").as("dist"))
                        .add(Projections.groupProperty("interaction.ac")))
                    .add(Restrictions.ge("dist", 1)).list();
    }
}
