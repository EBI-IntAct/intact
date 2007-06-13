package uk.ac.ebi.intact.commons.util;

import java.net.URL;

/**
 * Implemented for those classes that are datasets (for testing)
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface TestDataset
{
    String getId();

    URL getDbUnitDataset();

    boolean containsAllCVs();
}
