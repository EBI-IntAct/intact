/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.struts.view;

import java.util.List;


/**
 * The form to capture annotation editing.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommentEditForm extends EditForm {

    public void setAnnotations(List annots) {
        setItems(annots);
    }

    public List getAnnotations() {
        return getItems();
    }
}
