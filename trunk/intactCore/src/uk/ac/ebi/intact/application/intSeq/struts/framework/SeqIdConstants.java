/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.framework;

/**
 *
 * Contains constants required for the IntSeq application.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public interface SeqIdConstants {


    /**
     * The key to store an Intact Service object.
     */
    public static final String INTACT_SERVICE = "IntactService";

    /**
     * The key to access a user session object.
     */
    public static final String INTACT_USER = "IntactUser";


    //-------   working with PropertiesLoader  -------//


    public static final String PARSING_PROPERTY_FILE = "/config/Parsing.properties";



        //-------   struts-config.xml  ----------------//

    /*
     * Used in various action classes to define where to forward
     * to on different conditions.  See the struts-config.xml file
     * to see where the page that is using this forwards to.
     */


        /**
        * The key to success SequenceSimilarityAction action.
         */
         public static final String FORWARD_SUCCESS_SEQ = "successseq";

         /**
         * The key to success ProteinSearchAction action.
         */
        public static final String FORWARD_SUCCESS_ID = "successid";

        /**
         * The key to some errors.
         */
        public static final String FORWARD_ERROR = "idseqerror";


        /**
         * command error from the page which display the list of accession number.
         */
        public static final String FORWARD_FAILURE = "failure";

        /**
         * command error from the page which display the list of accession number.
         */
        public static final String FORWARD_ACC_ERROR = "accerror";

        /**
         * the key to forward on the welcome session when a session is put down.
         */
        public static final String FORWARD_END_SESSION = "endsession";

        /**
         * Forward to the result page which another template of the idseq page.
         */
        public static final String FORWARD_RESULT = "idseq";


        /**
         * Manager files ------- to arrange files in their directory.
         */
        public static final String DIR_INPUT_BLAST = "/ebi/sp/misc1/tmp/shuet/intactblast/fasta_files/";

        public static final String DIR_OUTPUT_BLAST = "/ebi/sp/misc1/tmp/shuet/intactblast/blast_files/";

        public static final String DIR_OUTPUT_WGETZ = "/ebi/sp/misc1/tmp/shuet/intactsrs/wgetz_files/list_acc/";

        public static final String DIR_OUTPUT_WGETZ_SEQ = "/ebi/sp/misc1/tmp/shuet/intactsrs/wgetz_files/seq_fasta/";

        public static final String DIR_OUTPUT_GETZ = "/ebi/sp/misc1/tmp/shuet/intactsrs/getz_files";


                /* ----------- REGULAR  EXPRESSIONS -----------------*/

        /*
         * regular expressions to parse the standard blast output file (-m 0 and wu-blast output file)
         */
        public static final String PARSING_ID = ">\\w*\\W(\\w*)\\s";     //">\\w*:(\\w*)\\s\\(\\d*\\)\\s"
        public static final String PARSING_PERCENTAGE = "\\sIdentities\\s=\\s\\d*/\\d*\\s\\((\\d*)\\D\\),";

        public static final String PARSING_BEGIN_FRAGMENT_QUERY = "Query\\W\\s*(\\d*)\\s\\D*\\s\\d*";
        public static final String PARSING_BEGIN_FRAGMENT_SUBJECT = "Sbjct\\W\\s*(\\d*)\\s\\D*\\s\\d*";

        public static final String PARSING_END_FRAGMENT_QUERY = "Query\\W\\s*\\d*\\s\\D*\\s(\\d*)\\s*";
        public static final String PARSING_END_FRAGMENT_SUBJECT = "Sbjct\\W\\s*\\d*\\s\\D*\\s(\\d*)\\s*";

        /*
         * regular expression to parse the table blast output file (-m 9 ~ ncbi blast option)
         */
        public static final String PARSING_TABLE_BLAST_FILE =  "[^\\t]*\\t" +       //query id
                                                            "\\D*\\W(\\w*)\\t" +    //subject id
                                                            "(\\d*).\\d*\\t" +      //% identity
                                                            "[^\\t]*\\t" +          //alignment length
                                                            "[^\\t]*\\t" +          //mismatches
                                                            "[^\\t]*\\t" +          //gap opening
                                                            "([^\\t]*)\\t" +        //q start
                                                            "([^\\t]*)\\t" +        //q end
                                                            "([^\\t]*)\\t" +        //s start
                                                            "([^\\t]*)\\t" +        //s end
                                                            "([^\\t]*)\\t";         // evalue

        /*
         * regular expression to parse the table fasta output file (-m 9 ~ ncbi fasta option)
         */
        public static final String PARSING_TABLE_FASTA_FILE ="^\\w*:(\\w*).*\\(\\s*\\d*\\)\\s*\\d*" +
                                                              "\\s*\\d*\\s*(.*)\\t\\d.(\\d\\d)\\d" +
                                                              "\\s*\\d.\\d*\\s*\\d*\\s*\\d*\\s*(\\d*)" +
                                                              "\\s*(\\d*)\\s*(\\d*)\\s*(\\d*)";


        /*
         * regular expression to parse the table wgetz ouput file (+-vn3+-ascii option = 4 columns)
         */
        public static final String PARSING_TABLE_WGETZ_AC = "[^\\t]*\\t([^\\t]*)\\t([^\\t]*)";
                                                            // retrieve the second and third columns
                                                            //(separated  by \\t tabulation)


            //-------   web.xml  ----------------//
    /**
     * some constants for biological tests -> available results
     *
     */
    public static final int SMALLEST_LENGHT_PROTEIN = 20;

        // the smallest percentage identity allowed.
    public static final String GREATER_THAN_PERCENTAGE = "fromPercentageIdentity";

        // the greagest E Value allowed.
    public static final String SMALLER_THAN_EVALUE = "greatestEValue";

    /**
         * Used as a key to identify a format database command.
         * the value is defined in the web.xml file
         */
    public static final String FORMAT_INTACT_COMM = "firstFormatIntact";

    /**
     * Used as a key to identify a blast command.
     * the value is defined in the web.xml file
     */
    public static final String BLAST_COMM_INTACT = "firstBlastIntact";

    /**
     * Used as a key to identify a wgetz command.
     * the value is defined in the web.xml file
     */
    public static final String SRSGETZ_DEB_INTACT_DB = "firstSrsWithIntact";

    /**
     * Used as a key to identify the wgetz options which allow to retrieve the protein ac and its description field.
     * the value is defined in the web.xml file
     */
    public static final String SRSGETZ_ACCDES_RETRIEVED = "firstSrsOptionsIntact";

    /**
     * Used as a key to identify a wgetz command.
     * the value is defined in the web.xml file
     */
    public static final String SRSGETZ_SECOND_DEB = "secondSrsWithIntact";

    /**
     * Used as a key to identify the wgetz options which allow to retrieve the protein sequence.
     * the value is defined in the web.xml file
     */
    public static final String SRSGETZ_SEQ_RETRIEVED = "secondSrsOptionsIntact";





}
