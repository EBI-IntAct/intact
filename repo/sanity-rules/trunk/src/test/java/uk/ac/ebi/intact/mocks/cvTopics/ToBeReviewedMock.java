/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvTopics;

import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.AnnotationMock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class ToBeReviewedMock {

    private static final String USED_IN_CLASS_DESCR = Experiment.class.getName();


    public static CvTopic getMock(){
        CvTopic toBeReviewed = CvObjectMock.getMock(CvTopic.class,CvTopic.TO_BE_REVIEWED, CvTopic.TO_BE_REVIEWED);

        toBeReviewed = (CvTopic) IntactObjectSetter.setBasicObject(toBeReviewed);

        Annotation usedInClass = AnnotationMock.getMock(UsedInClassMock.getMock(),USED_IN_CLASS_DESCR );
        toBeReviewed.addAnnotation(usedInClass);
        return toBeReviewed;
    }
}