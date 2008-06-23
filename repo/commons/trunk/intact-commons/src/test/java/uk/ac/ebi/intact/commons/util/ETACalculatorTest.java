package uk.ac.ebi.intact.commons.util;

import org.junit.*;

/**
 * ETACalculator Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 2.1
 * @version $Id$
 */
public class ETACalculatorTest {

    @Test
    public void calculateETA() throws Exception {
        ETACalculator eta = new ETACalculator( 100000 );
        Thread.sleep( 300 );
        long t1 = eta.calculateETA( 500 );
        Thread.sleep( 200 );
        long t2 = eta.calculateETA( 1000 );
        Thread.sleep( 300 );
        long t3 = eta.calculateETA( 2000 );
        Thread.sleep( 200 );
        long t4 = eta.calculateETA( 4000 );

        Assert.assertTrue( t1 > t2 );
        Assert.assertTrue( t2 > t3 );
        Assert.assertTrue( t3 > t4 );
    }

    @Test
    public void calculateETA_2() throws Exception {
        ETACalculator eta = new ETACalculator( 1000 );
        Thread.sleep( 200 );
        final long t0 = eta.calculateETA( 200 );
        final long t1 = eta.calculateETA( 9999 );
        final long t2 = eta.calculateETA( 1000 );

        Assert.assertTrue( t0 > t2 );
        Assert.assertEquals( 0L, t1 ); // because we are beyond the total count
        Assert.assertEquals( 0L, t2 ); // because we processed them all
    }
}
