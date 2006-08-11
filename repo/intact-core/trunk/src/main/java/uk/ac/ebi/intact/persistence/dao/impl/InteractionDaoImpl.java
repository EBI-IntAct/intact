/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Projections;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>03-May-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class InteractionDaoImpl extends InteractorDaoImpl<InteractionImpl> implements InteractionDao
{
    private static final Log log = LogFactory.getLog(InteractionDaoImpl.class);

    public InteractionDaoImpl(Session session)
    {
        super(InteractionImpl.class, session);
    }

    /**
     * Counts the interactors for an interaction
     * @param interactionAc The interaction accession number to use
     * @return number of distinct interactors
     */
    public Integer countInteractorsByInteractionAc(String interactionAc)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Counting interactors for interaction with ac: "+interactionAc);
        }

        return (Integer) getSession().createCriteria(InteractionImpl.class)
                .add(Restrictions.idEq(interactionAc))
                .createAlias("components", "comp")
                .createAlias("comp.interactor", "interactor")
                .setProjection(Projections.count("interactor.ac")).uniqueResult();
    }

    public List<String> getNestedInteractionAcsByInteractionAc(String interactionAc)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Getting nested interactions for interaction with ac: "+interactionAc);
        }

        return getSession().createCriteria(InteractionImpl.class)
                .add(Restrictions.idEq(interactionAc))
                .createAlias("components", "comp")
                .createAlias("comp.interactor", "interactor")
                .add(Restrictions.eq("interactor.objClass", InteractionImpl.class.getName()))
                .setProjection(Projections.distinct(Projections.property("interactor.ac"))).list();
    }

    public List<Interaction> getInteractionByExperimentShortLabel(String[] experimentLabels, Integer firstResult, Integer maxResults)
    {
        Criteria criteria = getSession().createCriteria(Interaction.class)
                 .createCriteria("experiments")
                 .add(Restrictions.in("shortLabel", experimentLabels));

        if (firstResult != null && firstResult >=0)
        {
            criteria.setFirstResult(firstResult);
        }

        if (maxResults != null && maxResults > 0)
        {
            criteria.setMaxResults(maxResults);
        }

        return criteria.list();
    }
}
