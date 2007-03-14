/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvTopics;

import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.mocks.CvObjectMock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class UsedInClassMock {
    public static CvTopic getMock(){
        CvTopic usedInClass = CvObjectMock.getMock(CvTopic.class,CvTopic.USED_IN_CLASS, "used in class");
        return usedInClass;
    }
}