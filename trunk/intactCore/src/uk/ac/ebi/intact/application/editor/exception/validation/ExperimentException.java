/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.exception.validation;


/**
 * Thrown when the validation for an experiment fails.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentException extends ValidationException {

    public ExperimentException() {
        super("error.exp.validation", "exp.validation");
    }
}
