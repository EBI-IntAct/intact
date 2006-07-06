/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.view;

import uk.ac.ebi.intact.model.Annotation;

/**
 * Bean to store data for comments (annotations).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommentBean {

    /**
     * Reference to the annotation object.
     */
    private Annotation myAnnotation;

    /**
     * The accession number.
     */
    private String myAc;

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
     * @param annot the <code>Annotation</code> object to construct an
     * instance of this class.
     */
    public CommentBean(Annotation annot) {
        myAnnotation = annot;
        myAc = annot.getAc();
        myTopic = annot.getCvTopic().getShortLabel();
        myAnnotatedText = annot.getAnnotationText();
    }

    public Annotation getAnnotation() {
        return myAnnotation;
    }

    /**
     * Returns the accession number.
     */
    public String getAc() {
        return myAc;
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
    public String getText() {
        return myAnnotatedText;
    }

    /**
     * Sets the annotated text.
     *
     * @param text the annotated text.
     */
    public void setText(String text) {
        myAnnotatedText = text;
    }

    /**
     * Adds new annotated text to the existing text.
     *
     * @param text the annotated text to add.
     */
    public void addText(String text) {
        myAnnotatedText = myAnnotatedText + " " + text;
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Only returns <tt>true</tt> if the accesssion
     * numbers match for a similar object type.
     *
     *
     * @param obj the object to compare.
     */
    public boolean equals(Object obj) {
        // Identical to this?
        if (obj == this) {
            return true;
        }
        if ((obj != null) && (obj.getClass() == getClass())) {
            // Can safely cast it.
            CommentBean other = (CommentBean) obj;
            // Accession numbers must match.
            return myAc.equals(other.getAc());
        }
        return false;
    }
}
