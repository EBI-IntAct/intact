/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */

package uk.ac.ebi.intact.application.mine.business;

import org.apache.log4j.Logger;

/**
 * The class <tt>Constants</tt> stores several constants which are used in the
 * MiNe application.
 * 
 * @author Andreas Groscurth
 */
public class Constants {
    // the name of the logger
    public static final String LOGGER_NAME = "mine";
    // the logger itself
    public static final Logger LOGGER = Logger.getLogger( LOGGER_NAME );
    public static final String USER = "user";
    public static final String PARAMETER = "AC";
    public static final String ERROR = "error";
    public static final String SUCCESS = "success";
    public static final String AMBIGOUS = "ambiguous";
    public static final int MAX_NUMBER_RESULTS = 20;
    public static final int MAX_INTERACTION_SIZE = 5;
    public static final int MAX_SEARCH_NUMBER = 7;
    public static final String SEARCH = "search";
    public static final Integer SINGLETON_GRAPHID = new Integer(
            Integer.MIN_VALUE );
    public static final String GRAPH_HELPER = "graphHelper";
}