/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import uk.ac.ebi.intact.application.editor.struts.view.EditForm;

import java.util.List;


/**
 * The form to display experiments for an Interaction.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentForm extends EditForm {

    /**
     * Sets the experiments for the form to retrieve.
     * @param exps list of ExperimentBean objects.
     *
     * <pre>
     * pre:  exps->forAll(obj : Object | obj.oclIsTypeOf(ExperimentBean))
     * </pre>
     */
    public void setExperiments(List exps) {
        setItems(exps);
    }

    /**
     * Returns a list of experiments for a JSP to display. The var component
     * of c:forAll part must be named as 'experiments'.
     * @return a list of ExperimentBean onjects.
     *
     * <pre>
     * post:  return->forAll(obj : Object | obj.oclIsTypeOf(ExperimentBean))
     * </pre>
     */
    public List getExperiments() {
        return getItems();
    }
}
