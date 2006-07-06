/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.exception;

/**
 * If database for goDensity is still empty within same tables
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class DatabaseEmptyException extends Exception {

    public DatabaseEmptyException() {
    }

    public DatabaseEmptyException(String message) {
        super(message);
    }
}
