/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view;

import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
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
     * Default constructor. Used for creating a new annotation.
     *
     * @see #clear()
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

    /**
     * Updates the internal annotation with the new values from the form.
     * @param helper the IntactHelper to search the database
     * @return an Annotation created or updated using values in the bean.
     * @throws IntactException for errors in searching for a CvTopic.
     */
    public Annotation getAnnotation(IntactHelper helper) throws IntactException {
        // The topic for the annotation.
        CvTopic cvtopic = (CvTopic) helper.getObjectByLabel(CvTopic.class,
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
        return getLink(EditorService.getTopic(CvTopic.class), myTopic);
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
    public void clear() {
        myTopic = "";
        myAnnotatedText = "";
    }

    /**
     * Returns true if given bean is equivalent to the current bean.
     * @param cb the bean to compare.
     * @return true if topic and text are equivalent; otherwise false is returned.
     */
    public boolean isEquivalent(CommentBean cb) {
        // Check attributes.
        return cb.getTopic().equals(getTopic())
                && cb.getDescription().equals(getDescription());
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
