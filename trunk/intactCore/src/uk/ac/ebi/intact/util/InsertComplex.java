/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import java.util.*;
import java.io.*;

import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.model.*;

//as good a logging facility as any other....
import org.apache.ojb.broker.util.logging.Logger;

/**
 * Insert complex data for Ho and Gavin publications.
 * Data is read from an input text file.
 *
 * Input file format:
 * Line records, elements are space-delimited:
 * Interaction Bait   Preys         Experiment
 * number
 * 12          Q05524 P00330 Q05524 gavin
 *
 * @author Henning Hermjakob, hhe@ebi.ac.uk
 */
public class InsertComplex {

    IntactHelper helper;

    /** All proteins which have been created for the current complex.
     */
    HashMap newProteins = new HashMap();
    Logger log = null;

    /**
     * basic constructor - sets up intact helper
     */
    public InsertComplex() throws Exception {

       try {
           helper = new IntactHelper();
       } catch (IntactException ie) {

           //something failed with type map or datasource...
           String msg = "unable to create intact helper class";
           System.out.println(msg);
           ie.printStackTrace();
       }
    }


    /** Add a new xref to an annotatedObject.
     *
     */
    public void addNewXref(AnnotatedObject current,
                           Xref xref)  throws Exception {

        current.addXref(xref);

        /* The temporary xref will only be added to the object
           if it does not yet exist in it.
           Only if it is added it will be made persistent.
        */
        if (xref.getParentAc() == current.getAc()){
            helper.create(xref);
        }
    }

    /**
     * Insert a Component object linking an Interactor to an Interaction.
     *
     * @param act The interaction to add the Interactor to
     * @param spAc Swiss-Prot accession number of the Protein to add.
     *             If the protein does not yet exist, it will be created.
     * @param role Role of the protein in the interaction.
     * @throws Exception
     */
    public void insertComponent (Interaction act,
                                 String spAc,
                                 CvComponentRole role) throws Exception {

        Component comp = new Component();
        comp.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
        comp.setInteraction(act);
        Protein protein = (Protein) helper.getObjectByXref(Protein.class, spAc);

        /* Check if the protein has already been created during the creation of this complex.
           See Note: transaction in main() for documentation.
        */
        if (null == protein){
            protein = (Protein) this.newProteins.get(spAc);
        }

        // If the protein does not exist, create it
        if (null == protein){
            protein = new Protein();
            protein.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
            protein.setShortLabel(spAc);
            helper.create(protein);
            this.newProteins.put(spAc,protein);
            addNewXref(protein,
                       new Xref ((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                                (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "sptr"),
                                spAc,
                                null, null, null));
        }

        // Complete the component
        comp.setInteractor(protein);
        comp.setCvComponentRole(role);
        helper.create(comp);
    }


    /**
     * Inserts a complex into the database
     *
     * @param interactionNumber The number of the interaction in the publication.
     *                          Used for the shortLabel.
     * @param bait Swiss-Prot accession number of the bait protein.
     * @param preys Swiss-Prot accession numbers of the prey proteins.
     * @param experimentLabel The short label of the experiment the complex belongs to.
     * @throws Exception
     */
    public void insertComplex(String interactionNumber,
                              String bait,
                              Vector preys,
                              String experimentLabel) throws Exception {

        // Get experiment
        Experiment ex = (Experiment) helper.getObjectByLabel(Experiment.class, experimentLabel);
        if (null == ex) {
            ex = new Experiment();
            ex.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
            helper.create(ex);
        }

        // Get Interaction
        // The label is the first two letters of the experiment label plus the interaction number
        String actLabel = experimentLabel.substring(0, 2) + "-" + interactionNumber;
        Interaction act = (Interaction) helper.getObjectByLabel(Interaction.class, actLabel);
        if (null == act) {
            act = new Interaction();
            act.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
            act.setShortLabel(actLabel);
            helper.create(act);
        }
        // add bait
        insertComponent(act, bait, (CvComponentRole) helper.getObjectByLabel(CvComponentRole.class, "bait"));

        // add preys
        for (int i = 0; i < preys.size(); i++) {
            String prey = (String) preys.elementAt(i);
            insertComponent(act, prey, (CvComponentRole) helper.getObjectByLabel(CvComponentRole.class, "prey"));
        }

        // link interaction to experiment
        ex.addInteraction(act);

        // Store or update
        helper.update(act);
    }

    /** Read complex data from flat file and insert it into the database.
     *
     * @param args InputFileName
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        InsertComplex app = new InsertComplex();

        // Parse input file line by line

        BufferedReader file = new BufferedReader(new FileReader(args[0]));
        String line;
        Protein protein;
        int lineCount = 0;

        System.out.print("Lines processed: ");

        while (null != (line = file.readLine())) {

            // Tokenize lines
            StringTokenizer st = new StringTokenizer(line);
            String interactionNumber = st.nextToken();
            String bait = st.nextToken();
            Vector preys = new Vector();

            while (st.hasMoreTokens()){
                preys.add(st.nextToken());
            }

            // remove last element from preys vector, it is the experiment identifier.
            String experimentLabel = (String) preys.lastElement();
            preys.removeElement(preys.lastElement());

            // Insert results into database
            try {
                // Start transaction.
                // The transaction range is the interaction.

                /* Note: transaction
                Within one transaction, relational systems provide so-called READ CONSISTENCY.
                This means that the same query always returns the same result within one transaction.
                In this application, this has the follwing effect:
                If a complex contains the same protein more than once, and this protein does
                not yet exist, it will be created when the first occurrence is encoutered.
                Due to the read consistency, the getObjectByXref would return null when the protein
                occurs for the second time and is queried for. As a result, it would be created twice.
                Therefore it is necessary to maintain newProteins, a HashMap listing all proteins
                created in the current transaction.
                */

                app.helper.startTransaction();
                app.newProteins.clear();
                app.insertComplex(interactionNumber, bait, preys, experimentLabel);
                app.helper.finishTransaction();
            } catch (Exception ie) {
                System.err.println("\nError: " + ie.getMessage() + "\nIgnoring:\n" + line);
                app.helper.undoTransaction();
            }

            // Progress report
            if((++lineCount % 1) == 0){
                System.out.print(lineCount + " ");
            }
        }
        System.out.println("\n");
    }
}
