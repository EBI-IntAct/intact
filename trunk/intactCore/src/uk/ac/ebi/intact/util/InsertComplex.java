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
    DAOSource dataSource;
    DAO dao;
    Logger log = null;
    long startTime = System.currentTimeMillis();

    /**
     * basic constructor - sets up (hard-coded) data source and an intact helper
     */
    public InsertComplex() throws Exception {

        dataSource = DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");

        //set the config details, ie repository file for OJB in this case
        Map config = new HashMap();
        config.put("mappingfile", "config/repository.xml");
        dataSource.setConfig(config);

        try {
            dao = dataSource.getDAO();
	    log = dataSource.getLogger();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

             helper = new IntactHelper(dataSource);

         } catch (IntactException ie) {

             //something failed with type map or datasource...
             String msg = "unable to create intact helper class - no datasource";
             System.out.println(msg);
             ie.printStackTrace();
         }

        // Set cached classes
        helper.addCachedClass(CvDatabase.class);
        helper.addCachedClass(Institution.class);
        helper.addCachedClass(Institution.class);
        helper.addCachedClass(Protein.class);
        helper.addCachedClass(CvComponentRole.class);
//        helper.addCachedClass(Experiment.class);
        helper.addCachedClass(Interaction.class);
    }


    /** Add a new xref to an annotatedObject.
     *
     */
    public void addNewXref(AnnotatedObject current,
                           Xref xref)  throws Exception {

        // Todo: Make sure the xref does not yet exist in the object

        current.addXref(xref);
        if (xref.getParentAc() == current.getAc()){
            dao.create(xref);
        }
    }

    public void insertComponent (Interaction act,
                                 String spAc,
                                 CvComponentRole role) throws Exception {
	log.info((System.currentTimeMillis() - startTime) + " ZZ  Insert component");
        Component comp = new Component();
        comp.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
        comp.setInteraction(act);
	log.info((System.currentTimeMillis() - startTime) + " ZZ  Create protein");
        Protein protein = (Protein) helper.getObjectByXref(Protein.class, spAc);
        if (null == protein){
            protein = new Protein();
            protein.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
            protein.setShortLabel(spAc);
            dao.create(protein);
            addNewXref(protein,
                       new Xref ((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                                (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "SPTR"),
                                spAc,
                                null, null, null));
        }
	log.info((System.currentTimeMillis() - startTime) + " ZZ  set interactor");
        comp.setInteractor(protein);
	log.info((System.currentTimeMillis() - startTime) + " ZZ  set role");
        comp.setCvComponentRole(role);
	log.info((System.currentTimeMillis() - startTime) + " ZZ  create comp");
        dao.create(comp);
	
    }


    public void insertComplex (String interactionNumber,
                               String bait,
                               Vector preys,
                               String experimentLabel) throws Exception {

    startTime = System.currentTimeMillis();
	log.info((System.currentTimeMillis() - startTime) + " ZZ  Get Experiment");
        // Get experiment
        Experiment ex = (Experiment) helper.getObjectByLabel(Experiment.class, experimentLabel);
        if (null == ex){
            ex = new Experiment();
            ex.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
            dao.create(ex);
        }

	log.info((System.currentTimeMillis() - startTime) + " ZZ Get Interaction");
        // Get Interaction
        // The label is the first two letters of the experiment label plus the interaction number
        String actLabel = experimentLabel.substring(0,2) + "-" + interactionNumber;
        Interaction act = (Interaction) helper.getObjectByLabel(Interaction.class,actLabel);
        if (null == act){
            act = new Interaction();
            act.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
            act.setShortLabel(actLabel);
            dao.create(act);
        }
	log.info(" ZZ Interaction: \n" + act);
	log.info((System.currentTimeMillis() - startTime) + " ZZ Add bait");
        // add bait
        insertComponent(act, bait, (CvComponentRole) helper.getObjectByLabel(CvComponentRole.class, "bait"));

	log.info((System.currentTimeMillis() - startTime) + " ZZ Add preys");
        // add preys
        for (int i = 0; i < preys.size(); i++) {
            String prey = (String) preys.elementAt(i);
            insertComponent(act, prey, (CvComponentRole) helper.getObjectByLabel(CvComponentRole.class, "prey"));
	log.info((System.currentTimeMillis() - startTime) + " ZZ Added prey" + i);

        }

        // link interaction to experiment
        ex.addInteraction(act);
	log.info((System.currentTimeMillis() - startTime) + " ZZ Added int");

        // Store or update
        dao.update(act);
	log.info((System.currentTimeMillis() - startTime) + " ZZ Updated int");
	//        dao.update(ex);
	log.info((System.currentTimeMillis() - startTime) + " ZZ Updated ex");

    }


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
                app.dao.begin();
                app.insertComplex(interactionNumber, bait, preys, experimentLabel);
                app.dao.commit();
            } catch (Exception ie) {
                System.err.println("\nError: " + ie.getMessage() + "Ignoring:\n" + line);
                app.dao.rollback();
            }

            // Progress report
            if((++lineCount % 1) == 0){
                System.out.print(lineCount + " ");
            }
        }
        System.out.println("\n");
    }
}
