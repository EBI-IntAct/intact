package uk.ac.ebi.intact.commons.util.impl;

import uk.ac.ebi.intact.commons.util.TestDataset;

import java.net.URL;

/**
 * Default implementation of a TestDataset
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class TestDatasetImpl implements TestDataset
{
    private String id;
    private URL dbUnitDatasetUrl;
    private boolean containsAllCVs;

    public TestDatasetImpl(String id, URL dbUnitDatasetUrl)
    {
        this.id = id;
        this.dbUnitDatasetUrl = dbUnitDatasetUrl;
    }

    public TestDatasetImpl(String id, URL dbUnitDatasetUrl, boolean containsAllCVs)
    {
        this.id = id;
        this.dbUnitDatasetUrl = dbUnitDatasetUrl;
        this.containsAllCVs = containsAllCVs;
    }

    public String getId()
    {
        return id;
    }

    public URL getDbUnitDataset()
    {
        return dbUnitDatasetUrl;
    }

    public boolean containsAllCVs()
    {
        return containsAllCVs;
    }
}
