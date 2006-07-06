/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.struts.view.details;

import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;

import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Displays a collection of Protein in the context of their Experiment.
 *
 * @see uk.ac.ebi.intact.model.Protein
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
    public ProteinDetailsViewBean( Collection objects, String link, String contextPath ) {
        super(objects, link, contextPath);
    }


    /**
     * Building the Protein view in the comtext of the wrapped Proteins' Experiment.
     *
     * @param writer the writer to use to write the HTML data out.
     */
    public void getHTML( Writer writer ) {

        Map experiments = new HashMap();

        // build a collection of distinct experiments from the set of Proteins.
        for (Iterator iterator1 = getWrappedObjects().iterator(); iterator1.hasNext();) {
            Protein protein = (Protein) iterator1.next();

            // TODO could be pushed as isOrphan() in the Protein class.
            // We skip all orphan protein.
            Collection activeInstance = protein.getActiveInstances();
            if (activeInstance == null || activeInstance.size() == 0) {
                continue;
            }

            Collection components = protein.getActiveInstances();

//            logger.info("Protein: " + protein.getShortLabel());

            // get all Interaction involving that Protein
            for (Iterator iterator = components.iterator(); iterator.hasNext();) {
                Component component = (Component) iterator.next();
                Interaction interaction = component.getInteraction();
//                logger.info.println("Interaction " + interaction.getShortLabel());
                // get all Experiment involving that interaction

                // keep a copy of distinct Experiment
                for (Iterator iterator2 = interaction.getExperiments().iterator(); iterator2.hasNext();) {
                    Experiment experiment = (Experiment) iterator2.next();
                    // add a copy of the experiment
//                    logger.info("Distinct Experiment: " + experiment.getShortLabel());
                    if ( ! experiments.containsKey( experiment.getAc() )){
                        Experiment ex = Experiment.getShallowCopy( experiment );
                        experiments.put( experiment.getAc(), ex );
                    }
                }
            }
        }


        // build a collection of distinct experiments from the set of Proteins.
        for (Iterator iterator1 = getWrappedObjects().iterator(); iterator1.hasNext();) {
            Protein protein = (Protein) iterator1.next();

            // TODO could be pushed as isOrphan() in the Protein class.
            // We skip all orphan protein.
            Collection activeInstance = protein.getActiveInstances();
            if (activeInstance == null || activeInstance.size() == 0) {
                continue;
            }

            Collection components = protein.getActiveInstances();

            // get all Interaction involving that Protein
            for (Iterator iterator2 = components.iterator(); iterator2.hasNext();) {
                Component component = (Component) iterator2.next();
                Interaction interaction = component.getInteraction();

                Collection int_exps = interaction.getExperiments();
                for ( Iterator iterator3 = int_exps.iterator (); iterator3.hasNext (); ) {
                    Experiment experiment = (Experiment) iterator3.next ();
                    Experiment ex = (Experiment) experiments.get( experiment.getAc() );
                    ex.addInteraction( interaction );
                }
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
