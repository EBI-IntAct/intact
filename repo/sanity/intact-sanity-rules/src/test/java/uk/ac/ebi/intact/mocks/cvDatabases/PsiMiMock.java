/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvDatabases;

import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class PsiMiMock {
    private static CvDatabase psiMi;

    static{
        psiMi = CvObjectMock.getMock(CvDatabase.class,CvDatabase.PSI_MI, "psi-mi");
        psiMi = (CvDatabase) IntactObjectSetter.setBasicObject(psiMi);
    }

    public static CvDatabase getMock(){
        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, psiMi, IdentityMock.getMock(), CvDatabase.PSI_MI_MI_REF);
        psiMi.addXref(xref);
        return psiMi;
    }

    public static CvDatabase getPsiMi() {
        return psiMi;
    }
}