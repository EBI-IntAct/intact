/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Constants;
import uk.ac.ebi.intact.persistence.DAOFactory;
import uk.ac.ebi.intact.persistence.DAOSource;
import uk.ac.ebi.intact.persistence.DataSourceException;

import uk.ac.ebi.intact.model.Protein;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This util class retrieves protein sequences from IntAct
 *
 * It downloads all IntAct sequences in fasta format into a file "proteinFastaDownload":
 *              * the fasta header contains the IntAct ac
 *              * the protein sequence follows (from the next line)
 *
 * like that:
 *
 *          >ac
 *          seq...........................
 *          >ac
 *          seq...........................
 *
 * -------------------- Creation of the file:
 *      needs to specify the path where this file will be created.
 *
 *      This private attribute is a String which specifies the path where the file will be created
 *      Check if this directory exists before running this utility
 *
 *      private final String PATH_INTACT_FORMAT_FILE = "/ebi/sp/misc1/tmp/shuet/intactblast" +
 *                                                              "/intact-data/";
 *
 * ------------------- Format of the file
 * Finally, the process formats the file. Formatdb must be used in order to format protein
 * source database like IntAct, before this database can be searched by blastp or fasta.
 *
 * This private attribute is the command line of the formatdb program
 * private final String FORMAT_COMMAND_LINE = "bsub -I " +
 *                                   "/ebi/extserv/data1/appbin/linux-x86/ncbi-blast/formatdb -i "
 *                                               + PATH_INTACT_FORMAT_FILE;
 *
 * all the Formatdb documentation is available on :
 *              http://ccgb.umn.edu/support/software/NCBI/README.formatdb
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class ProteinFastaDownload {


    //------ INSTANCE VARIABLES ----//


    /**
     * absolute path where the proteinFastaDownload will be created
     */
    private final String PATH_INTACT_FORMAT_FILE = "/ebi/sp/misc1/tmp/shuet/intactblast" +
                                                               "/intact-data/";

    /**
     * absolute path where the proteinFastaDownload will be created
     */
    private final String INTACT_FASTA_FILE_NAME = "proteinFastaDownload.fasta";  //this.getClass().getName()+".fasta"

    /**
     * The command line to format the previous database file, once it is fulled
     * the file name need to be added
     */
    private final String FORMAT_COMMAND_LINE = "bsub -I " +
                                    "/ebi/extserv/data1/appbin/linux-x86/ncbi-blast/formatdb -i "
                                                + PATH_INTACT_FORMAT_FILE;


    //------ CONSTRUCTOR ----//

    /**
     * constructor allows to prepare the database access
     *
     */
    public ProteinFastaDownload() throws IntactException, DataSourceException {

    }


    //--------- PROTECTED METHOD ---------------------//

    /**
      * get the line separator string.
      * It allows to use the same separator int the service and int the client
      * to keep the multiplateform aspect.
      *
      * @return the line separator
      */
    protected String getLineSeparator () {
        return System.getProperty ("line.separator");
    } // getLineSeparator


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
    protected void outputProcessManagement (Object stream, boolean in) {

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
                PrintWriter pw = null;
                if (stream != null) {
                    pw = new PrintWriter((OutputStream)stream);
                }
                pw.println();
                if (pw != null) {
                    pw.flush();
                }
                pw.close();
            }
        }
        catch (IOException io) {
            System.err.println(io);
        }
    }

      /**
      *  If we need to format the protein database in a Fasta format file,
       * before processing a biological software like Blast or Fasta,
       * the corresponding command line must be runned by this method.
      *
      * @param fileToFormat the file which needs to be formatted
      * @return boolean Attribute which inform if the process has been well done
      */
       protected boolean formatProteinFastaFile (File fileToFormat) {

               Runtime rt = Runtime.getRuntime();

           try{
                   //Process returned to the JVM
               Process child = rt.exec(FORMAT_COMMAND_LINE.concat(this.INTACT_FASTA_FILE_NAME));

                   // screen output management
               InputStream stdin = child.getInputStream();
               outputProcessManagement(stdin, true);

               InputStream stdin_error = child.getErrorStream();
               outputProcessManagement(stdin_error, true);

               OutputStream stdout = child.getOutputStream();
               outputProcessManagement(stdout, false);

                   // wait for the end of the blast process, 0 if it has been well done.
                   // sometimes, process block, and even deadlock
               int exitValue = child.waitFor();

               //child.destroy();

                   //test if the process has been finished in a right way ( value 0 if it is the case )
               if (exitValue == 0) {
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




    /**
     * get all protein sequences in a Fasta format (in a String object)
     *
     * @param helper The IntactHelper object which allows to
     *          retrieve data thanks to the <code>search</code> method
     * @return String all protein sequences stored in IntAct
     */
    protected String getAllProteinIntAct (IntactHelper helper) {

        String fastaSequence = "";
        String lineSeparator = getLineSeparator();

        try {

                // search method to get the Protein object and all proteins in IntAct
            Collection proteins = helper.search("uk.ac.ebi.intact.model.Protein", "ac", "*");

                // for each protein, get the ac and the sequence string
                // and creates the Fasta format there
            for (Iterator iterator = proteins.iterator(); iterator.hasNext();) {
                    // return the next Protein object
                Protein protein = (Protein) iterator.next();
                    // the accession number of the protein and the protein sequence
                String oneSequence = ">".concat(protein.getAc()+lineSeparator)
                                                        .concat(protein.getSequence()+lineSeparator);
                fastaSequence = fastaSequence.concat(oneSequence);
            }
        }
        catch (IntactException ie) {
            System.err.println (ie);
        }

        return fastaSequence;
    }



    /**
     *
     * This method
     *      * delete all files in the directory
     *      * creates the proteinFastaDownload File and full it with the search result
     *      * calls the format database method at the end to make
     *                  available the process with a sequence analysis algorithm like Blast or Fasta
     *
     * @param filecontent is a String that is going to full the file
     * @return boolean Inform if the formatdb process is well done
     */
    protected boolean filledProteinFastaFile (String filecontent) {

            // clean the directory
        File path = new File(PATH_INTACT_FORMAT_FILE);
        File[] formattedFiles = path.listFiles();
        File oneFile;
        boolean delete;
        for (int i=0; i<formattedFiles.length; i++) {

                // allows to delete all files in this directory except the proteinFastaFile file
            if (formattedFiles[i].isFile() == true) {
                oneFile = formattedFiles[i];
                delete = oneFile.delete();
                if (delete == true) {
                    System.out.println(" deleted file " + formattedFiles[i].getName());
                }
            }
        }

        System.out.println("proteinFastaFile :" + PATH_INTACT_FORMAT_FILE.concat(this.INTACT_FASTA_FILE_NAME));
        // create the proteinFastaDownload file
        File proteinFastaFile = new File (PATH_INTACT_FORMAT_FILE.concat(this.INTACT_FASTA_FILE_NAME));

        try {
            if (proteinFastaFile.exists() == false) {
                System.out.println("create the file !");
                proteinFastaFile.createNewFile();
            }
            System.out.println(" we want now filling the file ");
            if (proteinFastaFile.canWrite() == true ) {        // && proteinFastaFile.length() == 0) {
                FileWriter fileWriter = new FileWriter(proteinFastaFile);
                fileWriter.write(filecontent);
                 //to make sure that all the buffer is write in the file.
                fileWriter.flush();
                System.out.println("all is written in the file");
            }
        }
        catch (IOException io) {
                System.err.println (io);
        }

            // call the format file method
        boolean formatted = formatProteinFastaFile (proteinFastaFile);
        return formatted;
    }




    /**
     * This utilitie allows to download all protein sequences being in the IntAct database
     *
     * With a call to this class, it should download all IntAct sequences into a file in
     * fasta format: the fasta header contains the IntAct ac, and the protein sequence behind.
     * Finally, the file is already formatted and ready to be launched with a sequence analysis
     * program.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        ProteinFastaDownload pfd = new ProteinFastaDownload();

        final String util = "this class allows to generate a file in fasta format " +
                                    "with all IntAct protein sequences. The file is " +
                                    "in the path : " + pfd.PATH_INTACT_FORMAT_FILE;

        System.out.println ("utility of the class " + util);


        IntactHelper helper = new IntactHelper();

        String fileContent = pfd.getAllProteinIntAct(helper);
        System.out.println("fileContent = " + fileContent);

        boolean formatted = pfd.filledProteinFastaFile(fileContent);
        if (formatted == true) {
            System.out.println("OK the proteinFastaDownload file is formatted.");
        }
        else
            System.out.println("FAILURE in the format method.");
    }

}
