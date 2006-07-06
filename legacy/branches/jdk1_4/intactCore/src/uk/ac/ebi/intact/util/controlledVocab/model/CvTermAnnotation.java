/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.controlledVocab.model;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Sep-2005</pre>
 */
public class CvTermAnnotation {

    private String topic;
    private String annotation;

    public CvTermAnnotation( String topic, String annotation ) {
        this.topic = topic;
        this.annotation = annotation;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic( String topic ) {
        this.topic = topic;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation( String annotation ) {
        this.annotation = annotation;
    }
}