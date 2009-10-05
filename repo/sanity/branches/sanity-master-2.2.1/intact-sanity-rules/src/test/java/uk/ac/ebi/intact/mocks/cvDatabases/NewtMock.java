/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvDatabases;

import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
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
        return CvObjectUtils.createCvObject(new Institution("lala"), CvDatabase.class, CvDatabase.NEWT_MI_REF, CvDatabase.NEWT);
    }
}