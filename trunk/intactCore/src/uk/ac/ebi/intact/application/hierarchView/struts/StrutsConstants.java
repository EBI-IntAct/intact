package uk.ac.ebi.intact.application.hierarchView.struts;


/**
 * Manifest constants for the hierarchView application.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

public final class StrutsConstants {

    // ******************************************************* Properties files

    /**
     * Where to find the properties file
     */
    public static final String WEB_SERVICE_PROPERTY_FILE = "/config/WebService.properties";

    public static final String HIGHLIGHTING_PROPERTY_FILE = "/config/Highlighting.properties";

    public static final String GRAPH_PROPERTY_FILE = "/config/Graph.properties";


    // ********************************************************* Request parameters

    /**
     * The name of the keys attribute set in the session.
     */
    public static final String ATTRIBUTE_KEYS = "keys";

    /**
     * The name of the HTTP attribute to describe the URL of the highlight source.
     */
    public static final String ATTRIBUTE_SOURCE_URL = "url";
}

























