package uk.ac.ebi.intact.tutorial;

import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.model.*;

import org.apache.ojb.broker.accesslayer.LookupException;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.sql.SQLException;


/**
 * @author Anja Friedrichsem (afrie@ebi.ac.uk)
 * @version $Id$
 */
public class QueryTutorial {

    public static void main(String[] args) throws IntactException {

        // the non-argument Intact-Helper
        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
        } catch (IntactException ie) {


        }
//        // the advanced Intact-Helper
//        IntactHelper helper = null;
//        try{
//            DAOSource dataSource = DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");
//            helper = new IntactHelper(dataSource);
//
//        }catch(DataSourceException de){
//            de.printStackTrace();
//        }

        // get user name and db name
        try {
            String user = helper.getDbUserName();
            String dbName = helper.getDbName();
            System.out.println("User '" + user + "' is connected to database: " + dbName + ".");
        } catch (LookupException lupe) {
            lupe.printStackTrace();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }


        //get only one spezific experiment
        Object experiment = helper.getObjectByLabel(Experiment.class, "ho");
        if(experiment != null){
            String ex = experiment.toString();
            System.out.println("EXPERIMENT: " + ex);
        }else{
            System.out.println("Sorry, experiment not found!");
        }

        // get a collection of experiments
        Collection experiments = helper.search(Experiment.class.getName(), "shortlabel", null);
        System.out.println("We have " + experiments.size() + " experiment(s) found.");

        // search experiments interacions and print out the experiments shortlabels
        for (Iterator iterator = experiments.iterator(); iterator.hasNext();) {
            Experiment exp = (Experiment) iterator.next();
            System.out.println("\n EXPERIMENT: " + exp.getShortLabel());
            Collection interactions = exp.getInteractions();

            // search interactions interactors and print out the interactions shortlabel
            for (Iterator iterator1 = interactions.iterator(); iterator1.hasNext();) {
                Interaction interaction = (Interaction) iterator1.next();
                Collection components = interaction.getComponents();
                System.out.println("\t INTERACTION: " + interaction.getShortLabel() + " has " + components.size()
                                    + "participants");

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
