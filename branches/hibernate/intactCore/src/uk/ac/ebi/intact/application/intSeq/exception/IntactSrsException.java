/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.exception;

/**
 * This class manages the SRS exceptions.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class IntactSrsException extends Exception {

        //------ Instance Variables ----------//
    private String commandMessage;

        //------ Constructors ---------------//
    /*default constructor*/
    public IntactSrsException() {
    }

    /**
     * Constructor with a message.
     * @param msg which explains the exception.
     */
    public IntactSrsException(String msg) {
        super(msg);
    }

    /**
     * Constructor with a message.
     * @param msg which explains the exception and displays this one.
     */
    public IntactSrsException(String msg, Exception e) {
        super(msg);
        e.fillInStackTrace();
        commandMessage = e.getMessage();
    }

    /**
     * Keeps inform about the srs execution.
     *
     * @return String representing the message to display.
     */
    public String getCommandMessage() {
        if (commandMessage != null) {
            return commandMessage;
        }
        else {
            return "The command line seems to be passed on.";
        }
    }


}
