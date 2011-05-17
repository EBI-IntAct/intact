// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.application.dataConversion.psiDownload.model;

import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.CvFeatureType;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Institution;
import org.junit.Ignore;

/**
 * Allow to creatre an Experiment to which we can set an AC.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
@Ignore
public class TestableFeature extends Feature {

    public TestableFeature() {

    }

    public TestableFeature( String ac, Institution owner, String shortLabel, Component component, CvFeatureType featureType ) {
        super( owner, shortLabel, component, featureType );
        this.ac = ac;
    }
}