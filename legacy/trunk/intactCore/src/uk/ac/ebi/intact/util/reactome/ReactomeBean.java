/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.reactome;

/**
 * Contains the result of SQL query, populated by dbutils.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>26-Jan-2006</pre>
 */
public class ReactomeBean {

    /////////////////////////////////
    // Instance variables

    private String interactionAC;
    private String reactomeID;

    ////////////////////////////////
    // Constructor

    public ReactomeBean() {
    }

    ////////////////////////////////
    // Getters & Setters

    public String getInteractionAC() {
        return interactionAC;
    }

    public void setInteractionAC( String interactionAC ) {
        this.interactionAC = interactionAC;
    }

    public String getReactomeID() {
        return reactomeID;
    }

    public void setReactomeID( String reactomeID ) {
        this.reactomeID = reactomeID;
    }

    ///////////////////////////
    // Object's Overload

    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append( "ReactomeBean" );
        sb.append( "{interactionAC='" ).append( interactionAC ).append( '\'' );
        sb.append( ", reactomeID='" ).append( reactomeID ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final ReactomeBean that = (ReactomeBean) o;

        if ( !interactionAC.equals( that.interactionAC ) ) {
            return false;
        }
        if ( !reactomeID.equals( that.reactomeID ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = interactionAC.hashCode();
        result = 29 * result + reactomeID.hashCode();
        return result;
    }

    //////////////////////////
    // Utility

    public String toSingleLine() {
        final StringBuffer sb = new StringBuffer();
        sb.append( interactionAC );
        sb.append( '\t' );
        sb.append( reactomeID );
        return sb.toString();
    }
}