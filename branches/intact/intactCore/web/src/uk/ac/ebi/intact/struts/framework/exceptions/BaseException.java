/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.framework.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This is the common superclass for all application exceptions. This
 * class and its subclasses support the chained exception facility.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class BaseException extends Exception {

    /**
     * The root exception.
     */
    private Throwable myRootException;

    // Constructors

    /**
     * Default constructor.
     */
    public BaseException() {}

    /**
     * Constructor with a message.
     * @param msg the message explaing the exception.
     */
    public BaseException(String msg) {
        super(msg);
    }

    /**
     * Constructor that takes a root exception.
     * @param casue the root exception.
     */
    public BaseException(Throwable cause) {
        myRootException = cause;
    }

    /**
     * Print both the normal and root stack traces.
     * @param writer the writer to print the stack trace.
     */
    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
        if (getRootCause() != null) {
            getRootCause().printStackTrace(writer);
        }
        writer.flush();
    }

    /**
     * Print both the normal and root stack traces.
     * @param out the output stream.
     */
    public void printStackTrace(PrintStream out) {
        printStackTrace(new PrintWriter(out));
    }

    /**
     * Print both the normal and root stack traces to the std err.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Return the root exception, if one exists.
     * @return the root exception if it exists or else null.
     */
    public Throwable getRootCause() {
        return myRootException;
    }
}
