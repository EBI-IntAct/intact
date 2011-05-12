/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Splice variant of a UniProt protein.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15-Sep-2006</pre>
 */
public class UniprotSpliceVariant extends UniprotProteinTranscriptImpl{


    /////////////////////////
    // instance attributes
    private static final long serialVersionUID = 4864882167147206959L;
    
    /**
     * Secondary accession number of the splice variant.
     */
    private List<String> secondaryAcs;

    /**
     * Collection of synonyms.
     */
    private Collection<String> synomyms;

    /**
     * Additional note of the splice variant.
     */
    private String note;

    /**
     * The parent XRef qualifier is isoform-parent
     */
    private final String parentXRefQualifier = "MI:0243";

    /**
     * A splice variant must have a non null sequence
     */
    private final boolean isNullSequenceAllowed = false;

    ////////////////////////
    // Constructor

    public UniprotSpliceVariant( String primaryAc, Organism organism, String sequence ) {
        super(primaryAc, organism, sequence);
    }

    //////////////////////////
    // Getters and Setters

    /**
     * Returns secondary accession number of the splice variant.
     *
     * @return non null secondary accession number of the splice variant.
     */
    public List<String> getSecondaryAcs() {
        if ( secondaryAcs == null ) {
            secondaryAcs = new ArrayList<String>();
        }
        return secondaryAcs;
    }

    /**
     * Setter for property 'secondaryAcs'.
     *
     * @param secondaryAcs Value to set for property 'secondaryAcs'.
     */
    public void setSecondaryAcs( List<String> secondaryAcs ) {
        this.secondaryAcs = secondaryAcs;
    }

    /**
     * Returns collection of synonyms.
     *
     * @return collection of synonyms.
     */
    public Collection<String> getSynomyms() {
        if ( synomyms == null ) {
            synomyms = new ArrayList<String>();
        }
        return synomyms;
    }

    /**
     * Setter for property 'synomyms'.
     *
     * @param synomyms Value to set for property 'synomyms'.
     */
    public void setSynomyms( Collection<String> synomyms ) {
        this.synomyms = synomyms;
    }

    /**
     *
     * @return always false because a splice variant must have a sequence
     */
    public boolean isNullSequenceAllowed() {
        return isNullSequenceAllowed;
    }

    /**
     *
     * @return  the MI identifier of 'isoform-parent'
     */
    public String getParentXRefQualifier() {
        return this.parentXRefQualifier;
    }

    /**
     * Returns sequence of the splice variant.
     *
     * @return sequence of the splice variant.
     */
    @Override
    public String getSequence() {
        if ( getSequence() == null || getSequence().trim().length() == 0 ) {
            throw new IllegalArgumentException( "A splice variant must have a sequence." );
        }
        return super.getSequence();
    }

    /**
     * Returns additional note of the splice variant.
     *
     * @return additional note of the splice variant.
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets additional note of the splice variant.
     *
     * @param note additional note of the splice variant.
     */
    public void setNote( String note ) {
        this.note = note;
    }

    /**
     *
     * @return always null because a splice variant doesn't have specific description
     */
    public String getDescription() {
        return null;
    }

    public void setDescription(String description) {
    }

    //////////////////////////
    // Object's override

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o ) {

        boolean isEqual = super.equals(o);

        if (isEqual){
            UniprotSpliceVariant that = ( UniprotSpliceVariant ) o;

            if ( secondaryAcs != null ? !secondaryAcs.equals( that.secondaryAcs ) : that.secondaryAcs != null ) {
                return false;
            }
        }

        return isEqual;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result;
        result = super.hashCode();
        result = 31 * result + ( secondaryAcs != null ? secondaryAcs.hashCode() : 0 );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "SpliceVariant" );
        sb.append( "{primaryAC='" ).append( getPrimaryAc() ).append( '\'' );
        sb.append( ",secondaryAcs=" ).append( secondaryAcs );
        sb.append( ",sequence='" ).append( getSequence() ).append( '\'' );
        sb.append( ", start=" ).append( getStart() );
        sb.append( ", end=" ).append( getEnd() );
        sb.append( ", note='" ).append( note ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}