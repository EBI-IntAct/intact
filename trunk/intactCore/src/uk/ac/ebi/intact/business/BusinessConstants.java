package uk.ac.ebi.intact.business;

/**
 * Constants used for the business package.
 *
 * @author intact team
 * @version $Id$
 */
public class BusinessConstants {

    /**
     * Expand a complex to all pairwise interactions.
     */
    public static final int EXPANSION_ALL = 0;

    /**
     * Expand a complex into all bait-prey pairs. If no bait is defined in the complex,
     * the FIRST interactor is considered to be the bait. A warning should be given.
     */
    public static final int EXPANSION_BAITPREY = 1;


    /**
     * used as a key to identify a mapping filename (for Castor)
     * the value is defined in the web.xml file
     */
    public static final String MAPPING_FILE_KEY = "mappingfile";

}
