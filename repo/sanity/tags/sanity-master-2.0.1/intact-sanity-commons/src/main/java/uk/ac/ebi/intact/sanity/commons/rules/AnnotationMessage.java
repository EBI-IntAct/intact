/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.commons.rules;

import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.sanity.commons.report.Field;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class AnnotationMessage extends GeneralMessage {
    private Annotation annotation;

    public AnnotationMessage(MessageDefinition messageDefinition, IntactObject outLaw, Annotation annotation) {
        super(messageDefinition, outLaw);
        this.setAnnotation(annotation);

        getInsaneObject().getFields().addAll(fieldsForAnnotation(annotation));
    }

    public Collection<Field> fieldsForAnnotation(Annotation annotation) {
        Collection<Field> fields = new ArrayList<Field>();

        if (annotation.getCvTopic() != null) {
            Field topicField = new Field();
            topicField.setName("CvTopic AC");
            topicField.setValue(annotation.getCvTopic().getAc());

            fields.add(topicField);
        }

        Field annotTextField = new Field();
        annotTextField.setName("Annotation text");
        annotTextField.setValue(annotation.getAnnotationText());

        fields.add(annotTextField);

        return fields;
    }


    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }
}