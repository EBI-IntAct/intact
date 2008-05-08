/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvFeatureType;

import uk.ac.ebi.intact.model.CvFeatureType;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PsiMiMock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class MutationIncreasingMock {
    public static CvFeatureType getMock(){
        CvFeatureType mutationIncreasing = CvObjectMock.getMock(CvFeatureType.class,CvFeatureType.MUTATION_INCREASING, CvFeatureType.MUTATION_INCREASING);
        mutationIncreasing = (CvFeatureType) IntactObjectSetter.setBasicObject(mutationIncreasing);
        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, PsiMiMock.getMock(), IdentityMock.getMock(),CvFeatureType.MUTATION_INCREASING_MI_REF);
        mutationIncreasing.addXref(xref);
        return mutationIncreasing;
    }
}