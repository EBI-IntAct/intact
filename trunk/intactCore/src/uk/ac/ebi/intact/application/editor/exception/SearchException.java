/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.exception;

/**
 * Exception thrown for searching the database.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SearchException extends BaseException {

    /**
     * Constructor with a message.
     * @param msg the message explaining the exception.
     */
    public SearchException(String msg) {
        super(msg);
        setMessageKey("error.search");
    }
}
