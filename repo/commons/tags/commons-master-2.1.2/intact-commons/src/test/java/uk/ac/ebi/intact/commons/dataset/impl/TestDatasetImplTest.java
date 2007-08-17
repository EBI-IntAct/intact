package uk.ac.ebi.intact.commons.dataset.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.commons.dataset.TestDataset;
import uk.ac.ebi.intact.commons.dataset.TestDatasetFactory;

import java.net.URL;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class TestDatasetImplTest
{
    @Test
    public void testEquals_sameId() throws Exception {
        final String id = "id1";
        final URL dummyURL = TestDatasetImplTest.class.getResource(getClass().getSimpleName()+".class");
        TestDataset ds1 = TestDatasetFactory.createTestDataset(id, dummyURL, false);
        TestDataset ds2 = TestDatasetFactory.createTestDataset(id, dummyURL, false);

        Assert.assertEquals(ds1, ds2);
    }

    @Test
    public void testEquals_differentId() throws Exception {
        final URL dummyURL = TestDatasetImplTest.class.getResource(getClass().getSimpleName()+".class");
        TestDataset ds1 = TestDatasetFactory.createTestDataset("id1", dummyURL, false);
        TestDataset ds2 = TestDatasetFactory.createTestDataset("id2", dummyURL, false);

        Assert.assertNotSame(ds1, ds2);
    }
}
