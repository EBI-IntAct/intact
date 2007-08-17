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
public class NewtMock {
    /**
     * Create a cvDatabase with the folowing value :
     *      ac = AcGenerator.getNextVal()
     *      shortlabel = CvDatabase.NEWT
     *      fullname = newt
     *      one xref identity to psi-mi with primaryId CvDatabase.NEWT_MI_REF
     * @return a cvDatabase
     */
    public static CvDatabase getMock(){
        CvDatabase newt = CvObjectMock.getMock(CvDatabase.class,CvDatabase.NEWT, "newt");
        newt = (CvDatabase) IntactObjectSetter.setBasicObject(newt);
        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class,PsiMiMock.getMock(), IdentityMock.getMock(),CvDatabase.NEWT_MI_REF);
        newt.addXref(xref);
        return newt;
    }
}