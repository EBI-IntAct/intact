/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge.adapters.crossRefAdapter;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Oct-2006</pre>
 */
public class UniprotCrossReference {

    private String accessionNumber;
    private String database;
    private String description;

    public UniprotCrossReference( String accessionNumber, String database ) {
        this.accessionNumber = accessionNumber;
        this.database = database;
    }


    public UniprotCrossReference( String accessionNumber, String database, String description ) {
        this.accessionNumber = accessionNumber;
        this.database = database;
        this.description = description;
    }


    /**
     * Getter for property 'accessionNumber'.
     *
     * @return Value for property 'accessionNumber'.
     */
    public String getAccessionNumber() {
        return accessionNumber;
    }

    /**
     * Setter for property 'accessionNumber'.
     *
     * @param accessionNumber Value to set for property 'accessionNumber'.
     */
    public void setAccessionNumber( String accessionNumber ) {
        if( accessionNumber == null ) {
            throw new IllegalArgumentException( "You must give a non null accession number." );
        }
        this.accessionNumber = accessionNumber;
    }

    /**
     * Getter for property 'database'.
     *
     * @return Value for property 'database'.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Setter for property 'database'.
     *
     * @param database Value to set for property 'database'.
     */
    public void setDatabase( String database ) {
        if( database == null ) {
            throw new IllegalArgumentException( "You must give a non null database." );
        }
        this.database = database;
    }

    /**
     * Getter for property 'description'.
     *
     * @return Value for property 'description'.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for property 'description'.
     *
     * @param description Value to set for property 'description'.
     */
    public void setDescription( String description ) {
        this.description = description;
    }


    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        UniprotCrossReference that = (UniprotCrossReference) o;

        if ( !accessionNumber.equals( that.accessionNumber ) ) {
            return false;
        }
        if ( !database.equals( that.database ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = accessionNumber.hashCode();
        result = 31 * result + database.hashCode();
        return result;
    }
}