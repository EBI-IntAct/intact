/*
 * Created on 14.07.2004
 *
 */
package uk.ac.ebi.intact.application.mine.business.graph.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.mine.business.graph.MineStorage;
import uk.ac.ebi.intact.application.mine.business.graph.Storage;


/**
 * @author Andreas Groscurth
 */
public class StorageTest extends TestCase {

    public StorageTest(String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX()
     * methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(StorageTest.class);
    }
    
    public void test_normalObject() {
        Storage st = new MineStorage(10);
        assertNotNull(st);
    }
}
