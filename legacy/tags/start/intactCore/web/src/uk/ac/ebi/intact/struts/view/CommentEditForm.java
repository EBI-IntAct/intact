/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.view;

import org.apache.struts.action.ActionForm;

/**
 * The form to fill when editing a comment.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommentEditForm extends ActionForm {

    /**
     * The annotated text.
     */
    private String myAnnotatedText;

    /**
     * The topic.
     */
    private String myTopic;

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
     * Return the topic.
     */
    public String getTopic() {
        return myTopic;
    }

    /**
     * Sets the topic.
     *
     * @param topic sets the id of the topic.
     */
    public void setTopic(String topic) {
        myTopic = topic;
    }
}
