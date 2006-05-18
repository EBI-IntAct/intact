/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.imex.helpers;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
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

    public static boolean isAccepted( IntactHelper helper, Experiment experiment ) {

        CvTopic accepted = null;

        try {
            accepted = helper.getObjectByLabel( CvTopic.class, CvTopic.ACCEPTED );
        } catch ( IntactException e ) {
            System.out.println( "Could not find CvTopic( " + CvTopic.ACCEPTED + " ). Hence the experiment was not accepted." );
            return false;
        }

        if ( experiment == null ) {
            throw new IllegalArgumentException( "Experiment must not be null." );
        }

        if ( helper == null ) {
            throw new IllegalArgumentException( "IntactHelper must not be null." );
        }

        // checking if the experiment was accepted.
        for ( Annotation annotation : experiment.getAnnotations() ) {
            if ( accepted.equals( annotation.getCvTopic() ) ) {
                return true;
            }
        }

        return false;
    }

    public static boolean areAccepted( IntactHelper helper, Collection<Experiment> experiments, boolean verbose ) {
        boolean allAccepted = true;

        // Check that all of these experiments have been accepted
        for ( Experiment experiment : experiments ) {

            boolean accepted = ExperimentHelper.isAccepted( helper, experiment );

            if ( verbose ) {
                if ( ! accepted ) {
                    System.out.println( experiment.getShortLabel() + " was NOT accepted." );
                } else {
                    System.out.println( experiment.getShortLabel() + " was accepted." );
                }
            }

            allAccepted = accepted && allAccepted;
        }

        if ( ! allAccepted ) {
            if ( verbose ) {
                System.out.println( "Not all experiment were accepted. abort." );
            }
        }
        return allAccepted;
    }

    /**
     * Answer the following question: "has the interaction of the given expriemnt got an IMEx ID ?".
     *
     * @param experiment the experiment to check on
     * @param imex
     * @param verbose
     *
     * @return
     */
    public static boolean everyInteractionHasImexId( IntactHelper helper, Experiment experiment, boolean verbose ) {

        boolean allInteractionHaveId = true;

        for ( Iterator iterator = experiment.getInteractions().iterator(); iterator.hasNext(); ) {
            Interaction interaction = (Interaction) iterator.next();

            if ( ! InteractionHelper.hasIMExId( helper, interaction ) ) {
                allInteractionHaveId = false; // that interaction failed, exit here.

                if ( verbose ) {
                    System.out.println( "Interaction[ac=" + interaction.getAc() + ", shortlabel" +
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
}