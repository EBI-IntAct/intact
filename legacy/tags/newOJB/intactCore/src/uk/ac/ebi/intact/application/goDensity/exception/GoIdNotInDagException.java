/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.exception;

/**
 * If GO:ID is not in DAG
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class GoIdNotInDagException extends Exception {

    public GoIdNotInDagException() {
    }

    public GoIdNotInDagException(String message) {
        super(message);
    }
}
