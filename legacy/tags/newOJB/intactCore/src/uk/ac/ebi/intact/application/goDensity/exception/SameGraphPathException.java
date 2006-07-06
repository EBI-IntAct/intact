/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.exception;

/**
 * 2 GO:IDs are in the same graph path of a graph (DAG)
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */
public class SameGraphPathException extends Exception {

    public SameGraphPathException() {
        super();
    }

    /**
     * @param message
     */
    public SameGraphPathException(String message) {
        super(message);
    }
}
