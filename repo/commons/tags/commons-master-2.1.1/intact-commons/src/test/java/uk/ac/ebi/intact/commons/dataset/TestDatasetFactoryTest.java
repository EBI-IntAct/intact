package uk.ac.ebi.intact.commons.dataset;

import static org.junit.Assert.*;
import org.junit.Test;

import java.net.URL;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class TestDatasetFactoryTest
{
    
    @Test
    public void createTestDataset_usingURL() throws Exception {
        String id = "id";
        URL dummyUrl = TestDatasetFactoryTest.class.getResource(getClass().getSimpleName()+".class");
        boolean containsAllCvs = true;

        TestDataset testDataset = TestDatasetFactory.createTestDataset(id, dummyUrl, containsAllCvs);

        assertNotNull(testDataset);
        assertEquals(id, testDataset.getId());
        assertNotNull(testDataset.getSource());
        assertTrue(testDataset.containsAllCVs());
    }

    @Test
    public void createDbUnitDataset_usingURL() throws Exception {
        String id = "id2";
        URL dummyUrl = TestDatasetFactoryTest.class.getResource(getClass().getSimpleName()+".class");
        boolean containsAllCvs = false;
        String type = "xml";

        DbUnitTestDataset testDataset = TestDatasetFactory.createDbUnitTestDataset(id, dummyUrl, containsAllCvs, type);

        assertNotNull(testDataset);
        assertEquals(id, testDataset.getId());
        assertNotNull(testDataset.getSource());
        assertFalse(testDataset.containsAllCVs());
        assertEquals(type, testDataset.getFormatType());
    }
}
