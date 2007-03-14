/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvInteractions;

import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.AnnotationMock;
import uk.ac.ebi.intact.mocks.cvTopics.UniprotDrExportMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PsiMiMock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class CoSedimentationMock {

    public static CvInteraction getMock(){

        CvInteraction cosedimentation = CvObjectMock.getMock(CvInteraction.class,CvInteraction.COSEDIMENTATION, "cosedimentation");

        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, PsiMiMock.getMock(), IdentityMock.getMock(),CvInteraction.COSEDIMENTATION_MI_REF);
        cosedimentation.addXref(xref);

        Annotation uniprotDrExportAnnot = AnnotationMock.getMock(UniprotDrExportMock.getMock(), "yes");
        cosedimentation.addAnnotation(uniprotDrExportAnnot);

        return cosedimentation;
    }
}