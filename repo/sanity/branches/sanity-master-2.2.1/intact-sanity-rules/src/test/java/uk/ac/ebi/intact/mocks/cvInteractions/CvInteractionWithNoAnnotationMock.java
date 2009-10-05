/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvInteractions;

import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class CvInteractionWithNoAnnotationMock {

    public static CvInteraction getMock(){

        CvInteraction test = CvObjectMock.getMock(CvInteraction.class,"test", "test");
        test = (CvInteraction) IntactObjectSetter.setBasicObject(test);

        return test;
    }


}