/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.graph2MIF;

/**
 * This Exception should be thrown, if an error occours, but the above Element has
 * to determine if it is an fatal one or PSI Format without this Element would also be valid.
 * @author Henning Mersch <hmersch@ebi.ac.uk>
 * @version $Id$
 */
class ElementNotParseableError extends Exception {
    public ElementNotParseableError() {
        super();
    }

    public ElementNotParseableError(String message) {
        super(message);
    }
}
