package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.*;

public class XrefTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public XrefTest( String name ) throws Exception {
        super( name );
    }

    private Institution owner = new Institution( "EBI" );

    private CvTopic validation = new CvTopic( owner, CvTopic.XREF_VALIDATION_REGEXP );
    private CvTopic comment = new CvTopic( owner, "comment" );
    private CvTopic remark = new CvTopic( owner, "remark" );
    private CvXrefQualifier identity = new CvXrefQualifier( owner, "identity" );


    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( XrefTest.class );
    }

    public void testSetSecondaryId() {

        CvDatabase go = new CvDatabase( owner, "uniprotkb" );
        Xref xref = null;

        String id = "GO:0000001";
        String secId = "description of id";

        xref = new Xref( owner, go, id, secId, null, identity );
        assertEquals( secId.trim(), xref.getSecondaryId() );

        // check triming
        secId = "   description of id ";
        xref = new Xref( owner, go, id, secId, null, identity );
        assertEquals( secId.trim(), xref.getSecondaryId() );


        // checking null id
        secId = null;
        xref = new Xref( owner, go, id, secId, null, identity );
        assertNull( xref.getSecondaryId() );

        // checking empty id
        secId = "   ";
        xref = new Xref( owner, go, id, secId, null, identity );
        assertEquals( secId.trim(), xref.getSecondaryId() );
    }

    public void testSetPrimaryId() {

        CvDatabase go = new CvDatabase( owner, "uniprotkb" );
        Xref xref = null;

        String id = "GO:0000001";
        xref = new Xref( owner, go, id, null, null, identity );
        assertEquals( id.trim(), xref.getPrimaryId() );

        // check triming
        id = "   GO:0000001 ";
        xref = new Xref( owner, go, id, null, null, identity );
        assertEquals( id.trim(), xref.getPrimaryId() );


        try {
            // checking null id
            xref = new Xref( owner, go, null, null, null, identity );
            fail( "you must give a primaryID" );
        } catch ( Exception e ) {
            // ok
        }

        try {
            // checking empty id
            xref = new Xref( owner, go, "  ", null, null, identity );
            fail( "you must give a non empty primaryID" );
        } catch ( Exception e ) {
            // ok
        }
    }

    /**
     * Create a protein, then try to add an Annotation to it.
     */
    public void testHasValidPrimaryId() {

        CvDatabase go = new CvDatabase( owner, "uniprotkb" );

        go.addAnnotation( new Annotation( owner, comment, "an interresting comment." ) );
        go.addAnnotation( new Annotation( owner, comment, "an other very interresting comment." ) );
        go.addAnnotation( new Annotation( owner, validation, "GO:[0-9]{7}" ) );
        go.addAnnotation( new Annotation( owner, remark, "a remark." ) );

        Xref xref = null;

        xref = new Xref( owner, go, "GO:0000000", null, null, identity );
        assertTrue( xref.hasValidPrimaryId() );

        xref = new Xref( owner, go, "GO:000", null, null, identity );
        assertFalse( xref.hasValidPrimaryId() );

        xref = new Xref( owner, go, "29483732", null, null, identity );
        assertFalse( xref.hasValidPrimaryId() );

        xref = new Xref( owner, go, "GO:111A111", null, null, identity );
        assertFalse( xref.hasValidPrimaryId() );
    }
}
