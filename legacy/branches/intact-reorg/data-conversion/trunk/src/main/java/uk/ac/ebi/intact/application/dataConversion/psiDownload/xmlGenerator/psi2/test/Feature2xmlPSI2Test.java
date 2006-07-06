// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.psi2.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.PsiVersion;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.UserSessionDownload;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.test.PsiDownloadTest;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Feature2xmlFactory;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Feature2xmlI;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO document this ;o)
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class Feature2xmlPSI2Test extends PsiDownloadTest {

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( Feature2xmlPSI2Test.class );
    }


    private Feature buildSimpleFeature() {

        Experiment experiment = new Experiment( owner, "exp-2005-1", yeast );
        Collection experiments = new ArrayList( 1 );
        experiments.add( experiment );

        Protein protein = new ProteinImpl( owner, yeast, "bbc1_yeast", proteinType );
        Interaction interaction = new InteractionImpl( experiments, aggregation, interactionType, "bbc1-xxx", owner );
        Component component = new Component( owner, interaction, protein, bait );

        CvFeatureType acetylation = new CvFeatureType( owner, "acetylation" );
        acetylation.addXref( new Xref( owner, psi, "MI:0121", null, null, identity ) );

        Feature feature = new Feature( owner, "region", component, acetylation );

        Range range = new Range( owner, 2, 34, null );
        feature.addRange( range );

        feature.addXref( new Xref( owner, interpro, "IPRxxxxxxx", null, null, null ) );

        CvFeatureIdentification docking = new CvFeatureIdentification( owner, "docking" );
        docking.addXref( new Xref( owner, psi, "MI:0035", null, null, identity ) );
        feature.setCvFeatureIdentification( docking );

        return feature;
    }

    ////////////////////////
    // Tests

    private void testBuildFeature_nullArguments( PsiVersion version ) {

        UserSessionDownload session = new UserSessionDownload( version );
        Feature2xmlI f = Feature2xmlFactory.getInstance( session );

        // create a container
        Element parent = session.createElement( "featureList" );

        // call the method we are testing
        Element element = null;

        try {
            f.create( session, parent, null );
            fail( "giving a null Feature should throw an exception" );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( element );

        // create the IntAct object
        Feature feature = buildSimpleFeature();

        try {
            f.create( null, parent, feature );
            fail( "giving a null session should throw an exception" );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( element );

        try {
            f.create( session, null, feature );
            fail( "giving a null parent Element should throw an exception" );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( element );
    }

    public void testBuildFeature_nullArguments_PSI2() {
        testBuildFeature_nullArguments( PsiVersion.getVersion2() );
    }
}