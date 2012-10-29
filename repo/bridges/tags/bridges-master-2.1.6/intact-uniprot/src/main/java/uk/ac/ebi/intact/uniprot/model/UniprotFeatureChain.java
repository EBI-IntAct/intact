/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Feature chain of a UniProt protein.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15-Sep-2006</pre>
 */
public class UniprotFeatureChain extends UniprotProteinTranscriptImpl{

    /////////////////////////
    // instance attribute

    private static final long serialVersionUID = -5756756521582680353L;
    
    /**
     * Description of the chain.
     */
    private String description;

    /**
     * The parent XRef qualifier is chain parent
     */
    private final String parentXRefQualifier = "MI:0951";

    /**
     * A feature chain can have a null sequence when the swissprot curators don't know exactly the ranges of the chain and put '?' in uniprot.
     */
    private final boolean isNullSequenceAllowed = false;

    ///////////////////////
    // Constructor

    public UniprotFeatureChain( String id, Organism organism, String sequence ) {
        super(id, organism, sequence);
    }

    ///////////////////////////
    // Getters and Setters

    /**
     *
     * @return always an empty list because the feature chain doesn't have any secondary ac.
     */
    public List<String> getSecondaryAcs() {
        return Collections.emptyList();
    }

    public void setSecondaryAcs(List<String> secondaryAcs) {
    }

    /**
     *
     * @return always an empty list because the feature chain doesn't have any synonyms.
     */
    public Collection<String> getSynomyms() {
        return Collections.emptyList();
    }

    public void setSynomyms(Collection<String> synomyms) {
    }

    /**
     *
     * @return always null because the feature chain doesn't have any notes.
     */
    public String getNote() {
        return null;
    }

    public void setNote(String note) {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     *
     * @return always true because a feature chain can have an unknown range
     */
    public boolean isNullSequenceAllowed() {
        return isNullSequenceAllowed;
    }

    /**
     *
     * @return  the MI identifier of 'chain-parent'
     */
    public String getParentXRefQualifier() {
        return this.parentXRefQualifier;
    }

    ///////////////////////////
    // Object's override

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "UniprotFeatureChain" );
        sb.append( "{ id='" ).append( getPrimaryAc() ).append( '\'' );
        sb.append( ", sequence='" ).append( getSequence() ).append( '\'' );
        sb.append( ", organism=" ).append( getOrganism() );
        sb.append( ", description=" ).append( description );
        sb.append( ", start=" ).append( getStart() );
        sb.append( ", end=" ).append( getEnd() );
        sb.append( '}' );
        return sb.toString();
    }
}