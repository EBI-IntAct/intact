package uk.ac.ebi.intact.application.search3.test;

import com.meterware.httpunit.WebTable;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is a very basic test if the toolarge site is vaild 
 * @author Michael Kleen
 * @version BinaryTestCase.java Date: Jan 20, 2005 Time: 2:47:29 PM
 */
public class TooLargeTest extends SearchTestCase {

    /**
     * Returns this test suite.
     */
    public static Test suite() {
        return new TestSuite(TooLargeTest.class);
    }

    /**
     *
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the the
     *          webtable.
     */
    public void testLabels() throws Exception {
        WebTable[] content = this.getContentbySearchAction("*");

        String anIntactType = content[0].getCellAsText(0, 0).trim();
        String aCount = content[0].getCellAsText(0, 1).trim();
        String anExperiment = content[0].getCellAsText(1, 0).trim();
        String anInteraction = content[0].getCellAsText(2, 0).trim();
        String aProtein = content[0].getCellAsText(3, 0).trim();
        String aCvTerm = content[0].getCellAsText(4, 0).trim();

        assertEquals(anIntactType, "Intact type");
        assertEquals(aCount, "Count");
        assertEquals(anExperiment, "Experiment");
        assertEquals(anInteraction, "Interaction");
        assertEquals(aProtein, "Protein");
        assertEquals(aCvTerm, "Controlled vocabulary term");
    }

    /**
     *
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the the
     *         webtable.
     */
    public void testCounts() throws Exception {
        WebTable[] content = this.getContentbySearchAction("*");

        String experimentCount = content[0].getCellAsText(1, 1);
        String interactionCount = content[0].getCellAsText(2, 1);
        String proteinCount = content[0].getCellAsText(3, 1);
        String cvCount = content[0].getCellAsText(4, 1);

        assertNotNull(experimentCount);
        assertNotNull(interactionCount);
        assertNotNull(proteinCount);
        assertNotNull(cvCount);

        assertEquals(experimentCount, "2");
        assertEquals(interactionCount,"1329");
        assertEquals(proteinCount, "2357");
        assertEquals(cvCount, "198");
    }


}
