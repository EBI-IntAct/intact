/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import java.util.*;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

/**
 * This utility allows to get all statistics from the IA_Statistics table.
 *                        get the last Timestamps
 *                        get the Statistics collection of the last timestamp
 * calling the IntactStatistics JAVA object which is in the util directory also
 *
 * These data are required by the StatisticView application (in the DataManagement java class
 * of the Business Logic of the application)
 *
 *
 * ----------------------- FIRST NEEDS -----------------------------------
 * The Scripts
 *                  create_table_statistics.sql
 *            and   insert_count_statistics.sql
 * should be run before the run of this class.
 *
 * Then check the repository_user.xml : mapping of the IA_Statistics table
 *
 * -----------------------------------------------------------------------
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class StatisticsSet {

    private Logger logger;

    // ------ CONSTRUCTORS --------- //

    /**
     * Allows to create an instance with a default logger
     */
    public StatisticsSet () {
        this(StatisticsSet.class.getName());
    }

    /**
     * If you want to have a logging facilities from that class you have to give your
     * own logger name.
     * @param loggername the name of your Log4J logger
     */
    public StatisticsSet (String loggername) {
        logger = Logger.getLogger(loggername);
    }



    //--------- PROTECTED METHOD ---------------------//

    /**
     * This private method is called by the others public method of this class.
     *
     * Get all data of the IA_Statistics table in the IntAct database, thanks to the search method
     * managing an IntactHelper object.
     * The null parameter in the search method means to retrieve all the data from a table via OJB
     * and JDBC.
     *
     * todo : would be nice to implement a caching system,
     *
     * @param helper One instance of the IntactHelper class has to be passed in parameter, to
     *               use the search method.
     */
    private ArrayList getAllStatisticsIntAct (IntactHelper helper) throws IntactException {

        ArrayList statCollection = null;

        try {
            // search method to get the IntactStatistics object and all statistics in IntAct
            // null parameter means no restrictive criteria.
            Collection stat = helper.search("uk.ac.ebi.intact.util.IntactStatistics", "ac", null);

            statCollection = new ArrayList();

            logger.info ("Content of the table");
            for (Iterator iterator = stat.iterator(); iterator.hasNext();) {

                // to retrieve only one line
                ArrayList oneRow = new ArrayList(7);

                // return the next IntactStatistics object
                IntactStatistics intStat = (IntactStatistics) iterator.next();

                oneRow.add(intStat.getTimestamp());
                oneRow.add(new Integer(intStat.getNumberOfProteins()));
                oneRow.add(new Integer(intStat.getNumberOfInteractions()));
                oneRow.add(new Integer(intStat.getNumberOfBinaryInteractions()));
                oneRow.add(new Integer(intStat.getNumberOfComplexInteractions()));
                oneRow.add(new Integer(intStat.getNumberOfExperiments()));
                oneRow.add(new Integer(intStat.getNumberOfGoTerms()));

                // only for tests... to remove afterwards
                Timestamp timestamp = intStat.getTimestamp();

                int protNumber     = intStat.getNumberOfProteins();
                int interNumber    = intStat.getNumberOfInteractions();
                int binInterNumber = intStat.getNumberOfBinaryInteractions();
                int complexNumber  = intStat.getNumberOfComplexInteractions();
                int expNumber      = intStat.getNumberOfExperiments();
                int termNumber     = intStat.getNumberOfGoTerms();

                if (null != logger) logger.info (timestamp + " " +
                        protNumber + " " +
                        interNumber + " " +
                        binInterNumber + " " +
                        complexNumber + " "+
                        expNumber +" " +
                        termNumber);

                statCollection.add (oneRow);


            }
        }
        catch (IntactException ie) {
            if (null != logger)
                logger.error ("when trying to get all statistics, cause: " + ie.getRootCause(), ie);
            throw ie;
        }

        return statCollection;
    }

    /**
     * Retrieve the latest timestamp of the Statistics table
     *
     * @param helper to call the getAllStatisticsIntAct method
     * @return Timestamp the timestamp of the last line in the Statistics table or null if no data found.
     */
    public Timestamp getLastTimestamp(IntactHelper helper)
            throws IntactException{
        ArrayList stats = getAllStatisticsIntAct(helper);
        if (stats.isEmpty()) {
            return null;
        }
        ArrayList lastRow = (ArrayList) stats.get (stats.size()-1);
        return (Timestamp) lastRow.get(0);
    }

    /**
     * Retrieve the latest data of the Statistics table
     *
     * @param helper to call the getAllStatisticsIntAct method
     * @return Collection which contains the latest data of the Statistics table
     */
    public Collection getLastRow (IntactHelper helper)
            throws IntactException {
        ArrayList stats = getAllStatisticsIntAct(helper);
        return (ArrayList) stats.get (stats.size()-1);
    }

    /**
     * Retrieve the data of the column required in the Statistics table
     * like for instance, the list of quantity of proteins stored in this table over time.
     *
     * @param helper to call the getAllStatisticsIntAct method
     * @return Collection which contains the list of data of one column of the Statistics table
     */
    public Collection getListOfOneField (IntactHelper helper,
                                         int fieldToRetrieve)
            throws IntactException {

        ArrayList stats = getAllStatisticsIntAct(helper);

        ArrayList theDataList = new ArrayList();

        int i = 0;
        while ( i < stats.size() ) {
            ArrayList getOneRow = (ArrayList) stats.get(i);
            theDataList.add(getOneRow.get(fieldToRetrieve));
            i++;
        }
        return theDataList;
    }



    /**
     * D E M O
     */
    public static void main(String[] args) throws Exception {

        StatisticsSet pfd = new StatisticsSet();
        IntactHelper helper = new IntactHelper();

        System.out.println ("utility of the class: count some data in IntAct ");
        pfd.getLastTimestamp(helper);
        pfd.getLastRow(helper);
        pfd.getListOfOneField(helper, 0);
    }
}


