/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.exception;

/**
 * This class manages the Blast exceptions.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class BlastException extends Exception {

        //------ Instance Variables ----------//
    private String commandMessage;

        //------ Constructors ---------------//
    /*default constructor*/
    public BlastException() {
    }

    /**
     * Constructor with a message.
     * @param msg which explains the exception.
     */
    public BlastException(String msg) {
        super(msg);
    }

    /**
     * Constructor with a message.
     * @param msg which explains the exception and displays this one.
     */
    public BlastException(String msg, Exception e) {
        super(msg);
        e.fillInStackTrace();
        commandMessage = e.getMessage();
    }

    /**
     * Keeps inform about the blast execution.
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
