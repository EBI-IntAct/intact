/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.biosources;

import uk.ac.ebi.intact.mocks.InstitutionMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.cvDatabases.NewtMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.BioSourceXref;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class RatMock {
    public static final String SHORTLABEL = "rat";
    public static final String FULLNAME = "Rattus norvegicus";
    public static final String NEWT_ID = "10116";

    public static BioSource getMock(){
        BioSource bioSource = new BioSource(InstitutionMock.getMock(), SHORTLABEL, NEWT_ID);
        bioSource = (BioSource) IntactObjectSetter.setBasicObject(bioSource);
        BioSourceXref newtXref = XrefMock.getMock(BioSourceXref.class, NewtMock.getMock(), IdentityMock.getMock(), NEWT_ID);
        bioSource.addXref(newtXref);
        return bioSource;
    }
}