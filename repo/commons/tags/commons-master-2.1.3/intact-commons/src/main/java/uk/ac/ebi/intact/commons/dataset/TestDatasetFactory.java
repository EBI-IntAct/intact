package uk.ac.ebi.intact.commons.dataset;

import uk.ac.ebi.intact.commons.dataset.impl.DbUnitTestDatasetImpl;
import uk.ac.ebi.intact.commons.dataset.impl.TestDatasetImpl;

import java.io.IOException;
import java.net.URL;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class TestDatasetFactory
{
    private TestDatasetFactory() {}

    public static TestDataset createTestDataset(String id, URL sourceUrl, boolean containsAllCvs) throws IOException
    {
        return new TestDatasetImpl(id, sourceUrl.openStream(), containsAllCvs);
    }

    public static DbUnitTestDataset createDbUnitTestDataset(String id, URL sourceUrl, boolean containsAllCvs, String formatType) throws IOException
    {
        return new DbUnitTestDatasetImpl(id, sourceUrl.openStream(), containsAllCvs, formatType);
    }

    
}
