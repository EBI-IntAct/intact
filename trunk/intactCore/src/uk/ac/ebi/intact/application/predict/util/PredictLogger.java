/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.predict.util;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.sql.SQLException;

/**
 * The logger for the Predict application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class PredictLogger {

    /**
     * Only instance of this class.
     */
    private static PredictLogger ourInstance = new PredictLogger();

    /**
     * The priority for logging.
     */
    private Priority myPriority = Priority.INFO;

    /**
     * The logger for Predict. Allow access from the subclasses.
     */
    private transient Logger myLogger;

    /**
     * Returns the only instance of this class.
     * @return the only instance of this class.
     */
    public static PredictLogger getInstance() {
        return ourInstance;
    }

    /**
     * Logs a string.
     * @param msg the string to log.
     */
    public void log(String msg) {
        getLogger().log(myPriority, msg);
    }

    /**
     * Logs an exception.
     * @param throwable the exception to log.
     */
    public void log(Throwable throwable) {
        getLogger().log(myPriority, throwable);
    }

    /**
     * Logs the SQL exception using the logger
     * @param sqle the exception to log.
     */
    public void log(SQLException sqle) {
        while (sqle != null) {
            log("**** SQLException ****");
            log("** SQLState: " + sqle.getSQLState());
            log("** Message: " + sqle.getMessage());
            log("** Error Code: " + sqle.getErrorCode());
            log("***********");
            sqle = sqle.getNextException();
        }
    }

    // helper methods.

    private Logger getLogger() {
        if (myLogger == null) {
            myLogger = Logger.getLogger("predict");
        }
        return myLogger;
    }
}
