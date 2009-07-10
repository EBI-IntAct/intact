/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.service;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Error container in case or processing problem while retreiving proteins.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Feb-2007</pre>
 */
public class UniprotServiceReport {

    /**
     * Message associated with the error.
     */
    private String message;

    /**
     * Exception associated with the error.
     */
    private Exception exception;


    /////////////////////////////
    // Constructor

    public UniprotServiceReport( String message, Exception exception ) {
        if( message == null && exception == null ) {
            throw new IllegalArgumentException( "Either message or exception must not be null." );
        }
        this.message = message;
        this.exception = exception;
    }

    public UniprotServiceReport( String message ) {
        if( message == null  ) {
            throw new IllegalArgumentException( "Message must not be null." );
        }
        this.message = message;
    }

    public UniprotServiceReport( Exception exception ) {
        if( exception == null ) {
            throw new IllegalArgumentException( "Exception must not be null." );
        }
        this.exception = exception;
    }

    //////////////////////////
    // Getters and Setters

    public String getMessage() {
        return message;
    }

    public Throwable getException() {
        return exception;
    }

    ///////////////////////
    // Object

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "UniprotBridgeReport" );
        sb.append( "{message=" ).append( ( message == null ? "" : message ) );
        sb.append( ", exception=" ).append( ( exception == null ? "none" : ExceptionUtils.getFullStackTrace( exception ) ) );
        sb.append( '}' );
        return sb.toString();
    }
}