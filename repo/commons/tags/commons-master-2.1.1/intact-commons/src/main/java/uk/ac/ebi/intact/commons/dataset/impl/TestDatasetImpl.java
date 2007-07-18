package uk.ac.ebi.intact.commons.dataset.impl;

import uk.ac.ebi.intact.commons.dataset.TestDataset;

import java.io.InputStream;
import java.util.Collection;

/**
 * Default implementation of a TestDataset
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class TestDatasetImpl implements TestDataset
{
    private String id;
    private InputStream source;
    private boolean containsAllCVs;

    public TestDatasetImpl(String id, InputStream source)
    {
        this.id = id;
        this.source = source;
    }

    public TestDatasetImpl(String id, InputStream source, boolean containsAllCVs)
    {
        this.id = id;
        this.source = source;
        this.containsAllCVs = containsAllCVs;
    }

    public String getId()
    {
        return id;
    }

    public InputStream getSource()
    {
        return source;
    }

    public boolean containsAllCVs()
    {
        return containsAllCVs;
    }

    public Collection<String> getAvailableIds() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestDatasetImpl that = (TestDatasetImpl) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return (id != null ? id.hashCode() : 0);
    }
}
