/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.util.InteractionUtils;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>03-May-2006</pre>
 */
@SuppressWarnings( {"unchecked"} )
public class InteractionDaoImpl extends InteractorDaoImpl<InteractionImpl> implements InteractionDao {

    private static final Log log = LogFactory.getLog( InteractionDaoImpl.class );

    public InteractionDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( InteractionImpl.class, entityManager, intactSession );
    }

    /**
     * Counts the interactors for an interaction
     *
     * @param interactionAc The interaction accession number to use
     *
     * @return number of distinct interactors
     */
    public Integer countInteractorsByInteractionAc( String interactionAc ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Counting interactors for interaction with ac: " + interactionAc );
        }

        return ( Integer ) getSession().createCriteria( InteractionImpl.class )
                .add( Restrictions.idEq( interactionAc ) )
                .createAlias( "components", "comp" )
                .createAlias( "comp.interactor", "interactor" )
                .setProjection( Projections.count( "interactor.ac" ) ).uniqueResult();
    }

    public List<String> getNestedInteractionAcsByInteractionAc( String interactionAc ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Getting nested interactions for interaction with ac: " + interactionAc );
        }

        return getSession().createCriteria( InteractionImpl.class )
                .add( Restrictions.idEq( interactionAc ) )
                .createAlias( "components", "comp" )
                .createAlias( "comp.interactor", "interactor" )
                .add( Restrictions.eq( "interactor.objClass", InteractionImpl.class.getName() ) )
                .setProjection( Projections.distinct( Projections.property( "interactor.ac" ) ) ).list();
    }

    public List<Interaction> getInteractionByExperimentShortLabel( String[] experimentLabels, Integer firstResult, Integer maxResults ) {
        Criteria criteria = getSession().createCriteria( Interaction.class )
                .createCriteria( "experiments" )
                .add( Restrictions.in( "shortLabel", experimentLabels ) );

        if ( firstResult != null && firstResult >= 0 ) {
            criteria.setFirstResult( firstResult );
        }

        if ( maxResults != null && maxResults > 0 ) {
            criteria.setMaxResults( maxResults );
        }

        return criteria.list();
    }

    public List<Interaction> getInteractionsByInteractorAc( String interactorAc ) {
        return getSession().createCriteria( Interaction.class )
                .createAlias( "components", "comp" )
                .createAlias( "comp.interactor", "interactor" )
                .add( Restrictions.eq( "interactor.ac", interactorAc ) ).list();
    }

    public List<Interaction> getInteractionsForProtPair( String protAc1, String protAc2 ) {
        Query query = getSession().createQuery( "SELECT i FROM InteractionImpl AS i, Component AS c1, Component AS c2 " +
                                                "WHERE i.ac = c1.interactionAc AND i.ac = c2.interactionAc AND " +
                                                "c1.interactorAc = :protAc1 AND c2.interactorAc = :protAc2" );

        query.setParameter( "protAc1", protAc1 );
        query.setParameter( "protAc2", protAc2 );

        return query.list();
    }

    public Collection<Interaction> getSelfBinaryInteractionsByProtAc( String protAc ) {
        List<Interaction> interactions = getInteractionsByInteractorAc( protAc );

        Set<Interaction> selfInteractions = new HashSet<Interaction>();

        for ( Interaction inter : interactions ) {
            boolean isSelfInteraction = InteractionUtils.isSelfBinaryInteraction( inter );

            if ( isSelfInteraction ) {
                selfInteractions.add( inter );
            }
        }

        return selfInteractions;
    }
}
