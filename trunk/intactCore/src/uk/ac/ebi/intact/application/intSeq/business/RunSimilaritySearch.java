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
      * data required to manage the blast program.
      */
    protected String command = "";
    protected String sequence = "";
    /**
     * EValue initialized at "0" to make a test afterwards.
     */
    protected double evalueMin = 0;

     /**
      * the both ManagerFiles
      */
    protected ManagerFilesBlast fileInput = null;
    protected ManagerFilesBlast fileOutput = null;

    boolean blastOrFasta = false;
    boolean commandExecution = false;


            //------ CONSTRUCTOR ---------//

    /**
      * constructor by default
      */
    public RunSimilaritySearch () {
    }

    /**
     * constructor which retrieves the blast command line
      * and the sequence to run with (protein sequence needs to be in a Fasta format).
     *
     * @param seq String the protein sequence in the Fasta format
     * @param command String the multiple alignment program will be launched by this command line
     *
     */
    public RunSimilaritySearch (String seq, String command) {
        this.sequence = seq;
        this.command = command;
    }

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
     * keep inform the action whether the command line was executed well or not.
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
    public ArrayList RetrieveParseResult () {

                // retrieve the parsing result list
            ArrayList listResults = new ArrayList();

                // indicate the column of the EValue in the table, when the tabular format is used.
            int indexEvalue = -1;

                // create input and output files, and try to execute the command line.
            commandExecution = this.ExecBlast();

            if ( (commandExecution == true) && (fileOutput.inputFile.length() > 0)
                                        && (fileOutput.inputFile.canRead() == true)) {

                ArrayList theOptions = new ArrayList ();
                    // options "-m 9" or "-m 8" in the command line mean that the output file
                    // format is a table, easier to parse.
                theOptions.add("8");
                theOptions.add("9");

                    // looking for the "-m 8" or "-m 9" options in the command line = tabular format
                boolean optionLooked = this.SearchOptionTab ("\\s", theOptions);
                if (optionLooked == true) {

                    if (blastOrFasta == true) {
                            //when the tabular output blast file is used (-m 9 ncbi or -m 9 fasta)
                        listResults = fileOutput.ResultParsingTableFile(SeqIdConstants.PARSING_TABLE_BLAST_FILE);
                        indexEvalue = 6;
                    }
                    else {
                           //when the tabular output blast file is used (-m 9 ncbi or -m 9 fasta)
                        listResults = fileOutput.ResultParsingTableFile(SeqIdConstants.PARSING_TABLE_FASTA_FILE);
                        indexEvalue = 1;
                    }
                }
                else {
                        //when the standard output blast file is used (-m 0 ncbi or wu-blast output)
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
            }

                // any program needs this file anymore.
            fileOutput.DeleteFile();

                // to test if the EValue is smaller than our constant defined in the web.xml file.
            listResults = this.ParamTest(indexEvalue, listResults);

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
    protected boolean ExecBlast () {

        this.FillSeqFileConcatCommand (SeqIdConstants.DIR_INPUT_BLAST, true);
        this.FillSeqFileConcatCommand (SeqIdConstants.DIR_OUTPUT_BLAST, false);

            //retrieves the current Java Runtime Environment
        Runtime rt = Runtime.getRuntime();

        try{
                //Process returned to the JVM
            Process child = rt.exec(command);
                // limited buffer siwe: the process needs that a bufferReader
                // reads the input and output stream.
                // to obtain the process's ouput stream
            InputStream stdin = child.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader (isr);
            while ( (br.readLine()) != null)
                continue;

                // wait for the end of the blast process, 0 if it has been well done.
                // sometimes, process block, and even deadlock
            int exitValue = child.waitFor();

            // any program needs this file anymore
            fileInput.DeleteFile();

                //terminates the currently JVM
            //rt.exit(finish);

            child.destroy();

                //test if the process has been finished in a right way ( value 0 if it is the case )
            if (exitValue == 0) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (InterruptedException ie) {
            fileInput.DeleteFile();
            System.err.println (ie);
            return false;
        }
        catch (NullPointerException npe) {  // if command is null
            fileInput.DeleteFile();
            System.err.println (npe);
            return false;
        }
        catch (SecurityException se) {
            fileInput.DeleteFile();
            System.err.print(se);
            return false;
        }
        catch (RuntimeException re) {
            fileInput.DeleteFile();
            System.err.print(re);
            return false;
        }
        catch (IOException io) {
            fileInput.DeleteFile();
            System.err.println(io);
            return false;
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
    protected void FillSeqFileConcatCommand (String inputDir, boolean input) {

            String wholePathFile = "";

                // looks over whether the multiple alignment program defined in the web.xml file
                // is Blast or Fasta.
            ArrayList splitCommandLine = new ArrayList();
            splitCommandLine.add("/ebi/extserv/data1/appbin/linux-x86/ncbi-blast/blastall");
            splitCommandLine.add("/ebi/extserv/data1/appbin/linux-x86/wu-blast/blastp");
            blastOrFasta = this.SearchOptionTab ("\\s", splitCommandLine);

            if (input == true) {
                    //creates a file with a random name.
                fileInput = new ManagerFilesBlast(inputDir, (Object)".fasta");
                wholePathFile = fileInput.GetPathFile();

                if (fileInput.CreateFile() == true) {
                        // the Fasta protein sequence is captured in a file.
                        //CreateWriter method is included in this method.
                    fileInput.PutInFile(sequence);
                }

                    // adds option(s) to the command line.
                if (blastOrFasta == true) {
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
                fileOutput = new ManagerFilesBlast(inputDir, (Object)".blast");
                wholePathFile = fileOutput.GetPathFile();

                    // adds the blast or fasta command option to the command line.
                if (blastOrFasta == true) {
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
     * This method tests if the evalue is smaller than the constant defined in the web.xml file,
     * which means that the alignment is acceptable.
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

        // if we need to format the protein database in a Fasta format file, before the blastall execution
    protected void FormatDatabase (File dbFile) {
        // formatdb -i "input file for formatting"
            //result = Fasta format file representing the database

            // info in this web page: --- http://ccgb.umn.edu/support/software/NCBI/README.formatdb
    }



}
