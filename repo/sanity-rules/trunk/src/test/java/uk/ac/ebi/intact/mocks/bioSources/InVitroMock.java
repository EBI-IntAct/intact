/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.bioSources;

import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.mocks.InstitutionMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class InVitroMock {
    public static final String SHORTLABEL = "in-vitro";
    public static final String FULLNAME = "in-vitro";
    public static final String NEWT_ID = "-1";

    public static BioSource getMock(){
        BioSource bioSource = new BioSource(InstitutionMock.getMock(), SHORTLABEL, NEWT_ID);
        bioSource = (BioSource) IntactObjectSetter.setBasicObject(bioSource);
        return bioSource;
    }
}