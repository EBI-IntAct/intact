/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

/**
 * When the searched pubmed ID is not found.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Aug-2005</pre>
 */
public class PublicationNotFoundException extends Exception {
    public PublicationNotFoundException( String message ) {
        super( message );
    }
}
