/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseForm;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;

/**
 * The form for adding an annotation (comment).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommentAddForm extends IntactBaseForm {

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
     * @param topic sets the id of the topic.
     */
    public void setTopic(String topic) {
        myTopic = topic;
    }

    /**
     * Resets the form to their defualt values. Called from action classes
     * to reset the forms or else it will display the last entered values.
     */
    public void reset() {
        myAnnotatedText = null;
        myTopic = null;
    }

    /**
     * Validate the form contents entered by the user.
     *
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     * @return <tt>ActionErrors</tt> object that contains validation errors. If
     * no errors are found, <tt>null</tt> or an empty <tt>ActionErrors</tt>
     * object is returned.
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = null;

        // Validate annotated text.
        if (myAnnotatedText == null || myAnnotatedText.length() < 1) {
            errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                new ActionError("global.required", "Annotated text"));
        }
        return errors;
    }
}