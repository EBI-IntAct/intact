/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvTissues;

import uk.ac.ebi.intact.model.CvTissue;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class BrainMock {
    public static CvTissue getMock(){
        CvTissue brain = CvObjectMock.getMock(CvTissue.class, "brain", "brain");
        brain = (CvTissue) IntactObjectSetter.setBasicObject(brain);
        return brain;
    }
}