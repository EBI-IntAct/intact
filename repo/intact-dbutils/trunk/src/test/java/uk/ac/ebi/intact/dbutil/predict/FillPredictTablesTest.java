package uk.ac.ebi.intact.dbutil.predict;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.context.IntactContext;

/**
 * FillPredictTables Tester.
 *
 * @author <Authors name>
 * @since <pre>11/15/2006</pre>
 * @version 1.0
 */
public class FillPredictTablesTest extends TestCase {
    public FillPredictTablesTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    public static Test suite() {
        return new TestSuite(FillPredictTablesTest.class);
    }

    ////////////////////
    // Tests

    public void testMain() throws Exception {

        FillPredictTables.main( new String[]{} );
    }
}
