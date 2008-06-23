package uk.ac.ebi.intact.commons.util.net;

import java.io.IOException;
import java.net.URL;

/**
 * Filter for URLs
 *
 * @author Bruno Aranda (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface UrlFilter
{

    boolean accept(URL url) throws IOException;

}