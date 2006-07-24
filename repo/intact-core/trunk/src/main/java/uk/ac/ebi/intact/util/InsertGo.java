/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.CreateException;
import uk.ac.ebi.intact.persistence.UpdateException;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;


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

    private static final Log log = LogFactory.getLog(InsertGo.class);

    public static final int MAX_ID_LEN = 30;

    HashMap goTerms;

    /**
     * basic constructor - sets up (hard-coded) data source and an intact helper
     */
    public InsertGo() throws Exception {

        //set the config details, ie repository file for OJB in this case
        //Map config = new HashMap();
        //config.put("mappingfile", "config/repository.xml");
        //dataSource.setConfig(config);

        goTerms = new HashMap();

    }


    /** Add a new xref to an annotatedObject.
     *
     */
    public void addNewXref( AnnotatedObject current, Xref xref ) throws CreateException {

        // Todo: Make sure the xref does not yet exist in the object

        current.addXref( xref );
        if (xref.getParentAc().equals(current.getAc())){
            DaoFactory.getXrefDao().persist( xref );
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
                                 String pubmedAc) throws IntactException, CreateException, UpdateException {

        // Retrieve the protein if it already exists.
        // Criterion: Same Swiss-Prot xref.

        Protein protein = null;
        // TODO: why do we need that for ?
        //Xref spXref = new Xref(helper.getInstitution(),
        //                  DaoFactory.getCvObjectDao(CvDatabase.class).getByShortLabel( CvDatabase.UNIPROT),
        //                  spAc,
        //                  null, null, null);

        try {
            protein = DaoFactory.getProteinDao().getByXref(spAc);
        }
        catch (IntactException e) {
            log.error("Error retrieving Protein object for " + spAc
                               + ". Ignoring associated crossreferences. ");
            e.printStackTrace();
            return null;
        }

        if (null == protein){

            //IMPORTANT! This needs fixing for the new model, as to create
            //a Protein we need a BioSource!! See InsertComplex for details on
            //getting one from newt....
//            protein = new Protein();
//            protein.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
//            protein.setShortLabel(shortLabel);
//            dao.create(protein);
//            addNewXref(protein, spXref);
        } else {
            // set the short label in any case
            protein.setShortLabel(shortLabel);
        }

        // Now we have a valid protein object, complete it.
        Institution institution = DaoFactory.getInstitutionDao().getInstitution();

        addNewXref(protein,
                   new InteractorXref(institution,
                            DaoFactory.getCvObjectDao(CvDatabase.class).getByShortLabel( "sgd"),
                            sgdAc,
                            null, null, null));
        // Get GO term
        String goTerm = (String) this.goTerms.get(goAc);
        if (null != goTerm){
            goTerm = goTerm.substring(0,Math.min(goTerm.length(), MAX_ID_LEN));
        }


        addNewXref(protein,
                   new InteractorXref(institution,
                            DaoFactory.getCvObjectDao(CvDatabase.class).getByShortLabel( "go"),
                            goAc,
                            goTerm, null, null));
        if (DaoFactory.getProteinDao().exists((ProteinImpl)protein)){
            DaoFactory.getProteinDao().update((ProteinImpl)protein);
        }
        return protein;
    }

    /** Reads a GO definition file from a URL.
     *
     */
    public HashMap parseGoDefs(String aUrl){

        String goId = null;
        String goTerm = null;
        HashMap<String,String> goMap = new HashMap<String,String>();
        BufferedReader in     = null;
        InputStreamReader isr = null;

        try {
            // Parse input line by line
            URL goServer = new URL(aUrl);
            isr = new InputStreamReader( goServer.openStream() );
            in  = new BufferedReader( isr );


            String line;
            int lineCount = 0;

            log.error("GO definition file lines processed: ");

            RE idPat = new RE("goid: (GO:\\d+)");
            RE termPat =  new RE("term: (.*)");


            while (null != (line = in.readLine())) {

                // Progress report
                if((++lineCount % 1000) == 0){
                    System.err.print(lineCount + " ");
                }

                // parse GoId
                if (idPat.match(line)){
                    goId = idPat.getParen(1);
                    continue;
                }

                // parse term
                if (termPat.match(line)){
                    goTerm = termPat.getParen(1);
                    continue;
                }

                // empty line marks end of record, save current record
                if(line.length() == 0){
                    if ((null != goId) && (null != goTerm)){
                        goMap.put(goId,goTerm);
                    }
                    goId=null;
                    goTerm=null;
                }

                // ignore comments and all other lines by doing nothing
            }
        }
                // The GO terms are not essential, if anything fails,
                // just catch the exception.
        catch (IOException e) {
            e.printStackTrace();
        } catch ( RESyntaxException e ) {
            e.printStackTrace ();
        } finally {
            // close opened streams.
            if(isr != null) {
                try {
                    isr.close();
                } catch( IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if( in != null ){
                try {
                    in.close();
                } catch( IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return goMap;
    }




    public static void main( String[] args ) throws Exception {

        if( args.length < 2 ) {
            log.error ( "Usage: InsertGo " );
        }


        InsertGo app = new InsertGo();

        // parse GO definition file
        if (null != args[ 1 ]){
            app.goTerms = app.parseGoDefs( args[ 1 ] );
        }

        // Parse input file line by line

        FileReader fr = null;
        BufferedReader file = null;
        try {
            fr   = new FileReader( args[ 0 ] );
            file = new BufferedReader( fr );

            String line;
            int lineCount = 0;

            System.out.print("Lines processed: ");

            while (null != (line = file.readLine())) {

                // Tokenize lines
                StringTokenizer st = new StringTokenizer(line, "\t", false);
                String spAc = st.nextToken();
                st.nextToken();
                String sgdAc = st.nextToken();
                String label = st.nextToken();
                String goAc = st.nextToken();
                String bibReference = st.nextToken();
                //String pubmedId = null;

                // Parse the bibliographic reference
                StringTokenizer bibTokenizer = new StringTokenizer( bibReference, "|", false );
                bibTokenizer.nextToken();

                /*
                if (bibTokenizer.hasMoreTokens()) {
                    pubmedId = bibTokenizer.nextToken();
                } else {
                    pubmedId = null;
                } */

                // Insert results into database
                app.updateProtein(label, spAc, sgdAc, goAc, null);

                // Progress report
                if((++lineCount % 10) == 0){
                    System.out.print(lineCount + " ");
                }
            }
            log.debug("\n");
        }
        finally {
            // close opened streams.
            if(file != null) {
                try {
                    file.close();
                } catch( IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if( fr != null ){
                try {
                    fr.close();
                } catch( IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }
}
