/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.intact.uniprot.service.UniprotService;

/**
 * Splice variant of a UniProt protein.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15-Sep-2006</pre>
 */
public class UniprotSpliceVariant extends UniprotProteinTranscriptImpl{
    public static final Log log = LogFactory.getLog( UniprotSpliceVariant.class );


    /////////////////////////
    // instance attributes
    private static final long serialVersionUID = 9150707995631479231L;
    
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
        if ( sequence == null || sequence.trim().length() == 0 ) {
            log.error("The sequence was null, the primary Ac of the splice variant is " + getPrimaryAc());
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
        log.warn("A splice variant doesn't have a specific description. It is not possible to set the description of a splice variant.");
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
        sb.append( "{primaryAC='" ).append( primaryAc ).append( '\'' );
        sb.append( ",secondaryAcs=" ).append( secondaryAcs );
        sb.append( ",sequence='" ).append( sequence ).append( '\'' );
        sb.append( ", start=" ).append( start );
        sb.append( ", end=" ).append( end );
        sb.append( ", note='" ).append( note ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}