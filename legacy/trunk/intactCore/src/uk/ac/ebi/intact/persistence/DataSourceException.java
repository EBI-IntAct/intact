/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;


/**
 *  Custom exception class to handle errors occurring with
 *  eg opening/closing a connection to a data source.
 *
 * @author Chris Lewington
 */
public class DataSourceException extends Exception {

    private String nestedMessage;

    public DataSourceException() {
        }

    public DataSourceException(String msg) {

            super(msg);
    }

    public DataSourceException(String msg, Exception e) {

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
