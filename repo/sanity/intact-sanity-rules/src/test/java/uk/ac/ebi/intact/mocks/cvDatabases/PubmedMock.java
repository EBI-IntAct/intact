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
public class PubmedMock {

    /**
     * Create a cvDatabase with the folowing value :
     *      ac = AcGenerator.getNextVal()
     *      shortlabel = CvDatabase.PUBMED
     *      fullname = pubmed
     *      one xref identity to psi-mi with primaryId CvDatabase.PUBMED_MI_REF
     * @return a cvDatabase
     */

    public static CvDatabase getMock(){
        CvDatabase pubmed = CvObjectMock.getMock(CvDatabase.class,CvDatabase.PUBMED, "pubmed");
        pubmed = (CvDatabase) IntactObjectSetter.setBasicObject(pubmed);
        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class,PsiMiMock.getMock(), IdentityMock.getMock(),CvDatabase.PUBMED_MI_REF);
        pubmed.addXref(xref);
        return pubmed;
    }


}