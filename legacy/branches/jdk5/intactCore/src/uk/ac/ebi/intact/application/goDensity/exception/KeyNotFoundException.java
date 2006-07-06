/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.exception;

/**
 * If key is not found
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */
public class KeyNotFoundException extends Exception {

    public KeyNotFoundException() {
    }

    public KeyNotFoundException(String message) {
        super(message);
    }
}