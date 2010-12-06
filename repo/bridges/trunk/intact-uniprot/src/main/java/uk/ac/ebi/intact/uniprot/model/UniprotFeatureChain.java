/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.uniprot.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    public static final Log log = LogFactory.getLog( UniprotFeatureChain.class );

    /////////////////////////
    // instance attribute

    private static final long serialVersionUID = -7257651581115886347L;
    
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
        log.warn("A feature chain doesn't have any secondary acs, it is not possible to add a secondary ac to a feature chain. ");
    }

    /**
     *
     * @return always an empty list because the feature chain doesn't have any synonyms.
     */
    public Collection<String> getSynomyms() {
        return Collections.emptyList();
    }

    public void setSynomyms(Collection<String> synomyms) {
        log.warn("A feature chain doesn't have any synonyms, it is not possible to add a synonym to a feature chain. ");
    }

    /**
     *
     * @return always null because the feature chain doesn't have any notes.
     */
    public String getNote() {
        return null;
    }

    public void setNote(String note) {
        log.warn("A feature chain doesn't have any notes, it is not possible to add a note to a feature chain. ");
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
        sb.append( "{ id='" ).append( primaryAc ).append( '\'' );
        sb.append( ", sequence='" ).append( sequence ).append( '\'' );
        sb.append( ", organism=" ).append( organism );
        sb.append( ", description=" ).append( description );
        sb.append( ", start=" ).append( start );
        sb.append( ", end=" ).append( end );
        sb.append( '}' );
        return sb.toString();
    }
}