/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.business;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

//import uk.ac.ebi.intact.business.IntactHelper;
//import uk.ac.ebi.intact.business.IntactException;

import uk.ac.ebi.intact.application.intSeq.struts.framework.SeqIdConstants;
import uk.ac.ebi.intact.application.intSeq.business.ManagerFilesSrs;
import uk.ac.ebi.intact.application.intSeq.business.CallingSrsIF;
import uk.ac.ebi.intact.application.commons.business.IntactUserI;
import uk.ac.ebi.intact.business.IntactException;
import org.apache.log4j.Logger;

/**
 * This class allows to retrieve AC and Description fields from the SRS engine, with the SPTr database.
 * So, it executes first the Wgetz command to call SRS, with the user request. Results are record in a file.
 * The first request returns a list of AC and Description.
 * Afterwards, the second request returns one sequence which corresponds to an AC.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class CallingSrs implements CallingSrsIF{

    static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);


    /**
     * Inner class to generate unique ids to use primary keys for CommentBean
     * class.
     */
    private static class UniqueID {

        /**
         * The initial value. All the unique ids are based on this value for any
         * (all) user(s).
         */
        private static long theirCurrentTime = System.currentTimeMillis();

        /**
         * Returns a unique id using the initial seed value.
         */
        private static synchronized long get() {
            return theirCurrentTime++;
        }
    }


    // --------- INSTANCE VARIABLES --------------//

    protected IntactUserI theUser = null;

    /**
     * protein feature which has been captured by the user
     */
    protected String proteinTopic = "";

    protected String srsCommand = "";

    /**
     * the first idea is that there will be none error during the SRS running
     */
    protected boolean srsError = false;

    protected ManagerFilesSrs resultWgetzQuery = null;

    //protected IntactHelper myHelper;
    protected boolean matchWithIntactId = false;


    //-------------- CONSTRUCTORS ------------------//

    /**
     * constructor by default
     */
    public CallingSrs () {

    }

    /**
     * constructor which allows to check if the protein topic exists in IntAct. (later)
     *
     * @param id String which represents a protein feature (protein reference or simply a word)
     */
    public CallingSrs (String id) {
        this.proteinTopic = id;
    }

    /**
     * constructor which allows to call the SRS engine in case of lack of the request in IntAct
     *
     * @param proteintopic String which represents a protein feature (protein reference or simply a word)
     * @param command String the Wgetz command to make a request on SRS
     */
    public CallingSrs (String proteintopic, String command) {
        this.proteinTopic = proteintopic;
        this.srsCommand = command;
    }

    //------------- PROPERTIES ----------------//

    /**
     * Returns the unique id based on the current time; the ids are unique
     * for a session.
     */
    public static long getId() {
        return UniqueID.get();
    }

    /**
     * later, a method will allow to look for an IntAct entry from a protein topic
     * (no SRS process in this case)
     */
    public boolean GetBooleanIntactId () {
        return (this.matchWithIntactId);
    }


    /**
     * Wgetz execution + parsing of the WGETZ output file
     *
     * @return a list of Accession Number and Description got from the SRS request
     */
    public ArrayList RetrieveAccDes () throws IntactException {

        this.ExecFinalCommand(true);

//        if (srsError == false) {
//             parsing
            ArrayList acDes = resultWgetzQuery.ResultParsingTableFile (SeqIdConstants.PARSING_TABLE_WGETZ_AC);
            resultWgetzQuery.DeleteFile();

            return acDes;
//        }
//        else
//            return null;
    }


    /**
     * Wgetz execution + parsing of the WGETZ output file
     * get the sequence one by one in a fasta format string because the SequenceSimilarityAction
     * requires a protein sequence in a String format (don't forget that the Fasta Format is required)
     *
     * @return a list of Accession Number and Description got from the SRS request
     */
    public String GetSequenceFasta () throws IntactException {

        this.ExecFinalCommand (false);

        if (srsError == false) {
            String sequence = resultWgetzQuery.ReadingFile();
            resultWgetzQuery.DeleteFile();

            if ( (sequence.startsWith(">") == true) )
                return sequence;
            /*  the +-vn+4 wgetz command shouldn't be used...
            // to put the srs format sequence into a fasta format.
            else if ( (sequence.startsWith("<PRE>") == true) ) {   // && (sequence.endsWith("<\\PRE>")) ) {
            int len = sequence.length();
            String fastaSequence = sequence.substring(5, len-7);
            return fastaSequence;
            }
            */
            else
                return null;
        }
        else
            return null;

    }


    //------------ PROTECTED METHOD ----------------//


    /**
     * This method decides which command will be executed (Wgetz or Getz)
     * and put the file in the provided directory: to organize the
     * numerous generated files into two separate directories.
     *
     * @param acOrSeqRequest the boolean which will put the generated file in the good directory.
     *        true means that it is the AC-Description result file
     *
     */
    protected void ExecFinalCommand (boolean acOrSeqRequest) throws IntactException {

        if (srsCommand.startsWith("http") == true) {

            if (acOrSeqRequest == true)
                this.WebSrs (SeqIdConstants.DIR_OUTPUT_WGETZ);
            else
                this.WebSrs (SeqIdConstants.DIR_OUTPUT_WGETZ_SEQ);

        } else {
            logger.error ("Invalid SRS URL: " + srsCommand);
            throw new IntactException ("Invalid SRS URL: " + srsCommand);
        }

        /*
        else {
        //  for the moment, the only command is Wgetz.
        this.GetzSrs();
        }
        */
    }


    /**
     * This method manages the execution of the Wgetz URL.
     *
     * @param path the string which will add the good extension for the temporary file
     *        of which name is rendered randomly.
     *
     *
     */
    protected void WebSrs (String path) throws IntactException {

        try {
            URL wgetzUrl = new URL (srsCommand);
            URLConnection srsConnection = wgetzUrl.openConnection();

            BufferedReader in = new BufferedReader
                    (new InputStreamReader (srsConnection.getInputStream()));
            String inputLine = "";
            resultWgetzQuery = new ManagerFilesSrs (path, (Object)".srs");

            if (resultWgetzQuery.CreateFile() == true) {
                // reading this web page content
                resultWgetzQuery.CreateWriter();

                // allows to put the web page content in a file
                while ((inputLine = in.readLine()) != null) {
                    if ( inputLine.matches("<TITLE>SRS\\serror</TITLE>") == true ) {
                        srsError = true;
                    }
                    resultWgetzQuery.PutInFile(inputLine);
                }
                in.close();
                // to protect this file in writing
                resultWgetzQuery.inputFile.setReadOnly();
            } else {
                logger.error ("Unable to create the output file to store the SRS query result");
                throw new IntactException ("Unable to create the output file to store the SRS query result");
            }
        }
        catch (MalformedURLException mue) {
            logger.error ("SRS URL is malformed: " + srsCommand, mue);
            resultWgetzQuery.DeleteFile();
            srsError = true;
            throw new IntactException ("SRS URL is malformed: " + srsCommand, mue);
        }
        catch (IOException ioe) {
            logger.error ("Error when retreiving SRS response: " + ioe.getMessage(), ioe);
            resultWgetzQuery.DeleteFile();
            srsError = true;
            throw new IntactException ("Error when retreiving SRS response: " + ioe.getMessage(), ioe);
        }
    }

    /*
    // when SRS is called  by the getz command

    // il faut faire pointer cette classe sur RunSimilaritySearch !!!
    protected boolean GetzSrs () {
    // result : getz '[{sptrembl}-alltext:userId]' -f "id acc des seq"
    // if we want to capture the result into an output file
    //srsCommande with proteinTopic in parameter
    //biojava class to execute an Unix commande line...

    // make the output file
    outputGetzFile = new ManagerFilesSrs (SeqIdConstants.DIR_OUTPUT_GETZ);
    String getzCommand = srsCommand.concat(outputGetzFile.GetPathFile());

    Runtime rt = Runtime.getRuntime();

    try{
    Process child = rt.exec(getzCommand);

    // wait for the end of the blast process, can be long if the database is big.
    child.waitFor();

    // any program needs this file anymore
    //resultQueryFile.DeleteFile();

    //test if the process has been finished in a right way
    if (child.exitValue() == 0) {
    return true;
    }
    else {
    return false;
    }
    }
    catch (InterruptedException ie) {
    System.err.println (ie);
    return false;
    }
    catch (NullPointerException npe) {  // if command is null
    System.err.println (npe);
    return false;
    }
    catch (SecurityException se) {
    System.err.print(se);
    return false;
    }
    catch (RuntimeException re) {
    System.err.print(re);
    return false;
    }
    catch (IOException io) {
    System.err.println(io);
    return false;
    }
    }

    */

}
