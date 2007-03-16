/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvIdentifications;

import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.CvIdentification;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PsiMiMock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class PredeterminedMock {
     public static CvIdentification getMock(){
        CvIdentification predetermined = CvObjectMock.getMock(CvIdentification.class,CvIdentification.PREDETERMINED,"predetermined participant");
        predetermined = (CvIdentification) IntactObjectSetter.setBasicObject(predetermined);
        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, PsiMiMock.getMock(), IdentityMock.getMock(),CvIdentification.PREDETERMINED_MI_REF);
        predetermined.addXref(xref);
        return predetermined;
    }
}