package uk.ac.ebi.intact.application.search3.test;

import com.meterware.httpunit.WebTable;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This test tests the if the url-based query with proteins recieve the binary interaction
 * in which both involved
 * @author Michael Kleen
 * @version BinarTest.java Date: Jan 21, 2005 Time: 1:27:48 PM
 */
public class BinarTest extends SearchTestCase {

    /**
     * Returns this test suite.
     */
    public static Test suite() {
        return new TestSuite(BinarTest.class);
    }
    /**
     * 
     * @throws Exception
     */
    public void test2Proteins() throws Exception {

        WebTable[] content = this.getContentBinaryAction("EBI-1064", "EBI-885");
        String anInteractionShortLabel = content[1].getCellAsText(1, 1).trim();
        String anInteractionAC = content[1].getCellAsText(1, 2).trim();
        String aDescription = content[1].getCellAsText(1, 3).trim();

        assertEquals(anInteractionShortLabel, "ga-5");
        assertEquals(anInteractionAC, "EBI-1485");
        assertEquals(aDescription, "-");

        content = this.getContentBinaryAction("EBI-885", "EBI-1064");
        anInteractionShortLabel = content[1].getCellAsText(1, 1).trim();
        anInteractionAC = content[1].getCellAsText(1, 2).trim();
        aDescription = content[1].getCellAsText(1, 3).trim();

        assertEquals(anInteractionShortLabel, "ga-5");
        assertEquals(anInteractionAC, "EBI-1485");
        assertEquals(aDescription, "-");
    }

    /**
     * This should show the resultTooLarge table because the resultset is to large
     *
     * @throws Exception
     */
    public void testTooMuch() throws Exception {

        WebTable[] content = this.getContentBinaryAction("*", "EBI-1064");
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

        content = this.getContentBinaryAction("EBI-1064", "*");
        anIntactType = content[0].getCellAsText(0, 0).trim();
        aCount = content[0].getCellAsText(0, 1).trim();
        anExperiment = content[0].getCellAsText(1, 0).trim();
        anInteraction = content[0].getCellAsText(2, 0).trim();
        aProtein = content[0].getCellAsText(3, 0).trim();
        aCvTerm = content[0].getCellAsText(4, 0).trim();

        assertEquals(anIntactType, "Intact type");
        assertEquals(aCount, "Count");
        assertEquals(anExperiment, "Experiment");
        assertEquals(anInteraction, "Interaction");
        assertEquals(aProtein, "Protein");
        assertEquals(aCvTerm, "Controlled vocabulary term");
    }
}
