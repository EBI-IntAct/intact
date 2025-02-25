/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvDatabases;

import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class DDBJMock {
    public static CvDatabase getMock(){
        CvDatabase ddbj = CvObjectMock.getMock(CvDatabase.class,CvDatabase.DDBG, CvDatabase.DDBG);
        ddbj = (CvDatabase) IntactObjectSetter.setBasicObject(ddbj);
        ddbj.setIdentifier(CvDatabase.DDBG_MI_REF);
        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class,PsiMiMock.getMock(), IdentityMock.getMock(),CvDatabase.DDBG_MI_REF);
        ddbj.addXref(xref);
        return ddbj;
    }
}