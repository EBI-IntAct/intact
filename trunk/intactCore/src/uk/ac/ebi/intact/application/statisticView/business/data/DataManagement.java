/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.data;

import uk.ac.ebi.intact.util.StatisticsSet;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.application.statisticView.business.Constants;

import java.util.Collection;
import java.util.ArrayList;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

/**
 * This class provides an access to the IA_Statistic table of the IntAct database.
 * It allows to retrieve all the results, and to switch them in few categories (collection objects).
 * Each categories will be required by a Graph class of the business logic.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class DataManagement {

    static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    /**
     * Helper to manage the request in the database
     */
    private IntactHelper helper;

    private StatisticsSet statisticsSet = null;

    //---------- CONSTRUCTOR -------------------//

    /**
     * Constructor which create an instance of the IntactHelper object
     */
    public DataManagement() throws IntactException {
        try {
            helper = new IntactHelper();
            statisticsSet = new StatisticsSet();
        }
        catch (IntactException ie) {
            logger.error("Could not connect to the intact database, cause:" + ie.getRootCause(), ie);
            throw ie;
        }
    }

    public void closeDataStore () throws IntactException{
        helper.closeStore();
    }


    //---------- PUBLIC METHODS -------------------//


    /**
     * This method retrieves the Timestamp of the last row in the Statistics table
     * in this form: 2003-06-04 14:13:46.0
     *
     * Then, it works in it to return only the date of the Timestamp like this:
     *               2003-06-04
     *
     * @return String The date of the last data stored in the Statistics table
     */
    public String getLastTimestamp() throws IntactException {

        Timestamp timestamp = statisticsSet.getLastTimestamp (helper);
        String time = timestamp.toString();
        String[] tabString = time.split("\\s");
        String theDate = tabString[0];
        return theDate;
    }


    /**
     *
     * This method allows to retrieve the list of these latest data:
     *      number of proteins
     *      number of interactions ( + binary interactions + complexes)
     *      number of experiments
     *      number of terms
     * stored in the last row of the Statistics table
     *
     * @return Collection The 6 items of the last row are stored in this collection
     */
    public Collection getLastStatistics () throws IntactException {

        ArrayList statData = (ArrayList) statisticsSet.getLastRow(helper);

        // to delete the first item which is the timestamp!
        statData.remove(0);
        return statData;
    }


    /**
     *
     * This method allows to retrieve the list of data from one column of the statistics table.
     *
     * For example, if we want to retrieve the list of "number of proteins" over time,
     * we have to retrieve data from the second column (one after the Timestamp column)
     * Then the fieldRequired = 1
     *
     * @param fieldRequired Number which specifies from which column the data list will be retrieved.
     * @return Collection The 6 items of the last row are stored in this collection
     */
    public Collection getAllDataFromOneFieldStatistics (int fieldRequired) throws IntactException {
        return statisticsSet.getListOfOneField (helper, fieldRequired);
    }
}
