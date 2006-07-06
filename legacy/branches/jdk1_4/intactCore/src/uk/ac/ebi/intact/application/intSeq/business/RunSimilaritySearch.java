/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.business;

import java.io.*;
import java.util.ArrayList;

import uk.ac.ebi.intact.application.intSeq.struts.framework.SeqIdConstants;
import uk.ac.ebi.intact.application.intSeq.business.ManagerFilesBlast;
import uk.ac.ebi.intact.application.intSeq.business.RunSimilaritySearchIF;
import uk.ac.ebi.intact.business.IntactException;
import org.apache.log4j.Logger;


/**
 * This class manages the multiple alignment algorithm command line, defined in
 * the web.xml file (it can be wu-blast, ncbi-blast or fasta).
 * It makes two different <code>ManagerFiles</code>:
 *                      * the input file which contains the query protein sequence.
 *                      * the output file which contains the result.
 * The parsing method required for this kind of output file is called.
 * These two files are randomly created, and afterwards, they are deleted because this
 * web application is intended to be multi-user.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class RunSimilaritySearch implements RunSimilaritySearchIF{

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


    //------ INSTANCE VARIABLES ----//

    /**
     * command line required to manage the blast program.
     */
    protected String command = "";

    /**
     * protein sequence written in the input file
     */
    protected String sequence = "";

    /**
     * E-Value initialized at "0" to make a test afterwards.
     */
    protected double evalueMin = 0;

    /**
     * the both ManagerFiles
     *         -> 1 input file and 1 output file for each execution of BLAST or FASTA
     */
    protected ManagerFilesBlast fileInput = null;
    protected ManagerFilesBlast fileOutput = null;

    /**
     * Test if the command line is a BLAST or FASTA program
     */
    boolean blast = false;
    /**
     * Test if the command line has been well done
     */
    boolean commandExecution = false;


    //------ CONSTRUCTOR ---------//

