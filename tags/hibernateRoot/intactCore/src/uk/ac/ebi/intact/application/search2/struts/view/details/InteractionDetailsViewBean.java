/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.struts.view.details;

import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Displays a collection of Interactions in the context of their Experiment.
 *
 * @see uk.ac.ebi.intact.model.Interaction
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
    public InteractionDetailsViewBean( Collection objects, String link, String contextPath ) {
        super(objects, link, contextPath);
    }


    public void getHTML( Writer writer ) {

        HashMap experiments = new HashMap();

        // build a collection of distinct experiments from the set of Interactions.
        for (Iterator iterator1 = getWrappedObjects().iterator(); iterator1.hasNext();) {
            Interaction interaction = (Interaction) iterator1.next();
            Collection localExperiments = interaction.getExperiments();
            for (Iterator iterator2 = localExperiments.iterator(); iterator2.hasNext();) {
                Experiment experiment = (Experiment) iterator2.next();

                // If the experiment is not yet processed, add a copy to the current set
                Experiment shallowCopy = (Experiment) experiments.get(experiment.getAc());
                if (null == shallowCopy){
                    shallowCopy = Experiment.getShallowCopy( experiment );
                    experiments.put(experiment.getAc(), shallowCopy);
                }

                // add the interaction to the shallow experiment
                shallowCopy.addInteraction(interaction);
            }
        }

        // The order is important: initialize the map before resetting the
        // wrapped object or else it will use the short labels of experiments.
        initHighlightMap();
        setWrappedObjects( experiments.values() );

        // write the HTML content
        super.getHTML( writer );
    }
}
