/**
 * Generated by Agitar build: Agitator Version 1.0.4.000276 (Build date: Mar 27, 2007) [1.0.4.000276]
 * JDK Version: 1.5.0_09
 *
 * Generated on 04-Apr-2007 08:31:57
 * Time to generate: 01:53.004 seconds
 *
 */

package agitar.uk.ac.ebi.intact.modelt; import uk.ac.ebi.intact.model.*;

import com.agitar.lib.junit.AgitarTestCase;
import com.agitar.lib.mockingbird.Mockingbird;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import java.util.ArrayList;
import java.util.Collection;

public class CvObjectAgitarTest extends AgitarTestCase {

    static Class TARGET_CLASS = CvObject.class;

    public void testEquals() throws Throwable {
        boolean result = new CvGoNode().equals( new Object() );
        assertFalse( "result", result );
    }

    public void testEquals1() throws Throwable {
        CvObject obj = new CvTopic( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" );
        obj.setAc( "testCvObjectAc" );
        boolean result = obj.equals( obj );
        assertTrue( "result", result );
    }

//    public void testEquals2() throws Throwable {
//        CvObject cvAliasType = new CvAliasType( null, "testCvObjectShortLabel" );
//        cvAliasType.setAc( "testString" );
//        CvCellCycle obj = ( CvCellCycle ) Mockingbird.getProxyObject( CvCellCycle.class );
//        Mockingbird.enterRecordingMode();
//        Mockingbird.setReturnValue( obj.getAc(), "testString" );
//        Mockingbird.setReturnValue( obj.getAc(), "testString" );
//        Mockingbird.setReturnValue( obj.getAc(), "testString" );
//        Mockingbird.setReturnValue( obj.getAc(), "testString" );
//        Mockingbird.enterTestMode();
//        boolean result = cvAliasType.equals( obj );
//        assertTrue( "result", result );
//        assertInvoked( obj, "getAc", 4 );
//    }

    public void testEquals3() throws Throwable {
        boolean result = new CvXrefQualifier( null, "testCvObject\rShortLabel" ).equals( new CvTopic( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" ) );
        assertFalse( "result", result );
    }

    public void testEquals4() throws Throwable {
        CvObject cvDevelopmentalStage = new CvDevelopmentalStage( null, "testString" );
        boolean result = cvDevelopmentalStage.equals( new CvDatabase( new Institution( "testCvObjectShortLabel" ), "testString" ) );
        assertTrue( "result", result );
    }

    public void testGetAliases() throws Throwable {
        ArrayList result = ( ArrayList ) new CvFuzzyType( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" ).getAliases();
        assertEquals( "result.size()", 0, result.size() );
    }

    public void testGetAliases1() throws Throwable {
        CvObject cvDatabase = new CvDatabase( new Institution( "testCvObjectShortLabel1" ), "testCvObjectShortLabel" );
        Alias alias = new CvObjectAlias( new Institution( "testCvObjectShortLabel" ), new CvDatabase( null, "testCvObjectShortLabel1" ), ( CvAliasType ) Mockingbird.getProxyObject( CvAliasType.class ), "testCvObjectName" );
        cvDatabase.addAlias( ( CvObjectAlias ) alias );
        Mockingbird.enterTestMode();
        ArrayList result = ( ArrayList ) cvDatabase.getAliases();
        assertEquals( "result.size()", 1, result.size() );
        assertTrue( "(ArrayList) result.contains(alias)", result.contains( alias ) );
    }

    public void testGetAnnotations() throws Throwable {
        CvObject cvFeatureType = new CvFeatureType( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" );
        Collection someAnnotation = new ArrayList( 100 );
        cvFeatureType.setAnnotations( someAnnotation );
        cvFeatureType.addAnnotation( null );
        Collection result = cvFeatureType.getAnnotations();
        assertSame( "result", someAnnotation, result );
        assertTrue( "(ArrayList) someAnnotation.contains(null)", someAnnotation.contains( null ) );
    }

    public void testGetAnnotations1() throws Throwable {
        ArrayList result = ( ArrayList ) new CvDatabase( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" ).getAnnotations();
        assertEquals( "result.size()", 0, result.size() );
    }

    public void testGetXrefs() throws Throwable {
        ArrayList result = ( ArrayList ) new CvExperimentalRole( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" ).getXrefs();
        assertEquals( "result.size()", 0, result.size() );
    }

    public void testGetXrefs1() throws Throwable {
        Institution owner = new Institution( "testCvObjectShortLabel" );
        CvDatabase aDatabase = new CvDatabase( new Institution( "testCvObjectShortLabel1" ), "testCvObjectShortLabel" );
        Xref aXref = new CvObjectXref( owner, aDatabase, "testCvObjectAPrimaryId", new CvXrefQualifier( owner, "testCvObjectShortLabel" ) );
        aDatabase.addXref( ( CvObjectXref ) aXref );
        ArrayList result = ( ArrayList ) aDatabase.getXrefs();
        assertEquals( "result.size()", 1, result.size() );
        assertTrue( "(ArrayList) result.contains(aXref)", result.contains( aXref ) );
    }

    public void testHashCode() throws Throwable {
        CvObject cvInteractionType = new CvInteractionType( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" );
        cvInteractionType.setShortLabel( null );
        int result = cvInteractionType.hashCode();
        assertEquals( "result", 872, result );
        assertEquals( "(CvInteractionType) cvInteractionType.xrefs.size()", 0, ( ( CvInteractionType ) cvInteractionType ).xrefs.size() );
        assertNull( "(CvInteractionType) cvInteractionType.shortLabel", ( ( CvInteractionType ) cvInteractionType ).getShortLabel() );
        assertNull( "(CvInteractionType) cvInteractionType.ac", ( ( CvInteractionType ) cvInteractionType ).getAc());
        assertNull( "(CvInteractionType) cvInteractionType.fullName", ( ( CvInteractionType ) cvInteractionType ).getFullName());
    }

    public void testHashCode1() throws Throwable {
        CvObject cvIdentification = new CvIdentification( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" );
        int result = cvIdentification.hashCode();
        assertEquals( "result", -165825989, result );
        assertEquals( "(CvIdentification) cvIdentification.xrefs.size()", 0, ( ( CvIdentification ) cvIdentification ).xrefs.size() );
        assertEquals( "(CvIdentification) cvIdentification.shortLabel", "testCvObjectShortLab", ( ( CvIdentification ) cvIdentification ).getShortLabel() );
        assertNull( "(CvIdentification) cvIdentification.ac", ( ( CvIdentification ) cvIdentification ).getAc());
        assertNull( "(CvIdentification) cvIdentification.fullName", ( ( CvIdentification ) cvIdentification ).getFullName());
    }

//    public void testHashCode2() throws Throwable {
//        Institution owner = ( Institution ) Mockingbird.getProxyObject( Institution.class );
//        Mockingbird.enterTestMode();
//        CvObject cvComponentRole = new CvComponentRole( owner, "testCvObjectShortLabel" );
//        Mockingbird.getProxyObject( Institution.class );
//        Mockingbird.enterTestMode();
//        Mockingbird.enterRecordingMode();
//        Mockingbird.setReturnValue( CvObjectUtils.getPsiMiIdentityXref( cvComponentRole ), null );
//        Mockingbird.enterTestMode();
//        cvComponentRole.hashCode();
//        Mockingbird.getProxyObject( Institution.class );
//        Mockingbird.getProxyObject( CvDatabase.class );
//        Mockingbird.getProxyObject( CvXrefQualifier.class );
//        Mockingbird.enterTestMode();
//        Mockingbird.getProxyObject( Institution.class );
//        Mockingbird.getProxyObject( CvDatabase.class );
//        Mockingbird.getProxyObject( CvXrefQualifier.class );
//        Mockingbird.enterTestMode();
//        Institution anOwner = ( Institution ) Mockingbird.getProxyObject( Institution.class );
//        CvDatabase aDatabase = ( CvDatabase ) Mockingbird.getProxyObject( CvDatabase.class );
//        CvXrefQualifier aCvXrefQualifier = ( CvXrefQualifier ) Mockingbird.getProxyObject( CvXrefQualifier.class );
//        Mockingbird.enterTestMode();
//        Xref cvObjectXref = new CvObjectXref( anOwner, aDatabase, "testCvObjectAPrimaryId", aCvXrefQualifier );
//        Mockingbird.enterRecordingMode();
//        Mockingbird.setReturnValue( cvObjectXref.getPrimaryId(), "20\u001B,72:GT{z4*\u001B\u000EgR/17[" );
//        Mockingbird.setReturnValue( CvObjectUtils.getPsiMiIdentityXref( cvComponentRole ), cvObjectXref );
//        Mockingbird.enterTestMode();
//        int result = cvComponentRole.hashCode();
//        assertEquals( "result", 858403292, result );
//        assertEquals( "(CvComponentRole) cvComponentRole.xrefs.size()", 0, ( ( CvComponentRole ) cvComponentRole ).xrefs.size() );
//        assertEquals( "(CvComponentRole) cvComponentRole.shortLabel", "testCvObjectShortLab", ( ( CvComponentRole ) cvComponentRole ).shortLabel );
//        assertNull( "(CvComponentRole) cvComponentRole.ac", ( ( CvComponentRole ) cvComponentRole ).getAc());
//        assertNull( "(CvComponentRole) cvComponentRole.fullName", ( ( CvComponentRole ) cvComponentRole ).getFullName());
//        assertInvoked( cvObjectXref, "setOwner", new Object[]{anOwner} );
//        assertInvoked( cvObjectXref, "getPrimaryId" );
//    }

    public void testSetObjClass() throws Throwable {
        CvObject cvInteraction = new CvInteraction( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" );
        cvInteraction.setObjClass( "testCvObjectObjClass" );
        assertEquals( "(CvInteraction) cvInteraction.getObjClass()", "testCvObjectObjClass", cvInteraction.getObjClass() );
    }

    public void testEqualsThrowsNullPointerException() throws Throwable {
        CvObject obj = new CvCellCycle( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" );
        obj.addXref( new CvObjectXref( null, null, "testCvObjectAPrimaryId", null ) );
        obj.setAc( null );
        try {
            obj.equals( obj );
            fail( "Expected NullPointerException to be thrown" );
        } catch ( NullPointerException ex ) {
            assertEquals( "ex.getMessage()", "cvObject should not be null", ex.getMessage() );
            assertThrownBy( CvObjectUtils.class, ex );
        }
    }

    public void testGetIdentityXrefThrowsIndexOutOfBoundsException() throws Throwable {
        CvObject cvGoNode = new CvGoNode();
        cvGoNode.addXref( new CvObjectXref( null, null, "testCvObjectAPrimaryId", null ) );
        try {
            cvGoNode.getIdentityXref();
            fail( "Expected IndexOutOfBoundsException to be thrown" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "ex.getMessage()", "Index: 0, Size: 0", ex.getMessage() );
            assertThrownBy( ArrayList.class, ex );
        }
    }

    public void testGetIdentityXrefThrowsIndexOutOfBoundsException1() throws Throwable {
        Institution owner = new Institution( "testCvObjectShortLabel" );
        CvDatabase aDatabase = new CvDatabase( new Institution( "testCvObjectShortLabel1" ), "testCvObjectShortLabel" );
        aDatabase.addXref( new CvObjectXref( owner, aDatabase, "testCvObjectAPrimaryId", new CvXrefQualifier( owner, "testCvObjectShortLabel" ) ) );
        try {
            aDatabase.getIdentityXref();
            fail( "Expected IndexOutOfBoundsException to be thrown" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "ex.getMessage()", "Index: 0, Size: 0", ex.getMessage() );
            assertThrownBy( ArrayList.class, ex );
        }
    }

    public void testGetIdentityXrefThrowsIndexOutOfBoundsException2() throws Throwable {
        try {
            new CvDevelopmentalStage( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" ).getIdentityXref();
            fail( "Expected IndexOutOfBoundsException to be thrown" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "ex.getMessage()", "Index: 0, Size: 0", ex.getMessage() );
            assertThrownBy( ArrayList.class, ex );
        }
    }

    public void testHashCodeThrowsNullPointerException() throws Throwable {
        CvObject cvDevelopmentalStage = new CvDevelopmentalStage( new Institution( "testCvObjectShortLabel1" ), "testCvObjectShortLabel" );
        cvDevelopmentalStage.addXref( new CvObjectXref( null, null, "testCvObjectAPrimaryId", "testCvObjectASecondaryId", "testCvObjectADatabaseRelease", new CvXrefQualifier( new Institution( "testCvObjectShortLabel" ), "testCvObjectShortLabel" ) ) );
        try {
            cvDevelopmentalStage.hashCode();
            fail( "Expected NullPointerException to be thrown" );
        } catch ( NullPointerException ex ) {
            assertEquals( "ex.getMessage()", "cvObject should not be null", ex.getMessage() );
            assertThrownBy( CvObjectUtils.class, ex );
            assertEquals( "(CvDevelopmentalStage) cvDevelopmentalStage.xrefs.size()", 1, ( ( CvDevelopmentalStage ) cvDevelopmentalStage ).xrefs.size() );
            assertEquals( "(CvDevelopmentalStage) cvDevelopmentalStage.shortLabel", "testCvObjectShortLab", ( ( CvDevelopmentalStage ) cvDevelopmentalStage ).getShortLabel() );
            assertNull( "(CvDevelopmentalStage) cvDevelopmentalStage.ac", ( ( CvDevelopmentalStage ) cvDevelopmentalStage ).getAc());
            assertNull( "(CvDevelopmentalStage) cvDevelopmentalStage.fullName", ( ( CvDevelopmentalStage ) cvDevelopmentalStage ).getFullName());
        }
    }
}

