/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

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
     * Stores the state of the transaction when this bean was added. This
     * state in only set during the construction of an instance of this class.
     */
    private boolean myInTransaction;

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
     * Instantiate an object of this class from an Annotation object. The
     * bean is not involved in a transaction.
     *
     * @param annot the <code>Annotation</code> object to construct an
     * instance of this class.
     */
    public CommentBean(Annotation annot) {
        this(annot, false);
    }

    /**
     * Instantiate an object of this class from an Annotation object with
     * the transaction state when the bean was added.
     *
     * @param annot the <code>Annotation</code> object to construct an
     * instance of this class.
     * @param state the state of the transaction when bean was added.
     */
    public CommentBean(Annotation annot, boolean state) {
        myAnnotation = annot;
        myInTransaction = state;
        myAc = annot.getAc();
        myTopic = annot.getCvTopic().getShortLabel();
        myAnnotatedText = annot.getAnnotationText();
    }

    /**
     * Returns an annotation object this instance is created with.
     */
    public Annotation getAnnotation() {
        return myAnnotation;
    }

    /**
     * Returns true if this object is in a transaction.
     */
    public boolean inTransaction() {
        return myInTransaction;
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
