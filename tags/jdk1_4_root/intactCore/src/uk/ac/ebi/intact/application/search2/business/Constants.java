/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.business;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class Constants {

    /**
     * Name of the Log4J logger
     */
    public static final String LOGGER_NAME = "search";

    /**
     * Number of items to display when the display is chunked.
     * ie. count of interactions per chunk when an experiment contains too many of them.
     */
    public static final int MAX_CHUNK_SIZE = 20;

    public static final int NO_CHUNK_SELECTED = -1;
}
