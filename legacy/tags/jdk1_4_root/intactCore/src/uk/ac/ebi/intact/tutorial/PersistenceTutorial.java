/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/


package uk.ac.ebi.intact.tutorial;

import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.*;

import java.util.*;
import java.sql.SQLException;

import org.apache.ojb.broker.accesslayer.LookupException;

/**
 * This class gives an overview how to create,
 * update and delete objects in the IntAct database
 *
 * @author Anja Friedrichsen
 * @version $id$
 */
public class PersistenceTutorial {

    private IntactHelper helper;
    private Institution owner;
    private BioSource organism;
    private Experiment experiment;
    private Interaction interaction;
    private Component component1;
    private Component component2;
    private Component component3;

    public PersistenceTutorial(IntactHelper helper) {
        this.helper = helper;
    }

    /**
     * creates a BioSource
     *
     * @param owner  institution which owns the BioSource
     * @param shortLabel string that identifies the BiSource
     * @param taxID  NCBI taxID
     * @return
     * @throws IntactException
     */
    private BioSource createBioSource(Institution owner, String shortLabel, String taxID) throws IntactException {
        organism = new BioSource(owner, shortLabel, taxID);
        helper.create(organism);
        return organism;
    }

    /**
     * creates an Experiment
     *
     * @param owner institution that owns that experiment
     * @param shortLabel  string that identifies the experiment
     * @param organism  organism of the experimental data
     * @throws IntactException
     */
    private void createExperiment(Institution owner, String shortLabel, BioSource organism) throws IntactException {
        experiment = new Experiment(owner, shortLabel, organism);
        helper.create(experiment);
    }

    /**
     * creates an interaction
     *
     * @param experiment evidence for that interaction
     * @param shortLabel identifier of that interaction
     * @param owner institution which owns the interaction
     * @throws IntactException
     */
    private void createInteraction(Experiment experiment, String shortLabel, Institution owner) throws IntactException {
        Collection experiments = new ArrayList();
        experiments.add(experiment);
        Collection components = new ArrayList();
        CvInteractionType type = (CvInteractionType) helper.getObjectByLabel(CvInteractionType.class, "aggregation");
        interaction = new InteractionImpl(experiments, components, type, shortLabel, owner);
        helper.create(interaction);
    }

    /**
     *  creates interactors
     *  getting the role and creating components
     *
     * @param owner institution which owns the interaction
     * @param organism  the organism which belongs that interactor
     * @param interaction interaction the interactor takes part in
     * @throws IntactException
     */
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

    /**
     * updates the interaction
     *
     * @throws IntactException
     */
    private void updateInteraction() throws IntactException {
        // add the components to the existing interaction
        interaction.addComponent(component1);
        interaction.addComponent(component2);
        interaction.addComponent(component3);

        // update the interaction
        helper.update(interaction);
    }

    /**
     * updates IntAct with proteins from other databases like UniProt
     *
     * @param proteinAc Accession number to specify the protein
     * @throws UpdateProteinsI.UpdateException
     *
     */
    private void importProtein(String proteinAc) throws UpdateProteinsI.UpdateException {
        UpdateProteinsI proteinFactory = new UpdateProteins(helper);
        proteinFactory.insertSPTrProteins(proteinAc);
    }


    /**
     * creates an institution, a bioSource, an experiment, an interaction and interactors
     * updates the interaction and
     * imports a protein from UniProt
     *
     * @throws UpdateProteinsI.UpdateException
     *
     */
    private void insertData() throws UpdateProteinsI.UpdateException {
        try {
            owner = helper.getInstitution();
            organism = createBioSource(owner, "drosophila", "7215");
            createExperiment(owner, "tutorial", organism);
            createInteraction(experiment, "interaction", owner);
            createInteractors(owner, organism, interaction);
            updateInteraction();
            importProtein("P21181");

        } catch (IntactException ie) {

            ie.printStackTrace();

            if (ie.getRootCause() != null) {
                ie.printStackTrace();
            }
        }
    }

    /**
     * deletes an experiment specified by the shortlabel
     *
     * @param shortlabel  the shortlabel of the experiment which should be deleted
     * @throws IntactException
     */
    private void deleteExperiment(String shortlabel) throws IntactException {
        Collection experiments = helper.search(Experiment.class.getName(), "shortlabel", shortlabel);
        for (Iterator iterator = experiments.iterator(); iterator.hasNext();) {
            Experiment exp = (Experiment) iterator.next();
            helper.delete(exp);
        }
    }

    /**
     * deletes an interaction specified by the shortlabel
     *
     * @param shortlabel the shortlabel of the interaction which should be deleted
     * @throws IntactException
     */
    private void deleteInteraction(String shortlabel) throws IntactException {
        Collection interactions = helper.search(Interaction.class.getName(), "shortlabel", shortlabel);
        for (Iterator iterator = interactions.iterator(); iterator.hasNext();) {
            Interaction interaction = (Interaction) iterator.next();
            helper.delete(interaction);
        }
    }


    /**
     * delete a interactor (or more) specified by the shortlabel
     *
     * @param shortlabel  the shortlabel of the interactor which should be deleted
     * @throws IntactException
     */
    private void deleteInteractor(String shortlabel) throws IntactException {
        Collection interactors = helper.search(Interactor.class.getName(), "shortlabel", shortlabel);
        for (Iterator iterator = interactors.iterator(); iterator.hasNext();) {
            Interactor interactor = (Interactor) iterator.next();
            helper.delete(interactor);
        }
    }

    /**
     * deletes the experiment 'tutorial', the interaction 'interaction' and
     * the proteins where the shortlabel starts with 'protein'
     *
     * @throws IntactException
     */
    private void deleteData() throws IntactException {
        deleteExperiment("tutorial");
        deleteInteraction("interaction");
        deleteInteractor("protein*");
    }

    /**
     * inserts the data and deletes it right after
     *
     * @param args
     * @throws IntactException
     * @throws UpdateProteinsI.UpdateException
     *
     */
    public static void main(String[] args) throws IntactException, UpdateProteinsI.UpdateException {

        IntactHelper helper = null;

        try {
            helper = new IntactHelper();

            // get database information
            String user = helper.getDbUserName();
            String dbName = helper.getDbName();
            System.out.println("User " + user + " is connected to database: " + dbName + ".");

            PersistenceTutorial tutorial = new PersistenceTutorial( helper );

            //insert data
            tutorial.insertData();

            // and delete the data again
            tutorial.deleteData();

        } catch (LookupException lupe) {
            lupe.printStackTrace();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {

            if( helper != null ) {
                helper.closeStore();
            }
        }
    }
}