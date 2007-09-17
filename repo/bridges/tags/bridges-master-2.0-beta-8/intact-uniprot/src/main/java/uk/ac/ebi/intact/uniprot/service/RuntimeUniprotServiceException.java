/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.service;

/**
 * Uniprot Service specific exception.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class RuntimeUniprotServiceException extends RuntimeException {

    public RuntimeUniprotServiceException() {
    }

    public RuntimeUniprotServiceException( Throwable cause ) {
        super( cause );
    }

    public RuntimeUniprotServiceException( String message ) {
        super( message );
    }

    public RuntimeUniprotServiceException( String message, Throwable cause ) {
        super( message, cause );
    }
}