/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.struts.view.details;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.model.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinDetailsViewBean extends DetailsViewBean {

    /**
     * Construct an instance of this class for given collection of object.
     *
     * @param objects the collection of objects to construct the view for.
     * @param link the link to the help page.
     * @exception java.lang.NullPointerException thrown thrown if the collection if null or empty.
     */
    public ProteinDetailsViewBean( Collection objects, String link ) {
        super(objects, link);
    }

    public String getHTML() {

        Set experiments = new HashSet();

        // build a collection of distinct experiments from the set of Proteins.
        for (Iterator iterator1 = getWrappedObjects().iterator(); iterator1.hasNext();) {
            Protein protein = (Protein) iterator1.next();

            // TODO could be pushed as isOrphan() in the Protein class.
            // We skip all orphan protein.
            Collection activeInstance = protein.getActiveInstance();
            if (activeInstance == null || activeInstance.size() == 0) {
                continue;
            }

            Collection components = protein.getActiveInstance();

//            System.out.println("Protein: " + protein.getShortLabel());

            // get all Interaction involving that Protein
            for (Iterator iterator = components.iterator(); iterator.hasNext();) {
                Component component = (Component) iterator.next();
                Interaction interaction = component.getInteraction();
//                System.out.println("Interaction " + interaction.getShortLabel());
                // get all Experiment involving that interaction

                // keep a copy of distinct Experiment
                for (Iterator iterator2 = interaction.getExperiment().iterator(); iterator2.hasNext();) {
                    Experiment experiment = (Experiment) iterator2.next();
                    // add a copy of the experiment
//                    System.out.println("Distinct Experiment: " + experiment.getShortLabel());
                    experiments.add( new Experiment( experiment ) );
                }
            }
        }

        /* for each experiment, limit the interactions to those involving at least
         * one of the wrapped Protein.
         */
        for (Iterator iterator1 = experiments.iterator(); iterator1.hasNext();) {
            Experiment experiment = (Experiment) iterator1.next();

            Collection interactions = experiment.getInteraction();
            System.out.println("Experiment: " + experiment.getShortLabel());

            /* Check if the interaction involves one of the Protein found.
             * If so, keep the interaction, else don't display it.
             */
            Collection interactionToDisplay = new HashSet( interactions.size() );
            for (Iterator iterator2 = interactions.iterator(); iterator2.hasNext();) {
                Interaction interaction = (Interaction) iterator2.next();
//                System.out.println("Interaction: " + interaction.getShortLabel());

                Collection components = interaction.getComponent();
//                System.out.println( components.size() + " components");
                Collection proteins = new HashSet( components.size() );
                for (Iterator iterator3 = components.iterator(); iterator3.hasNext();) {
                    Component component = (Component) iterator3.next();
                    Interactor interactor = component.getInteractor();
                    if (interactor instanceof Protein) {
                        proteins.add(interactor);
                    }
                }

                Collection result = CollectionUtils.intersection( proteins, getWrappedObjects() );
                if ( false == result.isEmpty() ) {
                    interactionToDisplay.add( interaction );
                }
            } // interaction

            experiment.setInteraction( interactionToDisplay );
        } // experiments

        // The order is important: initialize the map before resetting the
        // wrapped object or else it will use the short labels of experiments.
        initHighlightMap();
        setWrappedObjects( experiments );

        // sends bach the HTML content
        return super.getHTML();
    }
}
