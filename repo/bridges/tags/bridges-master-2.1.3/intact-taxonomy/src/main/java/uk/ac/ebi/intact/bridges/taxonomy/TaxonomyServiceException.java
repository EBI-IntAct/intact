/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.taxonomy;

/**
 * Exception thrown by the TaxonomyBridge classes.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class TaxonomyServiceException extends Exception {

    public TaxonomyServiceException( String message, Throwable cause ) {
        super( message, cause );
    }

    public TaxonomyServiceException( String message ) {
        super( message );
    }

    public TaxonomyServiceException( Throwable cause ) {
        super( cause );
    }

    public TaxonomyServiceException() {
    }
}