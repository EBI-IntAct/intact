/*
 * Created on 14.07.2004
 *  
 */
package uk.ac.ebi.intact.application.mine.business.graph.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.mine.business.graph.model.NetworkKey;

/**
 * @author Andreas Groscurth
 */
public class NetworkKeyTest extends TestCase {

    public NetworkKeyTest(String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX()
     * methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(NetworkKeyTest.class);
    }

    public void test_normalObject() {
        NetworkKey nk = new NetworkKey("test", 1);
        assertNotNull(nk);
        assertEquals(nk.getBioSource(), "test");
        assertEquals(1, nk.getGraphID());
    }
}