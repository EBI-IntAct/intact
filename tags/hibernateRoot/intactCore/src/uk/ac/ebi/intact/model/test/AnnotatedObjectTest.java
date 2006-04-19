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
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.AnnotatedObjectImpl;
import uk.ac.ebi.intact.model.Institution;
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
}
