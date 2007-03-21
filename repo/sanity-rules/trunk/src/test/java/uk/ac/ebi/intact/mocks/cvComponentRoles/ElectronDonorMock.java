/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvComponentRoles;

import uk.ac.ebi.intact.model.CvComponentRole;
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
public class ElectronDonorMock {
    public static CvComponentRole getMock(){
        CvComponentRole electronDonor = CvObjectMock.getMock(CvComponentRole.class,CvComponentRole.ELECTRON_DONOR, CvComponentRole.ELECTRON_DONOR);

        electronDonor = (CvComponentRole) IntactObjectSetter.setBasicObject(electronDonor);

        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, PsiMiMock.getMock(), IdentityMock.getMock(),CvComponentRole.ELECTRON_DONOR_MI_REF);
        electronDonor.addXref(xref);

        return electronDonor;
    }
}