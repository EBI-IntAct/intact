/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.view;

import org.apache.taglibs.display.TableDecorator;

/**
 * This class is a decorator of the CommentBeans to reformat data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommentBeanWrapper extends TableDecorator {

    /**
     * Returns the HTML snippet for read only topic contents.
     */
    public String getTopic() {
        String topic = ((CommentBean) super.getObject()).getTopic();
        return "<input type=\"text\" name=\"topic\" size=\"15\" value=\""
            + topic + "\" readonly=\"yes\"></input>";
    }

    /**
     * Returns the HTML snippet for read only description contents.
     */
    public String getDescription() {
        String text = ((CommentBean) super.getObject()).getText();
        return "<textarea name=\"text\" rows=\"3\" cols=\"80\" readonly=\"yes\">"
            + text + "</textarea>";
    }

    /**
     * Returns the hyperlink to delete a comment.
     */
    public String getDeleteLink() {
        String ac = ((CommentBean) super.getObject()).getAc();
        return "<a href=\"delComment.do?ac=" + ac + "\">Delete</a>";
    }
}
