/*
 * Created on 14.07.2004
 *
 */
package uk.ac.ebi.intact.application.mine.business.graph.model.test;

import jdsl.core.ref.NodeSequence;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.mine.business.graph.model.SearchObject;


/**
 * @author Andreas Groscurth
 */
public class SearchObjectTest extends TestCase {

    public SearchObjectTest(String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX()
     * methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchObjectTest.class);
    }
    
    public void test_objectWithPath() {
        SearchObject so = new SearchObject(1);
        assertNotNull(so);
        so.setPath(new NodeSequence());
        assertNotNull(so.getPath());
    }
    
    public void test_bitOperation() {
        SearchObject so = new SearchObject(1);
        assertNotNull(so);
        
        int orI = so.getBitID() | 5;
        so.or(5);
        assertEquals(orI, so.getBitID());   
    }

    public void test_normalObject() {
        SearchObject so = new SearchObject(1);
        assertNotNull(so);
        assertNull(so.getPath());
        assertEquals(Integer.MAX_VALUE, so.getPathLength());  
        assertEquals(1, so.getIndex());     
    }

    public void test_negativeObject() {
        try {
            SearchObject so = new SearchObject(-1);
            fail("no negative index should be allowed !");
        }
        catch (Exception e) {
            // ok
        }
    }
}