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
        //public static final String DIR_INPUT_BLAST = "/ebi/sp/misc1/tmp/shuet/intactblast/fasta_files/";
        public static final String DIR_INPUT_BLAST = "/ebi/sp/misc1/tmp/shuet/blastfiles/input/";

        //public static final String DIR_OUTPUT_BLAST = "/ebi/sp/misc1/tmp/shuet/intactblast/blast_files/";
        public static final String DIR_OUTPUT_BLAST = "/ebi/sp/misc1/tmp/shuet/blastfiles/output/";

        //public static final String DIR_OUTPUT_WGETZ = "/ebi/sp/misc1/tmp/shuet/intactsrs/wgetz_files/list_acc/";
        public static final String DIR_OUTPUT_WGETZ = "/ebi/sp/misc1/tmp/shuet/srsfiles/output_acc/";

        //public static final String DIR_OUTPUT_WGETZ_SEQ = "/ebi/sp/misc1/tmp/shuet/intactsrs/wgetz_files/seq_fasta/";
        public static final String DIR_OUTPUT_WGETZ_SEQ = "/ebi/sp/misc1/tmp/shuet/srsfiles/output_sequence/";

        //public static final String DIR_OUTPUT_GETZ = "/ebi/sp/misc1/tmp/shuet/intactsrs/getz_files";


                /* ----------- REGULAR  EXPRESSIONS -----------------*/

        /*
         *                  WU BLAST
         * regular expressions to parse the standard blast output file (-m 0 and wu-blast output file)

			----- example of the output file to parse ---

                >SWALL:O88613 (3873) MUSCARINIC ACETYLCHOLINE RECEPTOR M1 (FRAGMENT)
                        Length = 200

                Score = 61 (26.5 bits), Expect = 4.4, Sum P(2) = 0.99
                Identities = 26/78 (33%), Positives = 36/78 (46%)

                Query:   317 TEKQARELSVPQG---PGAGAESTGEIINNTVPLENSI---PGNCCSALFKNLLLK---- 366
                            TE +AREL+  QG   PG G  S+     +    E S    PG CC       LL+
                Sbjct:    58 TENRARELAALQGSETPGKGGGSSSSSERSQPGAEGSPESPPGRCCRCCRTPRLLQAYSW 117

                Query:   367 KIKRCERKGT-ESVTEEK 383
                            K +  E +G+ ES+T  +
                Sbjct:   118 KEEEEEDEGSMESLTSSE 135

                Score = 37 (18.1 bits), Expect = 4.4, Sum P(2) = 0.99
                Identities = 7/14 (50%), Positives = 8/14 (57%)

                Query:   655 PLPTPELQMPTMVP 668
                            P+  PE Q PT  P
                Sbjct:   148 PMVDPEAQAPTKQP 161

                >SWALL:O55068 (3829) ALPHA1B-ADRENERGIC RECEPTOR (FRAGMENT)
                       Length = 85

                Score = 51 (23.0 bits), Expect = 5.6, P = 0.996
                Identities = 13/33 (39%), Positives = 19/33 (57%)

                Query:   164 SLIETPANGTGPSEALAMLLQETTGELEAAKAL 196
                            +L T A G  P  ++A+ L + + E EAAK L
                Sbjct:    47 TLSSTKAKGHNPRSSIAVKLFKFSREKEAAKTL 79


	                ------------------------------------
         */
            //retrieve the AC "O88613"
        public static final String PARSING_ID = ">\\w*\\W(\\w*)\\s";
            //retrieve the percentage identity "33%": Identities = 26/78 (33%)
        public static final String PARSING_PERCENTAGE = "\\sIdentities\\s=\\s\\d*/\\d*\\s\\((\\d*)\\D\\),";
            // retrieve the number of the start of the query fragment "317" : Query:   317 TEKQARELSVPQG-
        public static final String PARSING_BEGIN_FRAGMENT_QUERY = "Query\\W\\s*(\\d*)\\s\\D*\\s\\d*";
            // retrieve the number of the start of the subject fragment "58" : Sbjct:    58 TENRARELAALQGSETPG
        public static final String PARSING_BEGIN_FRAGMENT_SUBJECT = "Sbjct\\W\\s*(\\d*)\\s\\D*\\s\\d*";
            // retrieve the number of the end of the query fragment "383" : Query:   367 KIKRCERKGT-ESVTEEK 383
        public static final String PARSING_END_FRAGMENT_QUERY = "Query\\W\\s*\\d*\\s\\D*\\s(\\d*)\\s*";
            // retrieve the number of the end of the subject fragment "135" : Sbjct:   118 KEEEEEDEGSMESLTSSE 135
        public static final String PARSING_END_FRAGMENT_SUBJECT = "Sbjct\\W\\s*\\d*\\s\\D*\\s(\\d*)\\s*";



        /*
         *      NCBI BLAST
         * regular expression to parse the table blast output file (-m 9 ~ ncbi blast option)

                # BLASTP 2.2.2 [Dec-14-2001]
                # Database: /ebi/sp/misc1/tmp/shuet/intactblast/bank/seqSigHum.fasta
                # Query: sptrembl:Q9BQD2 Q9BQD2; Signal transducer and activator of transcription 6, interleukin-4 induced.
                # Fields: Query id, Subject id, % identity, alignment length, mismatches, gap openings, q. start, q. end, s. start, s. end, e-value, bit score
                sptrembl:Q9BQD2 sptrembl:Q9BQD2 100.00  679     0       0       1       679     1       679     0.0     1337.0
                sptrembl:Q9BQD2 sptrembl:Q8WWS8 40.14   715     356     17      1       650     1       708     1e-125  438.7
                sptrembl:Q9BQD2 sptrembl:Q8WWS9 39.80   711     356     17      1       650     1       700     1e-123  431.8

         */
            // a comment line with an arrow means that something is retrieved by the RE: concrete example with the first line
        public static final String PARSING_TABLE_BLAST_FILE =  "[^\\t]*\\t" +       //query id
                                                            "\\D*\\W(\\w*)\\t" +    //subject id  -> Q9BQD2
                                                            "(\\d*).\\d*\\t" +      //% identity  -> 100
                                                            "[^\\t]*\\t" +          //alignment length
                                                            "[^\\t]*\\t" +          //mismatches
                                                            "[^\\t]*\\t" +          //gap opening
                                                            "([^\\t]*)\\t" +        //q start     -> 1
                                                            "([^\\t]*)\\t" +        //q end       -> 679
                                                            "([^\\t]*)\\t" +        //s start     -> 1
                                                            "([^\\t]*)\\t" +        //s end       -> 679
                                                            "([^\\t]*)\\t";         // evalue

        /*
         *      FASTA
         * regular expression to parse the table fasta output file (-m 9 ~ ncbi fasta option)

                FASTA (3.39 May 2001) function [optimized, BL50 matrix (15:-5)] ktup: 2
                join: 38, opt: 26, gap-pen: -12/ -2, width:  16
                The best scores are:                                       opt bits E(55)       %_id  %_gid   sw  alen  mn0  mx0  mn1  mx1 gapq gapl
                sptrembl:Q9BQD2 Q9BQD2; Signal transducer and act  ( 679) 4524 1231       0     1.000 1.000 4524  679    1  679    1  679   0   0
                sptrembl:Q8WWS8 Q8WWS8; Partial STAT5B signal tra  ( 787) 1443  398 5.1e-113    0.410 0.459 1548  717    1  650    1  708  67   9
                sptrembl:Q8WWS9 Q8WWS9; Signal transducer and act  ( 791) 1420  392 3.8e-111    0.405 0.454 1520  714    1  650    1  700  64  14

         */
            // example for the second line
        public static final String PARSING_TABLE_FASTA_FILE ="^\\w*:(\\w*).*\\(\\s*\\d*\\)\\s*\\d*" +   //Q8WWS8
                                                              "\\s*\\d*\\s*(.*)\\t\\d.(\\d\\d)\\d" +    //41
                                                              "\\s*\\d.\\d*\\s*\\d*\\s*\\d*\\s*(\\d*)" + //1
                                                              "\\s*(\\d*)" +                             //650
                                                              "\\s*(\\d*)" +                             //1
                                                              "\\s*(\\d*)";                              //708


        /*
         *      WGETZ (to run SRS)
         * regular expression to parse the table wgetz ouput file (+-vn3+-ascii option = 4 columns)

            SWALL:A1AF_CAVPO        P22324  Alpha-1-antiproteinase F precursor (Alpha-1-antitrypsin) (Alpha-1- proteinase inhibitor) (APF) (Fragment).      403
            SWALL:A1AF_RABIT        P23035  Alpha-1-antiproteinase F precursor (Alpha-1-antitrypsin) (Alpha-1- proteinase inhibitor) (APF). 413
            SWALL:A1AS_CAVPO        P22325  Alpha-1-antiproteinase S precursor (Alpha-1-antitrypsin) (Alpha-1- proteinase inhibitor) (APS). 405

         */
            // retrieves the second and third columns (separated  by \\t tabulation)
            // example of items retrieved in the first line:
        public static final String PARSING_TABLE_WGETZ_AC = "[^\\t]*\\t([^\\t]*)" +  // the AC: P22324
                                                            "\\t([^\\t]*)";          // the description: Alpha-1-antiproteinase F precursor (Alpha-1-antitrypsin) (Alpha-1- proteinase inhibitor) (APF).

            //-------   web.xml  ----------------//
    /**
     * some constants for biological tests -> available results
     *
     */
    public static final int SMALLEST_LENGHT_PROTEIN = 20;

    /**
     * 2 important thresholds to allow the application retrieve only homologous sequences:
     *
     *    - the percentage identity
     *    - the E-Value
     *
     * The percentage identity must be > than GREATER_THAN_PERCENTAGE
     * The E-Value must be < than SMALLER_THAN_EVALUE
     *
     */
        // the smallest percentage identity allowed.
    public static final String GREATER_THAN_PERCENTAGE = "fromPercentageIdentity";

        // the greatest E Value allowed.
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
     *          To make the WGETZ URL
     *          Retrieve the Ac and the Description field of an entry with SRS
     *                                      according to a Search String in parameter
     *
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
     *
     *          To make the WGETZ URL
     *          Retrieve the protein sequence with SRS, according to an AC in parameter
     *
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
