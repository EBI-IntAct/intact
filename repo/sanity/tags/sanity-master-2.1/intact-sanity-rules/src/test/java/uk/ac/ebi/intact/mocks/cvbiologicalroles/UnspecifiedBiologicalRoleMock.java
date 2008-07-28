/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvbiologicalroles;

import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PsiMiMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.model.CvBiologicalRole;
import uk.ac.ebi.intact.model.CvObjectXref;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class UnspecifiedBiologicalRoleMock
{
    public static CvBiologicalRole getMock(){
        CvBiologicalRole unspecified = CvObjectMock.getMock(CvBiologicalRole.class,CvBiologicalRole.UNSPECIFIED, CvBiologicalRole.UNSPECIFIED);
        unspecified = (CvBiologicalRole) IntactObjectSetter.setBasicObject(unspecified);
        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, PsiMiMock.getMock(), IdentityMock.getMock(),CvBiologicalRole.UNSPECIFIED_PSI_REF);
        unspecified.addXref(xref);

        return unspecified;
    }
}