//    /**
//     * constructor by default
//     */
//    public RunSimilaritySearch () {
//    }
//
//    /**
//     * constructor which retrieves the blast command line
//     * and the sequence to run with (protein sequence needs to be in a Fasta format).
//     *
//     * @param seq String the protein sequence in the Fasta format
//     * @param command String the multiple alignment program will be launched by this command line
//     *
//     */
//    public RunSimilaritySearch (String seq, String command) {
//        this.sequence = seq;
//        this.command = command;
//    }

    /**
     * constructor which retrieves the Evalue minimum, required to validate the results,
     * in addition to the command line and the protein sequence.
     *
     * @param seq String the protein sequence in the Fasta format
     * @param command String the multiple alignment program will be launched by this command line
     * @param eValue double remind the maximum E-Value allowed in the result file
     *
     */
    public RunSimilaritySearch (String seq, String command, double eValue) {
        this.sequence = seq;
        this.command = command;
        this.evalueMin = eValue;
    }


    //------ PROPERTIES -------//

    /**
     * Returns the unique id based on the current time; the ids are unique
     * for a session.
     */
    public static long getId() {
        return UniqueID.get();
    }


    public String getSequence() {
        return (this.sequence);
    }

    public void setSequence (String seq) {
        this.sequence = seq;
    }

    /**
     * keep inform the Action Class whether the command line was executed well or not.
     *
     * @return the boolean answer.
     */
    public boolean GetCommandExecResponse () {
        return (this.commandExecution);
    }



    //------ PUBLIC METHODS -------//

    /**
     * In case of a good blast execution, the output blast file would be parsed.
     * return the file which contains all output file results detected by the parsing.
     *
     * @return the results in a list of list.
     */
    public ArrayList RetrieveParseResult () throws IntactException {

        // retrieve the parsing result list
        ArrayList listResults = new ArrayList();

        // indicate the column of the EValue in the table, when the tabular format is used.
        int indexEvalue = -1;

        // create input and output files, and try to execute the command line.
        commandExecution = this.ExecBlast();

        if (commandExecution == false) {
            throw new IntactException ("Could not run Blast");
        }

        if (fileOutput.inputFile.length() <= 0) {
            throw new IntactException ("Blast produce a file with no content");
        }

        if (fileOutput.inputFile.canRead() == false) {
            throw new IntactException ("The output file produce by Blast is not in read access");
        }


        ArrayList theOptions = new ArrayList ();
        // options "-m 9" or "-m 8" in the command line mean that the output file
        // format is a table, easier to parse.
        theOptions.add("8");
        theOptions.add("9");

        // looking for the "-m 8" or "-m 9" options in the command line = tabular format
        boolean optionLooked = this.SearchOptionTab ("\\s", theOptions);
        if (optionLooked == true) {

            logger.info("Found option '-m 8' or '-m 9'.");

            if (blast == true) {
                logger.info("Try to parse BLAST result");

                /* when the tabular output blast file is used (-m 9 ncbi or -m 9 fasta)
                 *
                 * # Fields: Query id, Subject id, % identity, alignment length, mismatches, gap openings, q. start, q. end, s. start, s. end, e-value, bit score
                 * sptrembl:Q9BQD2 sptrembl:Q9BQD2 100.00  679     0       0       1       679     1       679     0.0     1337.0
                 * sptrembl:Q9BQD2 sptrembl:Q8WWS8 40.14   715     356     17      1       650     1       708     1e-125  438.7
                 *          ------                 -----                           -       ---     -       ---     ------
                 */

                listResults = fileOutput.ResultParsingTableFile(SeqIdConstants.PARSING_TABLE_BLAST_FILE);

                /*
                 * example (second line)
                 * listResults = list of ArrayList with 7 columns -> | Q9BQD2 | 40.14 | 1 | 650 | 1 | 708 | 1e-125 |
                 */

                // the E-Value is in the column 7
                indexEvalue = 6;
            }
            else {
                logger.info("Try to parse FASTA result");

                /*
                 * when the tabular output FASTA file is used (-m 9 ncbi or -m 9 fasta):
                 *
                 * the best scores are:                                       opt bits E(55)       %_id  %_gid   sw  alen  mn0  mx0  mn1  mx1 gapq gapl
                 * sptrembl:Q9BQD2 Q9BQD2; Signal transducer and act  ( 679) 4524 1231       0     1.000 1.000 4524  679    1  679    1  679   0   0
                 * sptrembl:Q8WWS8 Q8WWS8; Partial STAT5B signal tra  ( 787) 1443  398 5.1e-113    0.410 0.459 1548  717    1  650    1  708  67   9
                 *          ------                                                     --------    -----                    -  ---    -  ---
                 */

                listResults = fileOutput.ResultParsingTableFile(SeqIdConstants.PARSING_TABLE_FASTA_FILE);

                /*
                 * example (second line)
                 * listResults = list of ArrayList with 7 columns -> | Q8WWS8 | 5.1e-113 | 0.41 | 1 | 650 | 1 | 708 |
                 */

                // the E-Value is in the column 1
                indexEvalue = 1;
            }
        }
        else {
            //when the standard output blast file is used (-m 0 ncbi or wu-blast output)
            // more comments about the ouput format
            // in the ResultParsing method of the ManagerFilesBlast class
            ArrayList patternList = new ArrayList ();
            //to pass the pattern list in parameter
            patternList.add(SeqIdConstants.PARSING_ID);
            patternList.add(SeqIdConstants.PARSING_PERCENTAGE);
            patternList.add(SeqIdConstants.PARSING_BEGIN_FRAGMENT_QUERY);
            patternList.add(SeqIdConstants.PARSING_BEGIN_FRAGMENT_SUBJECT);
            patternList.add(SeqIdConstants.PARSING_END_FRAGMENT_QUERY);
            patternList.add(SeqIdConstants.PARSING_END_FRAGMENT_SUBJECT);
            // "1" because we want to retrieve one item and not the whole line.
            listResults = fileOutput.ResultParsing(patternList, 1);
        }

        // no program needs this file anymore.
        fileOutput.DeleteFile();

        // To test if the EValue is smaller than our constant defined in the web.xml file.
        // Then the E-Value is removed from the list since it is not displayed for the user.
        listResults = this.ParamTest(indexEvalue, listResults);

        //listResults is a list of ArrayList (with 6 items for each)
        return listResults;
    }



    //------ PROTECTED METHODS -------//

    /**
     *      <code>FillSeqFileConcatCommand</code>
     * 1) creates and full the input file, and also complete the command line.
     * 2) creates the output file, and also complete the command line.
     *      <code>Runtime</code>
     * 3) processes the command line created.
     *      When Runtime.exec() isn't, the subprocess might be block or deadlock:
     *      if the program launched produces output, ensure that the <code>InputStream</code>
     *      is immediately processed.
     *
     * @return the results in a list of list.
     */
    protected boolean ExecBlast () throws IntactException {

        /*
         * These two methods allow to format the command line to execute.
         */
        this.FillSeqFileConcatCommand (SeqIdConstants.DIR_INPUT_BLAST, true); // input
        this.FillSeqFileConcatCommand (SeqIdConstants.DIR_OUTPUT_BLAST, false); // output

        //retrieves the current Java Runtime Environment
        Runtime rt = Runtime.getRuntime();

        try{
            //Process returned to the JVM
            logger.info ("Execution of: " + command);
            Process child = rt.exec(command);

            // screen output management
            InputStream stdin = child.getInputStream();
            OutputProcessManagement(stdin, true);

            InputStream stdin_error = child.getErrorStream();
            OutputProcessManagement(stdin_error, true);

            OutputStream stdout = child.getOutputStream();
            OutputProcessManagement(stdout, false);

            // wait for the end of the blast process, 0 if it has been well done.
            // sometimes, process block, and even deadlock
            int exitValue = child.waitFor();

            logger.info("Exit value: " + exitValue);

            // any program needs this file anymore
            fileInput.DeleteFile();

            //test if the process has been finished in a right way ( value 0 if it is the case )
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ie) {
            fileInput.DeleteFile();
            throw new IntactException ("Error when trying to execute Blast", ie);
        }
    }


    /**
     * This method creates a random file name in the good directory: input files and output files
     * are managed in two different directories.
     * Afterwards, this method fills in the corresponding command file with the good options
     * ("-i" for the input file and "-o" for the ouput for instance).
     *
     * @param inputDir String which describes the relative path of the file to create.
     * @param input boolean which informs whether the file to manage with the command line
     *          is the input file or the output file.
     */
    protected void FillSeqFileConcatCommand (String inputDir, boolean input)
            throws IntactException {

        String wholePathFile = "";

        // looks over whether the multiple alignment program defined in the web.xml file
        // is Blast or Fasta.
        ArrayList splitCommandLine = new ArrayList();
        splitCommandLine.add("/ebi/extserv/data1/appbin/linux-x86/ncbi-blast/blastall");
        splitCommandLine.add("/ebi/extserv/data1/appbin/linux-x86/wu-blast/blastp");
        blast = this.SearchOptionTab ("\\s", splitCommandLine);

        if (input == true) {
            // creates an input file where the protein sequence is going to be recorded
            // file with a random name and a constant extension.
            fileInput = new ManagerFilesBlast(inputDir, (Object)".fasta");
            wholePathFile = fileInput.GetPathFile();

            if (fileInput.CreateFile() == true) {
                // the Fasta protein sequence is captured in a file.
                //CreateWriter method is included in this method.
                fileInput.PutInFile(sequence);
            }

            // adds option(s) to the command line.
            // fasta if FALSE, TRUE if blast
            if (blast == true) {
                command = this.command.concat(" -i " + wholePathFile);
            }
            else {
                // fasta command needs to keep the order "fasta + inputfile + databasefile..."
                String[] tabString = command.split("\\s");
                String tempFastaCommand = tabString[0]; //  bsub
                tempFastaCommand = tempFastaCommand.concat(" ").concat(tabString[1]).concat(" ").concat(tabString[2])
                        //                      -I                            .../fasta/fasta34
                        .concat(" ").concat(wholePathFile).concat(" ").concat(tabString[3]);
                // inputfile                      databasefile
                // to add additional options to the command line
                if (tabString.length > 4) {
                    int i = 4;
                    while ( i < tabString.length) {
                        tempFastaCommand = tempFastaCommand.concat(" ").concat(tabString[i]);
                        i++;
                    }
                }

                this.command = tempFastaCommand;
            }

        }
        // to manage the output file in the command line.
        else {
            // creates an output file where the result of the alignment program is going to be recorded
            // file with a random name and a constant extension.
            fileOutput = new ManagerFilesBlast(inputDir, (Object)".blast");
            wholePathFile = fileOutput.GetPathFile();

            // adds the blast or fasta command option to the command line.
            if (blast == true) {
                command = this.command.concat(" -o " + wholePathFile);
            }
            else {
                command = this.command.concat(" ").concat(" -O " + wholePathFile + " -m 9");
            }
        }

    }

    /**
     * This method splits the command line according to the spaces,
     * and retrieve an item in it. In this class, this method checks if the "-m 9" option
     * exists in the command line, and also checks if the command called is Blast or Fasta.
     *
     * @param split String "regex" according to which the line will be splitted.
     * @param options List which contains String to retrieve in the line already splitted.
     *
     * @return boolean answer whether our option is found in the line splitted.
     */
    protected boolean SearchOptionTab (String split, ArrayList options) {

        String[] tabString = command.split(split);

        for (int m = 0; m < tabString.length; m++) {

            for (int n = 0; n < options.size(); n++) {
                if ( tabString[m].equals(options.get(n)) == true ) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * This method manages the output stream of a process:
     * limited buffer size: the process needs that a bufferReader
     * reads the screen output stream and the error output stream.
     *
     * @param stream manage this screen output stream with the appropriate object.
     * @param in boolean to specify if the previous Object parameter is an InputStream
     *                                                              or an OutputStream.
     *
     * */
    protected void OutputProcessManagement (Object stream, boolean in) {

        try {
            if (in == true) {
                InputStreamReader isr = new InputStreamReader((InputStream)stream);
                BufferedReader br = new BufferedReader (isr);
                while ( (br.readLine()) != null)
                    continue;

                br.close();
                isr.close();
            }
            else {
                //OutputStreamWriter osr = new OutputStreamWriter((OutputStream)stream);
                PrintWriter pw = null;
                if (stream != null) {
                    pw = new PrintWriter((OutputStream)stream);
                }
                pw.println();
                if (pw != null) {
                    pw.flush();
                }
                pw.close();
                /*BufferedWriter br_writer = new BufferedWriter (osr);
                br_writer.flush();

                osr.close();
                br_writer.close();*/
            }

        }
        catch (IOException io) {
            System.err.println(io);
        }
    }


    /**
     * This method tests if the evalue is smaller than the constant defined in the web.xml file,
     * which means that the alignment is acceptable.
     * If it is, the E-Value column is then removed from the list.
     *
     * @param indexTab int which corresponds to the "evalue column" in the tabular output file.
     * @param alignResults List which contains all the results from the parsing of the output file.
     *
     * @return arrayList of List: new results without the "evalue column" and without lines
     *          where the evalue was too small (lines removed). Each line of the ArrayList contains
     *          our 6 items (ac, %, start...)
     */
    protected ArrayList ParamTest (int indexTab, ArrayList alignResults) {
        // if indexEvalue < 0, it means that this value isn't retrieved by the regular expression.
        // This test is not done. The Evalue isn't tested in a wu-blast program.
        if ( (indexTab >= 0) && (alignResults.isEmpty() == false)) {

            for (int i = 0; i < alignResults.size(); i++) {
                ArrayList inListResults = (ArrayList) alignResults.get(i);

                // retrieve the evalue in the Fasta output file.
                Object evalueString = inListResults.get(indexTab);
                Double evalueDouble = new Double(evalueString.toString());
                Double eval = new Double(evalueMin);
                // if the evalue is smaller than the constant defined, the list keeps this line.
                if (evalueDouble.compareTo(eval) < 0) {
                    inListResults.remove(indexTab);
                    alignResults.set(i, inListResults);
                }
                // if the evalue is greater, line removed.
                else {
                    alignResults.remove(i);
                    i--;
                }
            }
        }
        return alignResults;
    }
}
