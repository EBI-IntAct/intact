/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view;

import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.application.editor.business.EditUser;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;

import java.io.Serializable;

/**
 * Bean to store data for comments (annotations).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommentBean extends EditBean implements Serializable {

    // Instance Data

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
     * @param annotation the <code>Annotation</code> object to construct an
     * instance of this class.
     */
    public CommentBean(Annotation annotation) {
        myKey = EditUser.getId();
        myAnnotation = annotation;
        myTopic = annotation.getCvTopic().getShortLabel();
        myAnnotatedText = annotation.getAnnotationText();
    }

    /**
     * Return the key for this object.
     * @return key for this object as a <code>long</code>.
     */
    public long getKey() {
        return myKey;
    }

    /**
     * Returns an annotation object this instance is created with.
     * @return the instance of <code>Annotation</code> this instance is created
     * with.
     */
    public Annotation getAnnotation() {
        return myAnnotation;
    }

    /**
     * Returns the topic.
     * @return topic as a <code>String</code>.
     */
    public String getTopic() {
        return myTopic;
    }

    /**
     * Sets the topic.
     * @param topic the new topic as a <code>String</code>.
     */
    public void setTopic(String topic) {
        myTopic = topic.trim();
    }

    /**
     * Returns the annotated text.
     * @return description as a <code>String</code>.
     */
    public String getDescription() {
        return myAnnotatedText;
    }

    /**
     * Sets the annotated text.
     * @param text the annotated text as a <code>String</code>.
     */
    public void setDescription(String text) {
        myAnnotatedText = text;
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
