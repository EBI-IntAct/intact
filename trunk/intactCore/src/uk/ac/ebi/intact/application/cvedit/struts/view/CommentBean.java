/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl;

import java.io.Serializable;

/**
 * Bean to store data for comments (annotations).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommentBean implements Serializable {

    /**
     * The unique identifier for this bean.
     */
    private long myKey;

    /**
     * Reference to the annotation object.
     */
    private Annotation myAnnotation;

    /**
     * The CV topic.
     */
    private String myTopic;

    /**
     * The annotated text.
     */
    private String myAnnotatedText;

    /**
     * Instantiate an object of this class from an Annotation object.
     *
     * @param annotation the <code>Annotation</code> object to construct an
     * instance of this class.
     */
    public CommentBean(Annotation annotation) {
        myKey = IntactUserImpl.getId();
        myAnnotation = annotation;
        myTopic = annotation.getCvTopic().getShortLabel();
        myAnnotatedText = annotation.getAnnotationText();
    }

    /**
     * Return the key for this object.
     */
    public long getKey() {
        return myKey;
    }

    /**
     * Returns an annotation object this instance is created with.
     */
    public Annotation getAnnotation() {
        return myAnnotation;
    }

    /**
     * Returns the topic.
     */
    public String getTopic() {
        return myTopic;
    }

    /**
     * Returns the annotated text.
     */
    public String getDescription() {
        return myAnnotatedText;
    }

    /**
     * Sets the annotated text.
     *
     * @param text the annotated text.
     */
    public void setDescription(String text) {
        myAnnotatedText = text;
    }

    /**
     * Adds new annotated text to the existing text.
     *
     * @param text the annotated text to add.
     */
    public void addDescription(String text) {
        myAnnotatedText = myAnnotatedText + " " + text;
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Only returns <tt>true</tt> if the internal
     * keys for both objects match.
     *
     * @param obj the object to compare.
     */
    public boolean equals(Object obj) {
        // Identical to this?
        if (obj == this) {
            return true;
        }
        if ((obj != null) && (getClass() == obj.getClass())) {
            // Can safely cast it.
            return myKey == ((CommentBean) obj).getKey();
        }
        return false;
    }
}
