/**
 * Generated by Agitar build: Agitator Version 1.0.4.000276 (Build date: Mar 27, 2007) [1.0.4.000276]
 * JDK Version: 1.5.0_09
 *
 * Generated on 04-Apr-2007 08:28:26
 * Time to generate: 00:08.562 seconds
 *
 */

package agitar.uk.ac.ebi.intact.modelt; import uk.ac.ebi.intact.model.*;

import com.agitar.lib.junit.AgitarTestCase;

public class PayAsYouGoCurrentEdgeAgitarTest extends AgitarTestCase {

    static Class TARGET_CLASS = PayAsYouGoCurrentEdge.class;

    public void testConstructor() throws Throwable {
        PayAsYouGoCurrentEdge payAsYouGoCurrentEdge = new PayAsYouGoCurrentEdge();
        assertEquals( "payAsYouGoCurrentEdge.getConf()", 0, payAsYouGoCurrentEdge.getConf() );
    }

    public void testEquals() throws Throwable {
        PayAsYouGoCurrentEdge payAsYouGoCurrentEdge = new PayAsYouGoCurrentEdge();
        payAsYouGoCurrentEdge.setSeen( 100 );
        boolean result = payAsYouGoCurrentEdge.equals( new PayAsYouGoCurrentEdge() );
        assertFalse( "result", result );
    }

    public void testEquals1() throws Throwable {
        PayAsYouGoCurrentEdge o = new PayAsYouGoCurrentEdge();
        o.setPk( new PayAsYouGoCurrentEdgePk() );
        boolean result = new PayAsYouGoCurrentEdge().equals( o );
        assertFalse( "result", result );
    }

    public void testEquals2() throws Throwable {
        boolean result = new PayAsYouGoCurrentEdge().equals( new PayAsYouGoCurrentEdge() );
        assertTrue( "result", result );
    }

    public void testEquals3() throws Throwable {
        PayAsYouGoCurrentEdge payAsYouGoCurrentEdge = new PayAsYouGoCurrentEdge();
        payAsYouGoCurrentEdge.setConf( 100 );
        boolean result = payAsYouGoCurrentEdge.equals( new PayAsYouGoCurrentEdge() );
        assertFalse( "result", result );
    }

    public void testEquals4() throws Throwable {
        PayAsYouGoCurrentEdgePk pk = new PayAsYouGoCurrentEdgePk();
        PayAsYouGoCurrentEdge payAsYouGoCurrentEdge = new PayAsYouGoCurrentEdge();
        payAsYouGoCurrentEdge.setPk( pk );
        payAsYouGoCurrentEdge.setConf( 42 );
        PayAsYouGoCurrentEdge o = new PayAsYouGoCurrentEdge();
        o.setPk( pk );
        o.setConf( 42 );
        boolean result = payAsYouGoCurrentEdge.equals( o );
        assertTrue( "result", result );
        assertSame( "payAsYouGoCurrentEdge.getPk()", pk, payAsYouGoCurrentEdge.getPk() );
    }

    public void testEquals5() throws Throwable {
        PayAsYouGoCurrentEdgePk pk = new PayAsYouGoCurrentEdgePk();
        PayAsYouGoCurrentEdge payAsYouGoCurrentEdge = new PayAsYouGoCurrentEdge();
        payAsYouGoCurrentEdge.setPk( pk );
        boolean result = payAsYouGoCurrentEdge.equals( new PayAsYouGoCurrentEdge() );
        assertFalse( "result", result );
        assertSame( "payAsYouGoCurrentEdge.getPk()", pk, payAsYouGoCurrentEdge.getPk() );
    }

    public void testEquals6() throws Throwable {
        boolean result = new PayAsYouGoCurrentEdge().equals( null );
        assertFalse( "result", result );
    }

    public void testEquals7() throws Throwable {
        boolean result = new PayAsYouGoCurrentEdge().equals( "testString" );
        assertFalse( "result", result );
    }

    public void testEquals8() throws Throwable {
        PayAsYouGoCurrentEdge o = new PayAsYouGoCurrentEdge();
        boolean result = o.equals( o );
        assertTrue( "result", result );
    }

    public void testHashCode() throws Throwable {
        PayAsYouGoCurrentEdge payAsYouGoCurrentEdge = new PayAsYouGoCurrentEdge();
        payAsYouGoCurrentEdge.setConf( 100 );
        int result = payAsYouGoCurrentEdge.hashCode();
        assertEquals( "result", 100, result );
        assertNull( "payAsYouGoCurrentEdge.getPk()", payAsYouGoCurrentEdge.getPk() );
    }

    public void testHashCode1() throws Throwable {
        PayAsYouGoCurrentEdgePk pk = new PayAsYouGoCurrentEdgePk( "testPayAsYouGoCurrentEdgeNidA", "testPayAsYouGoCurrentEdgeNidB", "testPayAsYouGoCurrentEdgeSpecies" );
        PayAsYouGoCurrentEdge payAsYouGoCurrentEdge = new PayAsYouGoCurrentEdge();
        payAsYouGoCurrentEdge.setPk( pk );
        int result = payAsYouGoCurrentEdge.hashCode();
        assertEquals( "result", -702833180, result );
        assertSame( "payAsYouGoCurrentEdge.getPk()", pk, payAsYouGoCurrentEdge.getPk() );
    }

    public void testHashCode2() throws Throwable {
        PayAsYouGoCurrentEdge payAsYouGoCurrentEdge = new PayAsYouGoCurrentEdge();
        int result = payAsYouGoCurrentEdge.hashCode();
        assertEquals( "result", 0, result );
        assertNull( "payAsYouGoCurrentEdge.getPk()", payAsYouGoCurrentEdge.getPk() );
    }

    public void testSetConf() throws Throwable {
        PayAsYouGoCurrentEdge payAsYouGoCurrentEdge = new PayAsYouGoCurrentEdge();
        payAsYouGoCurrentEdge.setConf( 100 );
        assertEquals( "payAsYouGoCurrentEdge.getConf()", 100, payAsYouGoCurrentEdge.getConf() );
    }

    public void testSetPk() throws Throwable {
        PayAsYouGoCurrentEdge payAsYouGoCurrentEdge = new PayAsYouGoCurrentEdge();
        PayAsYouGoCurrentEdgePk pk = new PayAsYouGoCurrentEdgePk();
        payAsYouGoCurrentEdge.setPk( pk );
        assertSame( "payAsYouGoCurrentEdge.getPk()", pk, payAsYouGoCurrentEdge.getPk() );
    }

    public void testSetSeen() throws Throwable {
        PayAsYouGoCurrentEdge payAsYouGoCurrentEdge = new PayAsYouGoCurrentEdge();
        payAsYouGoCurrentEdge.setSeen( 100 );
        assertEquals( "payAsYouGoCurrentEdge.getSeen()", 100, payAsYouGoCurrentEdge.getSeen() );
    }
}

