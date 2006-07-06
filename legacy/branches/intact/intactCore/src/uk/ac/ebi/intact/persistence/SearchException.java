/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

/**
 *  <p>Exception class to provide more meaningful error messages.
 *  There is an extra constructor to allow other exceptions to
 * pass on information.</p>
 */

public class SearchException extends Exception {


    private String nestedMessage;

    public SearchException() {
    }

    public SearchException(String msg) {

        super(msg);
    }

    public SearchException(String msg, Exception e) {

        super(msg);
        e.fillInStackTrace();
        nestedMessage = e.getMessage();

    }

    public String getNestedMessage() {

        if (nestedMessage != null) {

            return nestedMessage;
        }
        else {

            return "No nested messages have been passed on.";
        }
    }


}
