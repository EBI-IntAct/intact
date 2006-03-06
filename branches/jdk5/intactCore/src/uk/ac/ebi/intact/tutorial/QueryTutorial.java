/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/

package uk.ac.ebi.intact.tutorial;

import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.model.*;

import org.apache.ojb.broker.accesslayer.LookupException;

import java.util.*;
import java.sql.SQLException;
import java.io.*;

/**
 *
 * @author Anja Friedrichsen
 * @version $id$
 */
public class QueryTutorial {

    /**
     * This class gives you an overview how to get information out of the database
     *
     * @param args
     * @throws IntactException
     */
    public static void main(String[] args) throws IntactException {

        // the non-argument Intact-Helper
        IntactHelper helper = null;
        helper = new IntactHelper();

        // get user name and db name
        try {
            String user = helper.getDbUserName();
            String dbName = helper.getDbName();
            System.out.println("User " + user + " is connected to database: " + dbName + ".");
        } catch (LookupException lupe) {
            lupe.printStackTrace();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }


        //get only one specific experiment
        Object experiment = helper.getObjectByLabel(Experiment.class, "ho");
        if (experiment != null) {
            String ex = experiment.toString();
            System.out.println("EXPERIMENT: " + ex);
        } else {
            System.out.println("Sorry, experiment not found!");
        }

        // ask for a specific experiment
        String str = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please specify the name of the experiment (or press Enter to get all experiments): ");
            str = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String expStr = "*" + str + "*";
        // get a collection of experiments with the specified name
        Collection experiments = helper.search(Experiment.class, "shortlabel", expStr);

        // check if there is an experiment with that name
        // if not the list of available experiment are given
        if (experiments.size() == 0) {
            System.out.println("You have entered the experiment:  " + str);
            System.out.println("Unfortunately it is not in your database!");
            System.out.println("The following experiments are in your database: ");
            Collection allExperiments = helper.search(Experiment.class, "shortlabel", null);
            for (Iterator iterator = allExperiments.iterator(); iterator.hasNext();) {
                Experiment exp = (Experiment) iterator.next();
                System.out.println("\t" + exp.getShortLabel());
            }
        } else {
            System.out.println(experiments.size() + " experiment(s) found.");

            // search experiments interacions and print out the experiments shortlabels
            for (Iterator iterator = experiments.iterator(); iterator.hasNext();) {
                Experiment exp = (Experiment) iterator.next();
                Collection interactions = exp.getInteractions();
                System.out.println("\n EXPERIMENT: " + exp.getShortLabel() + " has " + interactions.size() + " interaction(s)");

                // search interactions interactors and print out the interactions shortlabel
                for (Iterator iterator1 = interactions.iterator(); iterator1.hasNext();) {
                    Interaction interaction = (Interaction) iterator1.next();
                    Collection components = interaction.getComponents();
                    System.out.println("\t INTERACTION: " + interaction.getShortLabel() + " has " + components.size()
                            + " participants");

                    for (Iterator iterator2 = components.iterator(); iterator2.hasNext();) {
                        Component component = (Component) iterator2.next();
                        Interactor interactor = component.getInteractor();

                        String type = null;
                        if (interactor instanceof Protein) {
                            type = "PROTEIN";
                        } else if (interactor instanceof Interaction) {
                            type = "INTERACTION";
                        } else {
                            type = interactor.getClass().getName();
                        }
                        System.out.println("\t\t " + type + "\t" +
                                interactor.getShortLabel() +
                                "\t Role:" +
                                component.getCvComponentRole().getShortLabel());
                    }
                }
            }
        }
    }
}
