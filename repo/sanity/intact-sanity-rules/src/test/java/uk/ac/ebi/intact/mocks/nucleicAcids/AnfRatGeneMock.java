/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.nucleicAcids;

import uk.ac.ebi.intact.model.NucleicAcid;
import uk.ac.ebi.intact.model.NucleicAcidImpl;
import uk.ac.ebi.intact.model.InteractorXref;
import uk.ac.ebi.intact.mocks.InstitutionMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.cvDatabases.DDBJMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.mocks.cvInteractorTypes.DnaTypeMock;
import uk.ac.ebi.intact.mocks.bioSources.RatMock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class AnfRatGeneMock {

    private static final String SHORTLABEL = "anf_rat_gene";
    private static final String FULLNAME = "Rat PND gene encoding atrial natriuretic factor";
    private static final String SEQUENCE = "TCACACCTTTGAAGTGGGGGCCTCTTGAGGCAAAT";
    private static final String DDBJ_ID = "K02062";

    public static NucleicAcid getMock(){
        NucleicAcid nucleicAcid = new NucleicAcidImpl(InstitutionMock.getMock(), RatMock.getMock(),SHORTLABEL, DnaTypeMock.getMock());
        nucleicAcid = (NucleicAcid) IntactObjectSetter.setBasicObject(nucleicAcid);
        nucleicAcid.setFullName(FULLNAME);
        nucleicAcid.setSequence(SEQUENCE);
        InteractorXref xref = XrefMock.getMock(InteractorXref.class, DDBJMock.getMock(), IdentityMock.getMock(),DDBJ_ID);
        nucleicAcid.addXref(xref);
        return nucleicAcid;
    }
}