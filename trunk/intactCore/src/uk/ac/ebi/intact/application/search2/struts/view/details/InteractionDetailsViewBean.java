/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.struts.view.details;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionDetailsViewBean extends DetailsViewBean {

    /**
     * Construct an instance of this class for given collection of object.
     *
     * @param objects the collection of objects to construct the view for.
     * @param link the link to the help page.
     * @exception java.lang.NullPointerException thrown if the collection if null or empty.
     */
    public InteractionDetailsViewBean( Collection objects, String link ) {
        super(objects, link);
    }


    public String getHTML() {

        Set experiments = new HashSet();

        // build a collection of distinct experiments from the set of Interactions.
        for (Iterator iterator1 = getWrappedObjects().iterator(); iterator1.hasNext();) {
            Interaction interaction = (Interaction) iterator1.next();
            Collection localExperiments = interaction.getExperiment();
            for (Iterator iterator2 = localExperiments.iterator(); iterator2.hasNext();) {
                Experiment experiment = (Experiment) iterator2.next();
                // add a copy of the experiment
                experiments.add( new Experiment( experiment ) );
            }
        }

        // for each experiment, limit the interactions to the set wrapped in the bean.
        for (Iterator iterator = experiments.iterator(); iterator.hasNext();) {
            Experiment experiment = (Experiment) iterator.next();

            Collection interactions = experiment.getInteraction();
            Collection result = CollectionUtils.intersection(interactions, getWrappedObjects());
            experiment.setInteraction(result);
        }

        // The order is important: initialize the map before resetting the
        // wrapped object or else it will use the short labels of experiments.
        initHighlightMap();
        setWrappedObjects( experiments );

        // sends bach the HTML content
        return super.getHTML();
    }
}
