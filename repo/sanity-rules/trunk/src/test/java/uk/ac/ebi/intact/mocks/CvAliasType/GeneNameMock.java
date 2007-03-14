/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.CvAliasType;

import uk.ac.ebi.intact.model.CvAliasType;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.mocks.CvObjectMock;
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
public class GeneNameMock {
    public static CvAliasType getMock(){
        CvAliasType geneName = CvObjectMock.getMock(CvAliasType.class,CvAliasType.GENE_NAME, "newt");

        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, PsiMiMock.getMock(), IdentityMock.getMock(),CvAliasType.GENE_NAME_MI_REF);
        geneName.addXref(xref);

        return geneName;
    }
}