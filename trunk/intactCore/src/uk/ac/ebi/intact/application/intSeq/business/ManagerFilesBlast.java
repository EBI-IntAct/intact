/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.business;

import java.util.*;
import java.io.*;

import uk.ac.ebi.intact.application.intSeq.business.ManagerFiles;
import uk.ac.ebi.intact.business.IntactException;


/**
 * This class inherits the <code>ManagerFiles</code> class.
 * The particular output file of the Blast program needs a non-generic method for
 * its parsing.
 * Usually, the wu-blast method requires this method, but sometimes, the
 * ncbi-blast gives the same output file, when the option "-m 9" is not used in the
 * command line. This option makes a table output format, which needs a generic method
 * defined in the <code>ManagerFiles</code> class, which parses a table.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class ManagerFilesBlast extends ManagerFiles {

     //---------- INSTANCE VARIABLES -------------//

     //---------- CONSTRUCTOR -------------------//

    /**
      * constructor by default
      */
    public ManagerFilesBlast () {
    }

    /**
     * constructor which allows us to create a random file name,
     * this application is multi-user. So, it allows to reduce possible conflicts.
     *
     * @param pathFile String to know where to put this file
     * @param extension Object representing the logic extension behind the file name
     *
     */
    public ManagerFilesBlast (String pathFile, Object extension) {
        this.fileName = GetRandFileName(extension);
        this.inputFile = new File (pathFile.concat(fileName));
        this.absolutePath = inputFile.getAbsolutePath();
    }

    /**
     * constructor which passes a hard file name in parameter
     *
     * @param pathFile String to know where to put this file
     * @param fileName String to represent the hard name file
     *
     */
    public ManagerFilesBlast (String pathFile, String fileName) {
        this.fileName = fileName;
        this.inputFile = new File (pathFile.concat(fileName));
        this.absolutePath = inputFile.getAbsolutePath();
    }



     //---------- PUBLIC METHODS -----------------//

    /**
     * This method parses a wu-blast output file, to retrieve the accession number of each result,
     * the percentage identity with the query, start and end of the both aligned fragments.
     *
     *  --------------- WU-BLAST OUTPUT FILE SAMPLE --------------------------------
     *          >SWALL:O88613 (3873) MUSCARINIC ACETYLCHOLINE RECEPTOR M1 (FRAGMENT)
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

     *
     * What is retrieved from this file by the regular expression is described
     * in the file where the regular expression is defined (the SeqIdConstant class temporarily
     * -- later in a configuration file of the application)
     *
     * @param patterns contains the Regular Expression list, one for each item to retrieve.
     * @param jWhichGroup defines the group requiered in the regular expression:
     *          jWhichGroup must be different from "0" otherwise, the whole line is retrieved.
     *
     * @return The result is a list (all lines retrieved) of lists (all items retrieved in one line).
     *
     */
    public ArrayList ResultParsing (ArrayList patterns, int jWhichGroup) throws IntactException {
        try {
            this.PrepareBufferedReader();

            if (bufReader.ready()) {

                    // variable which allows to read a file some line after others.
                String currentLine = null;

                    // to store the results of one line on a table
                ArrayList perc = new ArrayList();

                    // describe all items to retrieve in a line.
                String theIdConc = "";          // accession number
                String thePercConc = "";        // percentage identity
                String theQueryBegConc = "";    // start of the fragment query
                String theSubjectBegConc = "";  // start of the fragment subject
                String theEndConc = "";         // end of the fragment query

                    // to know if the beginning of the matched fragment is already stored... if it is,
                    // we don't have to store it again.
                 boolean beginQuery = false, beginSubject = false;

                    // reading the file some lines after others
                while ((currentLine = bufReader.readLine()) != null) {

                        //first, retrieve the subject accession number,
                        // which has matched with our query.
                    String theIdRetrieved = ParseWithReOneGroup (currentLine, (String)patterns.get(0), jWhichGroup);

                    if (theIdRetrieved != "") {
                            //store this variable during the parsing of one protein subject.
                        theIdConc = theIdRetrieved;
                    }

                    String thePercRetrieved = ParseWithReOneGroup (currentLine, (String)patterns.get(1), jWhichGroup);

                    if (thePercRetrieved != "") {
                        thePercConc = thePercRetrieved;
                            //for each percentage, we have to store the only first fragment beginning
                        beginQuery = false;
                        beginSubject = false;
                    }

                        // behind in the file, retrieve the list of query fragment's start number
                        // foreach identity percentage
                    String theBeginFragment = ParseWithReOneGroup (currentLine, (String)patterns.get(2), jWhichGroup);

                    if ( (theBeginFragment != "") && (beginQuery == false) ) {

                        theQueryBegConc = theBeginFragment;
                            // to store the first number only for this query fragment
                        beginQuery = true;
                    }

                    String theBeginSubjectFragment = ParseWithReOneGroup (currentLine, (String)patterns.get(3), jWhichGroup);

                    if ( (theBeginSubjectFragment != "") && (beginSubject == false) ) {

                        theSubjectBegConc = theBeginSubjectFragment;
                            // to store the first number only for this subject fragment
                        beginSubject = true;
                    }

                        // behind again in the file, retrieve the list of query fragment's end number
                        // foreach identity percentage.
                        // when a line is as long as 75 characters, it means that the fragment continues
                        // so we don't want this one but the last one (where the line is smaller)
                    String theEndQueryFragment = ParseWithReOneGroup (currentLine, (String)patterns.get(4), jWhichGroup);

                    if ( (theEndQueryFragment != "") && (currentLine.length() < 74) ) {

                        theEndConc = theEndQueryFragment;
                    }

                        // behind in the file, retrieve the list of subject fragment's end number
                        // by percentage identity
                    String theEndFragment = ParseWithReOneGroup (currentLine, (String)patterns.get(5), jWhichGroup);

                    if ( (theEndFragment != "") && (currentLine.length() < 74) ) {

                        ArrayList theConc = new ArrayList();
                        theConc.add(theIdConc);
                        theConc.add(thePercConc);
                        theConc.add(theQueryBegConc);
                        theConc.add(theEndConc);
                        theConc.add(theSubjectBegConc);
                        theConc.add(theEndFragment);

                        perc.add(theConc);
                    }

                } // while ((currentLine = bufReader.readLine()) != null)

                bufReader.close();
                fr.close();

                return perc;

            } // if (bufReader.ready())
            else {
                return null;
            }
        } // try
        catch (IOException io) {
            return null;
        }
   }
}