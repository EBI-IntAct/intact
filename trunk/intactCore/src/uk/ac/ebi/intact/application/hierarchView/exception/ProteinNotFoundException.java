/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.exception;

/**
 * Thrown when we don't find a protein by its AC
 *
 * Created by Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class ProteinNotFoundException extends Exception {

    public ProteinNotFoundException() {
    }

    public ProteinNotFoundException(String message) {
        super(message);
    }

}
