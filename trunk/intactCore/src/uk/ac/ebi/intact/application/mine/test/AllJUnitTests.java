/*
 * Created on 14.07.2004
 */

package uk.ac.ebi.intact.application.mine.test;

import uk.ac.ebi.intact.application.mine.struts.view.test.AmbiguousBeanTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllJUnitTests extends TestCase {

    /**
     * The constructor with the test name.
     * 
     * @param name the name of the test.
     */
    public AllJUnitTests(final String name) {
        super( name );
    }

    /**
     * Returns a suite containing tests.
     * 
     * @return a suite containing tests. <p/>
     * 
     * <pre>
     * 
     *  
     *   
     *    
     *       post: return != null
     *         post: return-&gt;forall(obj : Object | obj.oclIsTypeOf(TestSuite))
     *                                                                                             
     *    
     *   
     *  
     * </pre>
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite();
        suite
                .addTest( uk.ac.ebi.intact.application.mine.business.graph.test.AllJUnitTests
                        .suite() );
        suite
                .addTest( uk.ac.ebi.intact.application.mine.business.graph.model.test.AllJUnitTests
                        .suite() );
        suite.addTest( AmbiguousBeanTest.suite() );
        return suite;
    }
}