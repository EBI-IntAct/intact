/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;


/**
 *  <p>Specific exception class for handling transaction errors
 *  that may be thrown from a DAO.</p>
 */
public class TransactionException extends Exception {

    private String nestedMessage;

    public TransactionException() {
        }

    public TransactionException(String msg) {

            super(msg);
    }

    public TransactionException(String msg, Exception e) {

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
