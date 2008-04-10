package uk.ac.ebi.intact.commons.dataset.impl;

import uk.ac.ebi.intact.commons.dataset.DbUnitTestDataset;

import java.io.InputStream;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DbUnitTestDatasetImpl extends TestDatasetImpl implements DbUnitTestDataset
{
    private String formatType;

    public DbUnitTestDatasetImpl(String id, InputStream source, String formatType)
    {
        super(id, source);
        this.formatType = formatType;
    }

    public DbUnitTestDatasetImpl(String id, InputStream source, boolean containsAllCVs, String formatType)
    {
        super(id, source, containsAllCVs);
        this.formatType = formatType;
    }

    public String getFormatType()
    {
        return formatType;
    }
}
