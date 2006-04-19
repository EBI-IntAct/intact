/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.setupGoDensity;

/**
 * Some goDensity specific Settings
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */
public final class Config {

    // ============================================================================
    // S E T   D B   -  comment used and uncomment unused db !
    // ============================================================================

    public static final String DB = "ORACLE";
    //public static final String DB = "POSTGRESQL";


    // ============================================================================
    // I M A G E
    // ============================================================================

    /**
     * Imagesize x and y
     */
    public static final int IMAGeSIZE = 600;

    /**
     * BigOffset is the left and bottom part of the image (text-area)
     */
    public static final int BIgOFFSET = 160;

    /**
     * SmallOffset is the right and top area of the image
     */
    public static final int SMALlOFFSET = 20;


    // ============================================================================
    // D E N S I T Y   C A L C U L A T I O N
    // ============================================================================

    /**
     * <p>
     * - true -> only precalculated go-go-densities will be displayed.
     * If the user chooses a GO:ID which isn't precalculated, then "NoData" will
     * be displayed in the equivalent cluster of the image</p>
     * <p>
     * - false -> for all densities, where precalculated data is available, this
     * will be used. Rest will be calculated LIVE (be careful, can take hours!)
     */
    public static final boolean USeONLyPRECALCULATIOnDATA = true;


}
