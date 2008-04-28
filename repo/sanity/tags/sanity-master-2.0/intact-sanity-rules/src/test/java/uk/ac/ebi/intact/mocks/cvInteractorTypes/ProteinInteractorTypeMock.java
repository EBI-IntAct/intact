/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvInteractorTypes;

import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.CvInteractorType;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.CvObjectMock;
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
public class ProteinInteractorTypeMock {
    public static CvInteractorType getMock(){
        CvInteractorType protein = CvObjectMock.getMock(CvInteractorType.class,CvInteractorType.PROTEIN, "protein");
        protein = (CvInteractorType) IntactObjectSetter.setBasicObject(protein);
        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, PsiMiMock.getMock(), IdentityMock.getMock(),CvInteractorType.PROTEIN_MI_REF);
        protein.addXref(xref);
        return protein;
    }
}