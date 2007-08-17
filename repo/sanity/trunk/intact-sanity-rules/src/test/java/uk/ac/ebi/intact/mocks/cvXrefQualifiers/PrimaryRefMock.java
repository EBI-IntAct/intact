/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvXrefQualifiers;

import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.cvDatabases.PsiMiMock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class PrimaryRefMock {

    public static CvXrefQualifier getMock(){
        CvXrefQualifier primaryRef = CvObjectMock.getMock(CvXrefQualifier.class,CvXrefQualifier.PRIMARY_REFERENCE, "identical object");

        primaryRef = (CvXrefQualifier) IntactObjectSetter.setBasicObject(primaryRef);

        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, PsiMiMock.getMock(), IdentityMock.getMock(), CvXrefQualifier.PRIMARY_REFERENCE_MI_REF);
        primaryRef.addXref(xref);
        return primaryRef;
    }
}