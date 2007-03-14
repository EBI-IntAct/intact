/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.messages;

import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.Annotation;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class AnnotationMessage extends GeneralMessage {
    Annotation annotation;

    private AnnotationMessage(String description, int level, String proposedSolution, IntactObject outLow) {
        super(description, level, proposedSolution, outLow);
    }

    public AnnotationMessage(String description, int level, String proposedSolution, IntactObject outLow, Annotation annotation) {
        super(description, level, proposedSolution, outLow);
        this.setAnnotation(annotation);
    }


    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }
}