/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge.model;

/**
 * Cross reference of a UniProt protein.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15-Sep-2006</pre>
 */
public class UniprotXref {

    /////////////////////////
    // Instance attributes

    /**
     * Accession number of the cross reference.
     */
    private String accession;

    /**
     * Database of the cross reference.
     */
    private String database;

    /**
     * Additional description of the cross reference.
     */
    private String description;

    //////////////////////
    // Constructor

    public UniprotXref( String ac, String db, String description ) {

        if ( ac == null ) {
            throw new IllegalArgumentException( "A cross reference's AC cannot be null." );
        }
        this.accession = ac;

        if ( db == null ) {
            throw new IllegalArgumentException( "A cross reference's database cannot be null." );
        }
        this.database = db;

        this.description = description;
    }

    public UniprotXref( String ac, String db ) {
        setAccession( ac );
        setDatabase( db );
    }

    ///////////////////////////
    // Getters and Setters

    /**
     * Returns accession number of the cross reference.
     *
     * @return accession number of the cross reference.
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Sets accession number of the cross reference.
     *
     * @param accession accession number of the cross reference.
     */
    public void setAccession( String accession ) {
        if ( accession == null ) {
            throw new IllegalArgumentException( "An Xref must have a non null accession." );
        }
        if ( accession.trim().length() == 0 ) {
            throw new IllegalArgumentException( "An Xref must have a non empty accession." );
        }
        this.accession = accession;
    }

    /**
     * Returns database of the cross reference.
     *
     * @return database of the cross reference.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Sets database of the cross reference.
     *
     * @param database database of the cross reference.
     */
    public void setDatabase( String database ) {
        if ( database == null ) {
            throw new IllegalArgumentException( "An Xref must have a non null database." );
        }
        if ( database.trim().length() == 0 ) {
            throw new IllegalArgumentException( "An Xref must have a non empty database." );
        }
        this.database = database;
    }

    /**
     * Returns additional description of the cross reference.
     *
     * @return additional description of the cross reference.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets additional description of the cross reference.
     *
     * @param description additional description of the cross reference.
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    ////////////////////////////
    // Object's override

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final UniprotXref that = (UniprotXref) o;

        if ( !accession.equals( that.accession ) ) {
            return false;
        }
        if ( !database.equals( that.database ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = accession.hashCode();
        result = 29 * result + database.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "UniprotXref" );
        sb.append( "{ac='" ).append( accession ).append( '\'' );
        sb.append( ", db='" ).append( database ).append( '\'' );
        sb.append( ", description='" ).append( description ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}