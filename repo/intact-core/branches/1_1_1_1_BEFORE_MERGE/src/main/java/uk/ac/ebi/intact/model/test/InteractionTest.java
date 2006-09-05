// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Test persistence of the Interaction.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionTest extends TestCase {

    /**
     * Handler to the IntactHelper
     */
    private IntactHelper myHelper;

    public InteractionTest( String name ) throws Exception {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( InteractionTest.class );
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws Exception {
        super.setUp();
        myHelper = new IntactHelper();
    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        myHelper.closeStore();
    }

    public void testClone() {
        try {
            doCloneTest0();
            doCloneTest1();
            doCloneTest2();
        } catch ( CloneNotSupportedException cnse ) {
            fail( cnse.getMessage() );
        } catch ( IntactException ie ) {
            fail( ie.getMessage() );
        }
    }

    // Cloning an Interaction created in the memory.
    private void doCloneTest0() throws CloneNotSupportedException, IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The interaction type
        CvInteractionType interType = (CvInteractionType) myHelper.getObjectByLabel(
                CvInteractionType.class, "cleavage");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByLabel(
                CvInteractorType.class, "interaction");

        // Need to have an experiment - at least
        List exps = new ArrayList();
        exps.add((Experiment) myHelper.getObjectByLabel(Experiment.class, "ho"));

        // The interaction to persist
        InteractionImpl orig = new InteractionImpl(exps, interType, interactorType, "int-1", owner);

        // Make a copy.
        Interaction copy = (Interaction) orig.clone();

        // No AC.
        assertNull( copy.getAc() );

        // Short label must have "-x".
        assertTrue( copy.getShortLabel().endsWith( "-x" ) );
        assertEquals( orig.getShortLabel() + "-x", copy.getShortLabel() );

        // Test for shared objects.
        assertSame( orig.getOwner(), copy.getOwner() );
        assertSame( orig.getBioSource(), copy.getBioSource() );
        assertSame( orig.getCvInteractionType(), copy.getCvInteractionType() );

        // Fullname must match.
        assertEquals( orig.getFullName(), copy.getFullName() );

        // Different copies of Annotations.
        assertNotSame( orig.getAnnotations(), copy.getAnnotations() );
        assertEquals( orig.getAnnotations(), copy.getAnnotations() );

        // Different copies of Xrefs.
        assertNotSame( orig.getXrefs(), copy.getXrefs() );
        assertEquals( orig.getXrefs(), copy.getXrefs() );

        // Same KD.
        assertEquals( orig.getKD(), copy.getKD() );

        // Original has some experiments.
        assertFalse( orig.getExperiments().isEmpty() );

        // The copy shouldn't have any experiments.
        assertTrue( copy.getExperiments().isEmpty() );

        // New components.
        assertNotSame( orig.getComponents(), copy.getComponents() );
        // Must be of same size.
        assertEquals( orig.getComponents().size(), copy.getComponents().size() );

        // Extract the proteins and roles to match; can't compare components
        // because equals method is based on reference equality.
        Collection origprots = new ArrayList();
        Collection origroles = new ArrayList();
        for( Iterator iter = orig.getComponents().iterator(); iter.hasNext(); ) {
            Component comp = (Component) iter.next();
            origprots.add( comp.getInteractor() );
            origroles.add( comp.getCvComponentRole() );
        }
        Collection copyprots = new ArrayList();
        Collection copyroles = new ArrayList();
        for( Iterator iter = copy.getComponents().iterator(); iter.hasNext(); ) {
            Component comp = (Component) iter.next();
            copyprots.add( comp.getInteractor() );
            copyroles.add( comp.getCvComponentRole() );
        }
        // Must match the proteins and their roles.
        assertEquals( origprots, copyprots );
        assertEquals( origroles, copyroles );
    }

    // This test uses an Interaction already stored on the database.
    public void doCloneTest1() throws IntactException, CloneNotSupportedException {
        InteractionImpl orig = (InteractionImpl) myHelper.getObjectByLabel( Interaction.class, "ga-3" );

        CvTopic topic = myHelper.getObjectByLabel( CvTopic.class, "comment" );
        orig.addAnnotation( new Annotation( orig.getOwner(), topic ) );

        CvDatabase db = myHelper.getObjectByLabel( CvDatabase.class, "go" );
        InteractorXref xref = new InteractorXref( orig.getOwner(), db, "a", "b", "c", null );
        orig.addXref( xref );

        // Make a copy.
        Interaction copy = (Interaction) orig.clone();
        myHelper.materializeInteraction(copy);

        // No AC.
        assertNull( copy.getAc() );

        // Copy has not time stamps set yet.
        assertNull(copy.getCreated());
        assertNull(copy.getUpdated());

        // Short label must have "-x".
        assertTrue( copy.getShortLabel().endsWith( "-x" ) );
        assertEquals( orig.getShortLabel() + "-x", copy.getShortLabel() );

        // Test for shared objects.
        assertSame( orig.getOwner(), copy.getOwner() );
        assertSame( orig.getBioSource(), copy.getBioSource() );
        assertSame( orig.getCvInteractionType(), copy.getCvInteractionType() );

        // Fullname must match.
        assertEquals( orig.getFullName(), copy.getFullName() );

        // Different copies of Annotations but same contents.
        assertNotSame( orig.getAnnotations(), copy.getAnnotations() );
        assertTrue( CollectionUtils.isEqualCollection( transform( orig.getAnnotations() ), copy.getAnnotations() ) );

        // Different copies of Xrefs but same contents.
        assertNotSame( orig.getXrefs(), copy.getXrefs() );
        assertEquals( transform( orig.getXrefs() ), copy.getXrefs() );
        // Parent AC is not set in the copy.
        Xref copyXref = (Xref) copy.getXrefs().iterator().next();
        assertNull( copyXref.getParentAc() );

        // Same KD.
        assertEquals( orig.getKD(), copy.getKD() );

        // Original has some experiments.
        assertFalse( orig.getExperiments().isEmpty() );

        // The copy shouldn't have any experiments.
        assertTrue( copy.getExperiments().isEmpty() );

        // New components.
        assertNotSame( orig.getComponents(), copy.getComponents() );
        // Must be of same size.
        assertEquals( orig.getComponents().size(), copy.getComponents().size() );

        // Extract the proteins and roles to match; can't compare components
        // because equals method is based on reference equality.
        Collection origprots = new ArrayList();
        Collection origroles = new ArrayList();
        for( Iterator iter = orig.getComponents().iterator(); iter.hasNext(); ) {
            Component comp = (Component) iter.next();
            origprots.add( comp.getInteractor() );
            origroles.add( comp.getCvComponentRole() );
        }
        Collection copyprots = new ArrayList();
        Collection copyroles = new ArrayList();
        for( Iterator iter = copy.getComponents().iterator(); iter.hasNext(); ) {
            Component comp = (Component) iter.next();
            copyprots.add( comp.getInteractor() );
            copyroles.add( comp.getCvComponentRole() );
        }
        // Must match the proteins and their roles.
        assertEquals( origprots, copyprots );
        assertEquals( origroles, copyroles );
    }

    // This test for an Interaction with Features.
    public void doCloneTest2() throws IntactException, CloneNotSupportedException {
        InteractionImpl orig = (InteractionImpl) myHelper.getObjectByLabel( Interaction.class, "ga-1" );

        // Add features.
        Institution owner = orig.getOwner();
        Component component = (Component) orig.getComponents().iterator().next();
        CvFeatureType ft = new CvFeatureType( owner, "ft" );
        Feature feature1 = new Feature( owner, "feature1", component, ft );
        Range range1 = new Range( owner, 1, 2, 3, 4, "abc" );
        feature1.addRange( range1 );
        component.addBindingDomain( feature1 );

        // Another feature
        Feature feature2 = new Feature( owner, "feature2", component, ft );
        Range range2 = new Range( owner, 1, 2, 3, 4, "pqr" );
        feature2.addRange( range2 );
        component.addBindingDomain( feature2 );

        // Link features.
        feature1.setBoundDomain( feature2 );
        feature2.setBoundDomain( feature1 );

        // Verify it.
        assertTrue( feature1.getBoundDomain().equals( feature2 ) );
        assertTrue( feature2.getBoundDomain().equals( feature1 ) );

        // Should have two features.
        assertTrue( component.getBindingDomains().size() == 2 );

        // Make a copy.
        Interaction copy = (Interaction) orig.clone();

        // Should have the same number of components.
        assertEquals( orig.getComponents().size(), copy.getComponents().size() );

        // Extract the features - original
        Collection origfeatures = new ArrayList();
        for( Iterator iter0 = orig.getComponents().iterator(); iter0.hasNext(); ) {
            Component comp = (Component) iter0.next();
            for( Iterator iter1 = comp.getBindingDomains().iterator(); iter1.hasNext(); ) {
                origfeatures.add( iter1.next() );
            }
        }
        // Extract the features - copy
        Collection copyfeatures = new ArrayList();
        for( Iterator iter0 = copy.getComponents().iterator(); iter0.hasNext(); ) {
            Component comp = (Component) iter0.next();
            for( Iterator iter1 = comp.getBindingDomains().iterator(); iter1.hasNext(); ) {
                copyfeatures.add( iter1.next() );
            }
        }

        // Can't compare feature collections directly because a Feature uses
        // reference equality with the component.

        // Two features.
        assertEquals( origfeatures.size(), 2 );
        assertEquals( copyfeatures.size(), 2 );

        // Feature 1 from the original interaction.
        Feature origFeature1 = findFeature( orig.getComponents(), "feature1" );
        assertNotNull( "Feature 1 missing", origFeature1 );
        assertEquals( origFeature1.getShortLabel(), "feature1" );
        assertEquals( origFeature1.getBoundDomain().getShortLabel(), "feature2" );

        // Feature 1 from the cloned interaction.
        Feature copyFeature1 = findFeature( copy.getComponents(), "feature1-x" );
        assertNotNull( "Feature 1 missing", copyFeature1 );
        assertEquals( copyFeature1.getShortLabel(), "feature1-x" );
        // Bound domains are not copied.
        assertNull( copyFeature1.getBoundDomain() );

        // Components are set.
        assertNotNull( origFeature1.getComponent() );
        assertNotNull( copyFeature1.getComponent() );

        // Both features are pointing to different components.
        assertNotSame( origFeature1.getComponent(), copyFeature1.getComponent() );
        assertSame( origFeature1.getComponent(),
                    orig.getComponents().iterator().next() );
        assertSame( copyFeature1.getComponent(),
                    copy.getComponents().iterator().next() );

        // Ranges (original) are equal
        assertEquals( origFeature1.getRanges().size(), 1 );
        Range origRange1 = (Range) origFeature1.getRanges().iterator().next();
        assertEquals( origRange1.getFromIntervalStart(), 1 );
        assertEquals( origRange1.getFromIntervalEnd(), 2 );

        // Ranges (cloned) are equal
        assertEquals( copyFeature1.getRanges().size(), 1 );
        Range copyRange1 = (Range) copyFeature1.getRanges().iterator().next();
        assertEquals( copyRange1.getFromIntervalStart(), 1 );
        assertEquals( copyRange1.getFromIntervalEnd(), 2 );

        // Ranges are deep copied; change the copy.
        copyRange1.setFromIntervalStart( -1 );
        assertTrue( copyRange1.getFromIntervalStart() == -1 );
        assertTrue( origRange1.getFromIntervalStart() == 1 );
    }

    private Feature findFeature( Collection comps, String label ) {
        for( Iterator iter0 = comps.iterator(); iter0.hasNext(); ) {
            Component comp = (Component) iter0.next();
            for( Iterator iter1 = comp.getBindingDomains().iterator(); iter1.hasNext(); ) {
                Feature feature = (Feature) iter1.next();
                if( feature.getShortLabel().equals( label ) ) {
                    return feature;
                }
            }
        }
        return null;
    }

    // Converts ListProxy to proper object for to compare.
    private List transform( Collection items ) {
        List list = new ArrayList( items.size() );
        for( Iterator iter = items.iterator(); iter.hasNext(); ) {
            list.add( iter.next() );
        }
        return list;
    }
}
