/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import uk.ac.ebi.intact.application.editor.struts.view.EditForm;

import java.util.List;


/**
 * The form to capture annotation editing.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentForm extends EditForm {

    public void setExperiments(List exps) {
        setItems(exps);
    }

    public List getExperiments() {
        System.out.println("Getting experiments: " + getItems().size());
        return getItems();
    }
}
