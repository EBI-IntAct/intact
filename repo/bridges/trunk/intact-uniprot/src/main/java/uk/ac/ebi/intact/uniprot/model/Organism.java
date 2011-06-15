/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of an organism.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-Oct-2006</pre>
 */
public class Organism implements Serializable{

    ///////////////////////
    // Instance attributes

    private static final long serialVersionUID = 5114610382670182399L;
    /**
     * The taxid of an organism.
     */
    private int taxid;

    /**
     * The scientific name of an organism.
     */
    private String name;

    /**
     * The common name of an organism
     */
    private String commonName;

    /**
     * The parents' name of an organism.
     */
    private List<String> parents;    

    ////////////////////////
    // Constructors

    public Organism( int taxid ) {
        setTaxid( taxid );
    }

    public Organism( int taxid, String name ) {
        this( taxid );
        this.name = name;
    }

    ////////////////////////
    // Getters & Setters

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<String> getParents() {
        if( parents == null ) {
            parents = new ArrayList<String>( );
        }
        return parents;
    }

    public int getTaxid() {
        return taxid;
    }

    public void setTaxid( int taxid ) {
        if( taxid < 1 ) {
            throw new IllegalArgumentException( "taxid must be 1 or greater." );
        }
        this.taxid = taxid;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    //////////////////////////////
    // Object's override

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        Organism organism = (Organism) o;

        if ( taxid != organism.taxid ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return taxid;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "Organism" );
        sb.append( "{name='" ).append( name ).append( '\'' );
        sb.append( ", taxid=" ).append( taxid );
        sb.append( ", parents=" ).append( parents );
        sb.append( '}' );
        return sb.toString();
    }
}