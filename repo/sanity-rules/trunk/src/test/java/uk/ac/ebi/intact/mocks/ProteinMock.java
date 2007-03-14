/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.mocks.cvInteractorTypes.ProteinInteractorTypeMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PsiMiMock;
import uk.ac.ebi.intact.mocks.cvDatabases.UniprotMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.mocks.CvAliasType.GeneNameMock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class ProteinMock {
    private static String SHORTLABEL = "aatm_rabit";
    private static String FULLNAME = "Aspartate aminotransferase, mitochondrial";
    private static String ALIAS = "GOT2";
    private static String IDENTITY_XREF_ID = "P12345";
    private static String SEQUENCE = "SSWWAHVEMGPPDPILGVTEAYKRDTNSKK";

    public static Protein getMock(){
        Protein protein = new ProteinImpl(InstitutionMock.getMock(), BioSourceMock.getMock(),SHORTLABEL, ProteinInteractorTypeMock.getMock());
        protein.setSequence(SEQUENCE);
        protein.setFullName(FULLNAME);

        InteractorXref xref = XrefMock.getMock(InteractorXref.class, UniprotMock.getMock(), IdentityMock.getMock(), IDENTITY_XREF_ID);
        protein.addXref(xref);

        InteractorAlias alias =  AliasMock.getMock(InteractorAlias.class, GeneNameMock.getMock(),protein);
        alias.setName(ALIAS);
        protein.addAlias(alias);

        return protein;
    }
}