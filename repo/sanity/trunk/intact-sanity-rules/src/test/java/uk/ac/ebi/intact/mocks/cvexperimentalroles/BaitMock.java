/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvexperimentalroles;

import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PsiMiMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.model.CvExperimentalRole;
import uk.ac.ebi.intact.model.CvObjectXref;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class BaitMock {
    public static CvExperimentalRole getMock(){
        CvExperimentalRole bait = CvObjectMock.getMock(CvExperimentalRole.class,CvExperimentalRole.BAIT, CvExperimentalRole.BAIT);

        bait = (CvExperimentalRole) IntactObjectSetter.setBasicObject(bait);


        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, PsiMiMock.getMock(), IdentityMock.getMock(),CvExperimentalRole.BAIT_PSI_REF);
        bait.addXref(xref);

        return bait;
    }
}