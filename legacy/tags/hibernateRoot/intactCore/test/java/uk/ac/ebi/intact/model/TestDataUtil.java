/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods for the creation of test data objects
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>22-Mar-2006</pre>
 */
public class TestDataUtil
{
    private TestDataUtil(){}

    public static BioSource createBioSource(IntactHelper helper, String shortLabel, String taxID) throws IntactException {
        BioSource organism = new BioSource(helper.getInstitution(), shortLabel, taxID);
        organism.setFullName("Organism created for JUnit testing purposes");

        helper.create(organism);

        return organism;
    }

    public static BioSource getBioSource(IntactHelper helper, String label) throws IntactException{
         return helper.getObjectByLabel(BioSource.class, label);
    }

    public static void deleteBioSource(IntactHelper helper, String label) throws IntactException{
         helper.delete(helper.getObjectByLabel(BioSource.class, label));
    }

    public static Experiment createExperiment(IntactHelper helper, String shortLabel, BioSource organism) throws IntactException {
        Experiment experiment = new Experiment(helper.getInstitution(), shortLabel, organism);
        experiment.setFullName("Experiment created for JUnit testing purposes");

        helper.create(experiment);

        return experiment;
    }

    public static void deleteExperiment(IntactHelper helper, String label) throws IntactException{
         helper.delete(getExperiment(helper, label));
    }

    public static Experiment getExperiment(IntactHelper helper, String label) throws IntactException{
         return helper.getObjectByLabel(Experiment.class, label);
    }

    public static Interaction createInteraction(IntactHelper helper, Experiment experiment, String shortLabel) throws IntactException {
        Collection<Experiment> experiments = new ArrayList<Experiment>();
        experiments.add(experiment);

        return createInteraction(helper, experiments, shortLabel);
    }

    public static Interaction createInteraction(IntactHelper helper, Collection<Experiment> experiments, String shortLabel) throws IntactException {
        List components = new ArrayList();
        CvInteractionType type =  helper.getObjectByLabel(CvInteractionType.class, "aggregation");
        CvInteractorType interactorType =  helper.getObjectByLabel(CvInteractorType.class, "protein" );

        Interaction interaction = new InteractionImpl(experiments, components, type, interactorType, shortLabel, helper.getInstitution());
        interaction.setFullName("Interaction created for JUnit testing purposes");

        helper.create(interaction);

        return interaction;
    }

    public static void deleteInteraction(IntactHelper helper, String label) throws IntactException{
         helper.delete(getInteraction(helper, label));
    }

    public static Interaction getInteraction(IntactHelper helper, String label) throws IntactException{
         return helper.getObjectByLabel(Interaction.class, label);
    }

    public static Protein createProtein(IntactHelper helper, BioSource bioSource, String label) throws IntactException{
        CvInteractorType interactorType =  helper.getObjectByLabel(CvInteractorType.class, "protein" );

        Protein protein = new ProteinImpl(helper.getInstitution(), bioSource, label,interactorType);
        protein.setFullName("Protein created for JUnit testing purposes");

        helper.create(protein);

        return protein;
    }

    public static void deleteProtein(IntactHelper helper, String label) throws IntactException{
         helper.delete(getProtein(helper, label));
    }

    public static Protein getProtein(IntactHelper helper, String label) throws IntactException{
         return helper.getObjectByLabel(Protein.class, label);
    }

}
