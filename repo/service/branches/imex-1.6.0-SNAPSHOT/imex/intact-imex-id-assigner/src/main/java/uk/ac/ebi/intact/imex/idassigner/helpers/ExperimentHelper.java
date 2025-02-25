/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.imex.idassigner.helpers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persistence.dao.ExperimentDao;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

import java.util.Collection;
import java.util.Iterator;

/**
 * Utility methods for an Experiment.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-May-2006</pre>
 */
public class ExperimentHelper {

    public static final Log log = LogFactory.getLog( ExperimentHelper.class );

    public static boolean isAccepted( Experiment experiment ) {

        CvTopic accepted = null;

        try {
            accepted = CvHelper.getAccepted();
        } catch ( IntactException e ) {
            log.debug( "Could not find CvTopic( " + CvTopic.ACCEPTED + " ). Hence the experiment was not accepted." );
            return false;
        }

        if ( experiment == null ) {
            throw new IllegalArgumentException( "Experiment must not be null." );
        }

        // checking if the experiment was accepted.
        for ( Annotation annotation : experiment.getAnnotations() ) {
            if ( accepted.equals( annotation.getCvTopic() ) ) {
                return true;
            }
        }

        return false;
    }

    public static boolean areAccepted( Collection<Experiment> experiments, boolean verbose ) {
        boolean allAccepted = true;

        // Check that all of these experiments have been accepted
        for ( Experiment experiment : experiments ) {

            boolean accepted = ExperimentHelper.isAccepted( experiment );

            if ( verbose ) {
                if ( ! accepted ) {
                    log.debug( experiment.getShortLabel() + " was NOT accepted." );
                } else {
                    log.debug( experiment.getShortLabel() + " was accepted." );
                }
            }

            allAccepted = accepted && allAccepted;
        }

        if ( ! allAccepted ) {
            if ( verbose ) {
                log.debug( "Not all experiment were accepted. abort." );
            }
        }
        return allAccepted;
    }

    /**
     * Answer the following question: "has the interaction of the given expriemnt got an IMEx ID ?".
     *
     * @param experiment the experiment to check on
     * @param verbose
     *
     * @return
     */
    public static boolean everyInteractionHasImexId( Experiment experiment, boolean verbose ) {

        boolean allInteractionHaveId = true;

        for ( Iterator iterator = experiment.getInteractions().iterator(); iterator.hasNext(); ) {
            Interaction interaction = (Interaction) iterator.next();

            if ( ! InteractionHelper.hasIMExId( interaction ) ) {
                allInteractionHaveId = false; // that interaction failed, exit here.

                if ( verbose ) {
                    log.debug( "Interaction[ac=" + interaction.getAc() + ", shortlabel" +
                                        interaction.getShortLabel() + "] of experiment [" +
                                        experiment.getShortLabel() + "] doesn't have an IMEx ID." );
                } else {
                    // we don't need to display everything, there's not point to keep going.
                    return allInteractionHaveId;
                }
            }
        }

        return allInteractionHaveId;
    }

    public static Collection<Experiment> searchByPrimaryReference( String pmid) {

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        ExperimentDao edao = daoFactory.getExperimentDao();

        return edao.getByXrefLike( CvHelper.getPubmed(), CvHelper.getPrimaryReference(), pmid );
    }
}