/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge.model;

/**
 * Feature chain of a UniProt protein.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15-Sep-2006</pre>
 */
public class UniprotFeatureChain {

    /////////////////////////
    // instance attribute

    /**
     * Identifier of the feature chain.
     */
    private String id;

    /**
     * Sequence of the feature chain.
     */
    private String sequence;

    /**
     * Organism of the feature chain.
     */
    private Organism organism;

    /**
     * Start amino acid of the subsequence.
     */
    private Integer start;

    private Integer end;

    ///////////////////////
    // Constructor

    public UniprotFeatureChain( String id, Organism organism, String sequence ) {
        setId( id );
        setSequence( sequence );
        setOrganism( organism );
    }

    ///////////////////////////
    // Getters and Setters

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for property 'id'.
     *
     * @param id Value to set for property 'id'.
     */
    public void setId( String id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "ID must not be null." );
        }
        if( id.trim().equals( "" ) ) {
            throw new IllegalArgumentException( "ID must not be empty." );
        }
        this.id = id;
    }

    /**
     * Getter for property 'sequence'.
     *
     * @return Value for property 'sequence'.
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Setter for property 'sequence'.
     *
     * @param sequence Value to set for property 'sequence'.
     */
    public void setSequence( String sequence ) {
        if ( sequence == null ) {
            throw new IllegalArgumentException( "Sequence must not be null." );
        }
        this.sequence = sequence;
    }

    /**
     * Getter for property 'organism'.
     *
     * @return Value for property 'organism'.
     */
    public Organism getOrganism() {
        return organism;
    }

    /**
     * Setter for property 'organism'.
     *
     * @param organism Value to set for property 'organism'.
     */
    public void setOrganism( Organism organism ) {
        if ( organism == null ) {
            throw new IllegalArgumentException( "Organism must not be null." );
        }
        this.organism = organism;
    }

    /**
     * Getter for property 'start'.
     *
     * @return Value for property 'start'.
     */
    public int getStart() {
        return start;
    }

    /**
     * Setter for property 'start'.
     *
     * @param start Value to set for property 'start'.
     */
    public void setStart( int start ) {
        if ( end != null && start > end ) {
            throw new IllegalArgumentException( "Start (" + start + ") must be lower than end (" + end + ") !" );
        }
        if( start < 1 ) {
            throw new IllegalArgumentException( "Start must be 1 or greater." );
        }
        this.start = start;
    }

    /**
     * Getter for property 'end'.
     *
     * @return Value for property 'end'.
     */
    public int getEnd() {
        return end;
    }

    /**
     * Setter for property 'end'.
     *
     * @param end Value to set for property 'end'.
     */
    public void setEnd( int end ) {
        if ( start != null && start > end ) {
            throw new IllegalArgumentException( "End (" + end + ") must be greater than start (" + start + ") !" );
        }
        if( end < 1 ) {
            throw new IllegalArgumentException( "End must be 1 or greater." );
        }
        this.end = end;
    }

    ///////////////////////////
    // Object's override

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        UniprotFeatureChain that = (UniprotFeatureChain) o;

        if ( !id.equals( that.id ) ) {
            return false;
        }
        if ( !organism.equals( that.organism ) ) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result;
        result = id.hashCode();
        result = 31 * result + organism.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "UniprotFeatureChain" );
        sb.append( "{end=" ).append( end );
        sb.append( ", id='" ).append( id ).append( '\'' );
        sb.append( ", sequence='" ).append( sequence ).append( '\'' );
        sb.append( ", organism=" ).append( organism );
        sb.append( ", start=" ).append( start );
        sb.append( '}' );
        return sb.toString();
    }
}