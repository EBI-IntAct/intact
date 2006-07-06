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


/**
 * Create GO crossreferences for Protein objects.
 * Data is read from an input text file.
 *
 * Input file format:
 * Line records, elements are tab-delimited
 * SP AC    SGD SGD AC      Systematic  GO ac       References                  GO
 *                          name from                                           evidence
 *                          SGD
 * P04710	SGD	S0004660	AAC1		GO:0005743	SGD_REF:12031|PMID:2167309	TAS		C	ADP/ATP translocator	YMR056C	gene	taxon:4932	20010118
 *
 *
 * @author Henning Hermjakob, hhe@ebi.ac.uk
 */
public class InsertGo {

    IntactHelper helper;
    DAOSource dataSource;
    DAO dao;

    /**
     * basic constructor - sets up (hard-coded) data source and an intact helper
     */
    public InsertGo() throws Exception {

        dataSource = DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");

        //set the config details, ie repository file for OJB in this case
        Map config = new HashMap();
        config.put("mappingfile", "config/repository.xml");
        dataSource.setConfig(config);

        try {
            dao = dataSource.getDAO();
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

    /** Insert or update a protein object.
     *  Set the shortLabel,
     *  add SP ac, SGD ac, GO term(s) and Pubmed IDs as crossreferences.
     */
    public Protein updateProtein(String shortLabel,
                                 String spAc,
                                 String sgdAc,
                                 String goAc,
                                 String pubmedAc) throws Exception {

        // Retrieve the protein if it already exists.
        // Criterion: Same Swiss-Prot xref.

        Protein protein = null;
        Xref spXref = null;

        spXref = new Xref((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                          (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "SPTR"),
                          spAc,
                          null, null);

        try {
            protein = (Protein) helper.getObjectByXref(Protein.class, spAc);
        }
        catch (IntactException e) {
            System.err.println("Error retrieving Protein object for " + spAc
                    + ". Ignoring associated crossreferences. ");
            e.printStackTrace();
            return null;
        }

        if (null == protein){
            protein = new Protein();
            protein.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
            protein.setShortLabel(shortLabel);
            dao.create(protein);
            addNewXref(protein,
                       new Xref ((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                                (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "SPTR"),
                                spAc,
                                null, null));
        } else {
            // set the short label in any case
            protein.setShortLabel(shortLabel);
        }

        // Now we have a valid protein object, complete it.

        addNewXref(protein,
                   new Xref((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                            (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "SGD"),
                            sgdAc,
                            null, null));
        addNewXref(protein,
                    new Xref((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                             (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "GO"),
                             goAc,
                             null, null));
        return protein;
    }


    public static void main(String[] args) throws Exception {

        InsertGo app = new InsertGo();

        // Parse input file line by line

        BufferedReader file = new BufferedReader(new FileReader(args[0]));
        String line;
        Protein protein;
        int lineCount = 0;

        System.out.print("Lines processed: ");

        while (null != (line = file.readLine())) {

            // Tokenize lines
            StringTokenizer st = new StringTokenizer(line, "\t", false);
            String spAc = st.nextToken();
            String dummy = st.nextToken();
            String sgdAc = st.nextToken();
            String label = st.nextToken();
            String goAc = st.nextToken();
            String bibReference = st.nextToken();
            String pubmedId = null;

            // Parse the bibliographic reference
            StringTokenizer bibTokenizer
                    = new StringTokenizer(bibReference,
                            "|", false);
            dummy = bibTokenizer.nextToken();
            if (bibTokenizer.hasMoreTokens()) {
                pubmedId = bibTokenizer.nextToken();
            } else {
                pubmedId = null;
            }

            // Insert results into database
            protein = app.updateProtein(label, spAc, sgdAc, goAc, null);

            // Progress report
            if((++lineCount % 10) == 0){
                System.out.print(lineCount + " ");
            }
        }
        System.out.println("\n");
    }
}
