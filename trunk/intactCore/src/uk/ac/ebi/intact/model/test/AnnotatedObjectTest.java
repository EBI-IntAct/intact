/**
 * Created by IntelliJ IDEA.
 * User: hhe
 * Date: Dec 5, 2002
 * Time: 11:54:37 AM
 * To change this template use Options | File Templates.
 */
package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junitx.framework.Assert;
import junitx.framework.ObjectFactory;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.test.util.StringUtils;

public class AnnotatedObjectTest extends TestCase {

    ///////////////////////////
    // instance variable.

    private Institution owner;


    /**
     * Constructor
     *
     * @param name the name of the test.
     */
    public AnnotatedObjectTest( String name ) throws Exception {
        super( name );
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws Exception {
        super.setUp();
        owner = new Institution( "owner" );
    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( AnnotatedObjectTest.class );
    }


    /**
     * Implementation of the AnnotatedObject that will allow testing of its equals and hashCode.
     * we just give access to its constructor and provide a way of setting its AC.
     */
    private class MyAnnotatedObject extends AnnotatedObjectImpl {

        public MyAnnotatedObject( String ac, String shortLabel, Institution owner ) {
            super( shortLabel, owner );
            this.ac = ac;
        }

        public MyAnnotatedObject( String shortLabel, Institution owner ) {
            super( shortLabel, owner );
        }
        // nothing more , the class is abstract but implements everything already ;)
    }


    /////////////////////////////////
    // Utility methods

    private AnnotatedObject createAnnotatedObject() {

        AnnotatedObject ao = new MyAnnotatedObject( "EBI-yyy", "test", owner );

        return ao;
    }


    /////////////////////////////////
    // Tests

    public void testSetShortlabel() {

        AnnotatedObject ao = createAnnotatedObject();
        assertNotNull( ao );

        // trimming
        String shortlabel = "   myAnnotatedObject   ";
        ao.setShortLabel( shortlabel );
        assertEquals( shortlabel.trim(), ao.getShortLabel() );

        // null shortlabel
        try {
            ao.setShortLabel( null );
            fail( "null shortlabel should not be allowed." );
        } catch ( Exception e ) {
            // ok
        }

        // empty shortlabel
        try {
            ao.setShortLabel( "   " );
            fail( "empty shortlabel should not be allowed." );
        } catch ( Exception e ) {
            // ok
        }

        // check truncation
        shortlabel = StringUtils.generateStringOfLength( AnnotatedObject.MAX_SHORT_LABEL_LEN + 10 );
        ao.setShortLabel( shortlabel );
        assertEquals( AnnotatedObject.MAX_SHORT_LABEL_LEN,
                      ao.getShortLabel().length() );
    }

    public void testSetFullName() {

        AnnotatedObject ao = createAnnotatedObject();
        assertNotNull( ao );

        // trimming
        String fullName = "   myAnnotatedObject   ";
        ao.setFullName( fullName );
        assertEquals( fullName.trim(), ao.getFullName() );

        // null fullName
        try {
            ao.setFullName( null );
        } catch ( Exception e ) {
            fail( "null fullname should be allowed." );
        }

        // empty fullName
        try {
            ao.setFullName( "   " );
            assertEquals( "", ao.getFullName() );
        } catch ( Exception e ) {
            fail( "null fullname should be allowed." );
        }
    }

    public void testEqualsAndHashCode() {

        // shortlabel are not equals
        ObjectFactory factory = new ObjectFactory() {
            public Object createInstanceX() {
                return new MyAnnotatedObject( "EBI-yyy", "shortlabel 1", owner );
            }

            public Object createInstanceY() {
                return new MyAnnotatedObject( "EBI-yyy", "shortlabel 2", owner );
            }
        };

        System.out.println( factory.createInstanceX().getClass().getName() );
        System.out.println( factory.createInstanceY().getClass().getName() );
        System.out.println(
                "TYPE: " + ( factory.createInstanceX().getClass() == factory.createInstanceY().getClass() ) );
        System.out.println( factory.createInstanceX().equals( factory.createInstanceY() ) );

        // Make sure the object factory meets its contract for testing.
        // This contract is specified in the API documentation.
        Assert.assertObjectFactoryContract( factory );
        // Assert equals(Object) contract.
        Assert.assertEqualsContract( factory );
        // Assert hashCode() contract.
        Assert.assertHashCodeContract( factory );


        /////////////////////////////////////////////////////////
        // fullName are not equals
        factory = new ObjectFactory() {
            public Object createInstanceX() {

                AnnotatedObject ao = new MyAnnotatedObject( "EBI-yyy", "shortlabel", owner );
                ao.setFullName( "a nice fullName" );
                return ao;
            }

            public Object createInstanceY() {
                return new MyAnnotatedObject( "EBI-yyy", "shortlabel", owner );
            }
        };

        Assert.assertObjectFactoryContract( factory );
        Assert.assertEqualsContract( factory );
        Assert.assertHashCodeContract( factory );


        /////////////////////////////////////////////////////////
        // xref collection are not equals
        final CvDatabase go = new CvDatabase( owner, "go" );
        factory = new ObjectFactory() {
            public Object createInstanceX() {
                MyAnnotatedObject ao = new MyAnnotatedObject( "EBI-yyy", "shortlabel", owner );
                ao.addXref( new Xref( owner, go, "GO:0000000", null, null, null ) );
                return ao;
            }

            public Object createInstanceY() {
                return new MyAnnotatedObject( "EBI-yyy", "shortlabel", owner );
            }
        };

        Assert.assertObjectFactoryContract( factory );
        Assert.assertEqualsContract( factory );
        Assert.assertHashCodeContract( factory );


        // Note: AC, aliases and annotations are not taken into account in the equals/hashCode.
    }
}
