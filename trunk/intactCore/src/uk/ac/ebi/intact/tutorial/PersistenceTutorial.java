package uk.ac.ebi.intact.tutorial;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.UpdateProteinsI;
import uk.ac.ebi.intact.util.UpdateProteins;

import java.util.*;

/**
 * @author Anja Friedrichsen (afrie@ebi.ac.uk)
 * @version $Id$
 */
public class PersistenceTutorial {

    IntactHelper helper;
    Institution owner;
    BioSource organism;
    Experiment experiment;
    Interaction interaction;
    Component component1;
    Component component2;
    Component component3;

    // create an Institution
    private Institution createInstitution(String shortlabel) throws IntactException {
        owner = new Institution(shortlabel);
        helper.create(owner);
//      owner = helper.getInstitution();
        return owner;
    }

    // create a BioSource
    private BioSource createBioSource(Institution owner, String shortLabel, String taxID) throws IntactException {
        organism = new BioSource(owner, shortLabel, taxID);
        helper.create(organism);
        return organism;
    }

    // create an Experiment
    private void createExperiment(Institution owner, String shortLabel, BioSource organism) throws IntactException {
        experiment = new Experiment(owner, shortLabel, organism);
        helper.create(experiment);
    }

    // create an Interaction
    private void createInteraction(Experiment experiment, String shortLabel, Institution owner) throws IntactException {
        Collection experiments = new ArrayList();
        experiments.add(experiment);
        Collection components = new ArrayList();
        CvInteractionType type = (CvInteractionType) helper.getObjectByLabel(CvInteractionType.class, "aggregation");
        interaction = new InteractionImpl(experiments, components, type, shortLabel, owner);
        helper.create(interaction);
    }

    // create Interactors
    private void createInteractors(Institution owner, BioSource organism, Interaction interaction) throws IntactException {
        Protein protein1 = new ProteinImpl(owner, organism, "protein1");
        Protein protein2 = new ProteinImpl(owner, organism, "protein2");
        Protein protein3 = new ProteinImpl(owner, organism, "protein3");

        helper.create(protein1);
        helper.create(protein2);
        helper.create(protein3);

        // create CvComponentRole's
        CvComponentRole bait = (CvComponentRole) helper.getObjectByLabel(CvComponentRole.class, "bait");
        CvComponentRole prey = (CvComponentRole) helper.getObjectByLabel(CvComponentRole.class, "prey");

        //create components
        component1 = new Component(owner, interaction, protein1, prey);
        component2 = new Component(owner, interaction, protein2, prey);
        component3 = new Component(owner, interaction, protein3, bait);

        helper.create(component1);
        helper.create(component2);
        helper.create(component3);
    }

    // update the interaction
    private void updateInteraction() throws IntactException {

        // add the components to the existing interaction
        interaction.addComponent(component1);
        interaction.addComponent(component2);
        interaction.addComponent(component3);

        // update the interaction
        helper.update(interaction);
    }

    private void updateProtein(String proteinAc) throws UpdateProteinsI.UpdateException{
        UpdateProteinsI proteinFactory = new UpdateProteins(helper);
        proteinFactory.insertSPTrProteins(proteinAc);
    }

    // create an institution, a bioSource, an experiment, an interaction and interactors
    // update the interaction
    private void insertData() throws UpdateProteinsI.UpdateException {
        try {
            owner = createInstitution("ebi");
            organism = createBioSource(owner, "drosophila", "7215");
            createExperiment(owner, "tutorial", organism);
            createInteraction(experiment, "interaction", owner);
            createInteractors(owner, organism, interaction);
            updateInteraction();
            updateProtein("P21181");

        } catch (IntactException ie) {

            ie.printStackTrace();

            if (ie.getRootCause() != null) {
                ie.printStackTrace();
            }
        }
    }

    // delete the experiment
    private void deleteExperiment(String shortlabel) throws IntactException {
        Collection experiments = helper.search(Experiment.class.getName(), "shortlabel", shortlabel);
        for (Iterator iterator = experiments.iterator(); iterator.hasNext();) {
            Experiment exp = (Experiment) iterator.next();
            helper.delete(exp);
        }
    }

    // delete the interaction
    private void deleteInteraction(String shortlabel) throws IntactException {
        Collection interactions = helper.search(Interaction.class.getName(), "shortlabel", shortlabel);
        for (Iterator iterator = interactions.iterator(); iterator.hasNext();) {
            Interaction interaction = (Interaction) iterator.next();
            helper.delete(interaction);
        }
    }

    // delete the interactors
    private void deleteInteractor(String shortlabel) throws IntactException {
        Collection interactors = helper.search(Interactor.class.getName(), "shortlabel", shortlabel);
        for (Iterator iterator = interactors.iterator(); iterator.hasNext();) {
            Interactor interactor = (Interactor) iterator.next();
            helper.delete(interactor);
        }
    }

    // delete the experiment, the interaction and all proteins
    private void deleteData() throws IntactException {
        deleteExperiment("tutorial");
        deleteInteraction("interaction");
        deleteInteractor("protein*");
    }


    public static void main(String[] args) throws IntactException, UpdateProteinsI.UpdateException {

        PersistenceTutorial tutorial = new PersistenceTutorial();
        try {
            tutorial.helper = new IntactHelper();
            //insert data
            tutorial.insertData();
            // and delete the data again
            tutorial.deleteData();
        } finally {
            tutorial.helper.closeStore();
        }

    }
}
