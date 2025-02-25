/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchview.exception;

/**
 * Thrown when the user session is expired.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class SessionExpiredException extends Exception {

    public SessionExpiredException() {
    }

    public SessionExpiredException( String message ) {
        super( message );
    }
}
