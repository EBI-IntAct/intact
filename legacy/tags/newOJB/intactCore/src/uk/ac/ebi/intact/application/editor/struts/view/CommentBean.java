/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view;

import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;

/**
 * Bean to store data for comments (annotations).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommentBean extends AbstractEditKeyBean {

    // Instance Data

    /**
     * Reference to the annotation object. Transient as it can be created using
     * values in the bean.
     */
    private transient Annotation myAnnotation;

    /**
     * The CV topic.
     */
    private String myTopic;

    /**
     * The annotated text.
     */
    private String myAnnotatedText;

    /**
     * Default constructor.
     *
     * @see #reset()
     */
    public CommentBean() {
    }

    /**
     * Instantiate an object of this class from an Annotation object. The key
     * is set to a default value (unique).
     * @param annotation the underlying <code>Annotation</code> object.
     */
    public CommentBean(Annotation annotation) {
        initialize(annotation);
    }

    /**
     * Instantiates with given annotation and key.
     * @param annotation the underlying <code>Annotation</code> object.
     * @param key the key to assigned to this bean.
     */
    public CommentBean(Annotation annotation, long key) {
        super(key);
        initialize(annotation);
    }

    // Override Object's equals method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract.
     * @param obj the object to compare.
     * @return true only if <code>obj</code> is an instance of this class
     * and all non transient fields are equal to given object's non tranient
     * fields. For all other instances, false is returned.
     */
//    public boolean equals(Object obj) {
//        // Identical to this?
//        if (this == obj) {
//            return true;
//        }
//        if (!(obj instanceof CommentBean)) {
//            return false;
//        }
//        // Can safely cast it.
//        CommentBean other = (CommentBean) obj;
//
//        // Compare topic and annotation text.
//        if (!equals(myTopic, other.myTopic)) {
//            return false;
//        }
//        return equals(myAnnotatedText, other.myAnnotatedText);
//    }

    /**
     * Updates the internal annotation with the new values from the form. If there
     * is no annotation, a new annotation is created.
     * @param user the user instance to search for a CvTopic object.
     * @return an Annotation created or updated using values in the bean.
     * @throws SearchException for errors in searching for a CvTopic.
     */
    public Annotation getAnnotation(EditUserI user) throws SearchException {
        // The topic for the annotation.
        CvTopic cvtopic = (CvTopic) user.getObjectByLabel(CvTopic.class,
                getTopic());
        // Update the existing annotation object.
        myAnnotation.setCvTopic(cvtopic);
        myAnnotation.setAnnotationText(getDescription());
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
     * Returns the topic with a link to show its contents in a window.
     * @return the topic as a browsable link.
     */
    public String getTopicLink() {
        return getLink("CvTopic", myTopic);
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

    /**
     * Resets fields to blanks, so the addAnnotation form doesn't display
     * previous values.
     */
    public void reset() {
        super.reset();
        myTopic = "";
        myAnnotatedText = "";
    }

    /**
     * Intialize the member variables using the given Annotation object.
     * @param annotation <code>Annotation</code> object to populate this bean.
     */
    private void initialize(Annotation annotation) {
        myAnnotation = annotation;
        myTopic = annotation.getCvTopic().getShortLabel();
        myAnnotatedText = annotation.getAnnotationText();
    }
}
