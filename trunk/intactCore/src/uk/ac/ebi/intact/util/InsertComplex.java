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
import uk.ac.ebi.intact.util.UpdateProteinsI;

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
    UpdateProteins proteinFactory;

    /** All proteins which have been created for the current complex.
     */
    HashMap createdProteins = null;

    Logger log = null;

    /**
     * basic constructor - sets up intact helper and protein factory
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

       try {
           proteinFactory = new UpdateProteins(helper);

           // Transactions are controlled by this class, not by UpdateProteins.
           // Set local transaction control to false.
           proteinFactory.setLocalTransactionControl(false);

       } catch (UpdateProteinsI.UpdateException e) {

           //something failed with type map or datasource...
           String msg = "unable to create protein factory";
           System.out.println(msg);
           e.printStackTrace();
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
     * @param taxId The tax Id of the target proteins.
     * @param role Role of the protein in the interaction.
     * @throws Exception
     */
    public void insertComponent (Interaction act,
                                 String spAc,
                                 String taxId,
                                 CvComponentRole role) throws Exception {

        Component comp = new Component();
        comp.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
        comp.setInteraction(act);

        Collection proteins = null;

        // The relevant proteins might already have been created for the current complex.
        if (createdProteins.containsKey(spAc)){
            proteins = (Collection) createdProteins.get(spAc);
        } else {
            proteins = helper.getObjectsByXref(Protein.class, spAc);

            if (0 == proteins.size()) {
                // * If the protein does not exist, create it
                System.err.print("P");

                // if it is an sptr protein, create a full protein object
                proteins.addAll(proteinFactory.insertSPTrProteins(spAc));

                // if it looks like an sgd protein, create it with an xref to sgd
                if ((0 == proteins.size()) && (spAc.substring(0, 1).equals("S"))) {
                    proteins.add(proteinFactory.insertSimpleProtein(spAc,
                            (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "sgd"),
                            taxId));
                }

            } else {
                System.err.print("p");
            }

            // Save the created proteins for further use
            createdProteins.put(spAc, proteins);
        }
        Protein targetProtein = null;

        // Filter for the correct protein
        for (Iterator i = proteins.iterator(); i.hasNext();) {
            Protein tmp = (Protein) i.next();
            if (tmp.getBioSource().getTaxId().equals(taxId)) {
                if (null == targetProtein) {
                    targetProtein = tmp;
                } else {
                    throw new IntactException("More than one target protein found for: " + spAc);
                }
            }
        }

        if (null == targetProtein) {
            throw new IntactException("No target protein found for: " + spAc);
        }

        // Complete the component
        comp.setInteractor(targetProtein);
        comp.setCvComponentRole(role);
        helper.create(comp);
    }


    /**
     * Inserts a complex into the database
     * If the complex already exists, it is skipped!
     *
     * @param interactionNumber The number of the interaction in the publication.
     *                          Used for the shortLabel.
     * @param bait Swiss-Prot accession number of the bait protein.
     * @param preys Swiss-Prot accession numbers of the prey proteins.
     * @param experimentLabel The short label of the experiment the complex belongs to.
     * @param taxId The tax id of the target proteins.
     * @throws Exception
     */
    public void insertComplex(String interactionNumber,
                              String bait,
                              Vector preys,
                              String taxId,
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

            // Initialise list of proteins created
            createdProteins = new HashMap();

            // add bait
            insertComponent(act, bait, taxId, (CvComponentRole) helper.getObjectByLabel(CvComponentRole.class, "bait"));

            // add preys
            for (int i = 0; i < preys.size(); i++) {
                String prey = (String) preys.elementAt(i);
                insertComponent(act, prey, taxId, (CvComponentRole) helper.getObjectByLabel(CvComponentRole.class, "prey"));
            }

            // link interaction to experiment
            ex.addInteraction(act);

            // No need to do an update here because we have created a new Interaction.
            // In fact, it is an error to do so because you can only update objects that
            // are already in the DB.
//                helper.update(act);
            System.err.print("C");
        } else {
            System.err.print("c");
        }
        // Only update if the object exists in the database. Since
        // the transaction is outside this method, do nothing for creation as it
        // is handled upon committing the transaction.
        if (helper.isPersistent(ex)) {
            helper.update(ex);
        }
    }

    /** Read complex data from flat file and insert it into the database.
     *
     * @param args[0] InputFileName
     * @param args[1] taxid. The tax id of the target proteins.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        InsertComplex app = new InsertComplex();

        if (args.length != 2) {
            System.err.println("Usage: InsertComplex complexFileName targetTaxId");
            return;
        }

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
		        app.helper.startTransaction( BusinessConstants.OBJECT_TX );
                app.insertComplex(interactionNumber, bait, preys, args[1], experimentLabel);
		        app.helper.finishTransaction();
            } catch (Exception ie) {
                System.err.println();
                System.err.println("Error while processing input line: ");
                System.err.println(line);
                System.err.println(ie.getMessage());
            }

            // Progress report
            if((++lineCount % 1) == 0){
                System.out.print(lineCount + " ");
            } else {
                System.out.println(".");
            }
        }
        System.out.println("\n");
    }
}
