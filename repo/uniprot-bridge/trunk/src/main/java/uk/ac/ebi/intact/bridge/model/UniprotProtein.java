/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Subset of information held in a UniProt protein entry.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15-Sep-2006</pre>
 */
public class UniprotProtein {

    /////////////////////////
    // Instance attributes

    /**
     * Uniprot protein ID.
     */
    private String id;

    /**
     * Primary Accession Number.
     */
    private String primaryAc;

    /**
     * Ordered list of secondary Accession Numbers.
     */
    private List<String> secondaryAcs;

    /**
     * Organism of the protein.
     */
    private Organism organism;

    /**
     * Description of the protein.
     */
    private String description;

    /**
     * Collection of gene name.
     */
    private Collection<String> genes;

    /**
     * Collection of ORF name.
     */
    private Collection<String> orfs;

    /**
     * Collection of synonyms.
     */
    private Collection<String> synomyms;

    /**
     * Collection of locus name.
     */
    private Collection<String> locuses;

    /**
     * Collection of releated diseases.
     */
    private Collection<String> diseases;

    /**
     * Collection of keywords.
     */
    private Collection<String> keywords;

    /**
     * Known function of that protein.
     */
    private Collection<String> functions;

    /**
     * Collection of cross references.
     */
    private Collection<UniprotXref> crossReferences;

    /**
     * Collection of Splice variant.
     */
    private Collection<UniprotSpliceVariant> spliceVariants;

    /**
     * Collection of feature chain
     */
    private Collection<UniprotFeatureChain> featureChains;

    // desease, functions

    /**
     * Hashing og the sequence generated using CRC64 algorithm.
     */
    private String crc64;

    /**
     * Amino Acis sequence of the protein.
     */
    private String sequence;

    /**
     * Length of the sequence.
     */
    private int sequenceLength;

    /**
     * Release version of the protein.
     */
    private String releaseVersion;

    /**
     * Date at which the annotation was last updated.
     */
    private Date lastAnnotationUpdate;

    /**
     * Date at which the sequence was last updated.
     */
    private Date lastSequenceUpdate;

    /////////////////////////
    // Constructor

    public UniprotProtein( String id, String primaryAc, Organism organism, String description ) {
        setId( id );
        setOrganism( organism );
        setPrimaryAc( primaryAc );
        this.description = description;
    }

    //////////////////////////
    // Getters and Setters

    /**
     * Returns uniprot protein ID.
     *
     * @return uniprot protein ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets uniprot protein ID.
     *
     * @param id uniprot protein ID.
     */
    public void setId( String id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "ID cannot be null." );
        }
        if ( id.trim().length() == 0 ) {
            throw new IllegalArgumentException( "ID cannot be empty." );
        }
        this.id = id;
    }

    /**
     * Returns primary Accession Number.
     *
     * @return primary Accession Number.
     */
    public String getPrimaryAc() {
        return primaryAc;
    }

    /**
     * Sets primary Accession Number.
     *
     * @param primaryAc primary Accession Number.
     */
    public void setPrimaryAc( String primaryAc ) {
        if ( primaryAc == null ) {
            throw new IllegalArgumentException( "Primary Ac cannot be null." );
        }
        if ( primaryAc.trim().length() == 0 ) {
            throw new IllegalArgumentException( "Primary Ac cannot be empty." );
        }
        this.primaryAc = primaryAc;
    }

    /**
     * Returns ordered list of secondary Accession Numbers.
     *
     * @return non null ordered list of secondary Accession Numbers.
     */
    public List<String> getSecondaryAcs() {
        if ( secondaryAcs == null ) {
            secondaryAcs = new ArrayList<String>();
        }
        return secondaryAcs;
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
            throw new IllegalArgumentException( "Organism cannot be null." );
        }
        this.organism = organism;
    }

    /**
     * Returns description of the protein.
     *
     * @return description of the protein.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description of the protein.
     *
     * @param description description of the protein.
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * Returns collection of gene name.
     *
     * @return non null collection of gene name.
     */
    public Collection<String> getGenes() {
        if ( genes == null ) {
            genes = new ArrayList<String>();
        }
        return genes;
    }

    /**
     * Returns collection of ORF name.
     *
     * @return non null collection of ORF name.
     */
    public Collection<String> getOrfs() {
        if ( orfs == null ) {
            orfs = new ArrayList<String>();
        }
        return orfs;
    }

    /**
     * Returns collection of synonyms.
     *
     * @return non null collection of synonyms.
     */
    public Collection<String> getSynomyms() {
        if ( synomyms == null ) {
            synomyms = new ArrayList<String>();
        }
        return synomyms;
    }

    /**
     * Returns collection of locus name.
     *
     * @return non null collection of locus name.
     */
    public Collection<String> getLocuses() {
        if ( locuses == null ) {
            locuses = new ArrayList<String>();
        }
        return locuses;
    }

    /**
     * Getter for property 'keywords'.
     *
     * @return non null collection of keywords.
     */
    public Collection<String> getKeywords() {
        if ( keywords == null ) {
            keywords = new ArrayList<String>();
        }
        return keywords;
    }

    /**
     * Getter for property 'diseases'.
     *
     * @return Value for property 'diseases'.
     */
    public Collection<String> getDiseases() {
        if ( diseases == null ) {
            diseases = new ArrayList<String>();
        }
        return diseases;
    }

    /**
     * Getter for property 'functions'.
     *
     * @return Value for property 'functions'.
     */
    public Collection<String> getFunctions() {
        if ( functions == null ) {
            functions = new ArrayList<String>();
        }
        return functions;
    }

    /**
     * Returns collection of cross references.
     *
     * @return non null collection of cross references.
     */
    public Collection<UniprotXref> getCrossReferences() {
        if ( crossReferences == null ) {
            crossReferences = new ArrayList<UniprotXref>();
        }
        return crossReferences;
    }

    /**
     * Returns collection of Splice variant.
     *
     * @return non null collection of Splice variant.
     */
    public Collection<UniprotSpliceVariant> getSpliceVariants() {
        if ( spliceVariants == null ) {
            spliceVariants = new ArrayList<UniprotSpliceVariant>();
        }
        return spliceVariants;
    }

    /**
     * Returns collection of feature chain
     *
     * @return non null collection of feature chain
     */
    public Collection<UniprotFeatureChain> getFeatureChains() {
        if ( featureChains == null ) {
            featureChains = new ArrayList<UniprotFeatureChain>();
        }
        return featureChains;
    }

    /**
     * Returns hashing og the sequence generated using CRC64 algorithm.
     *
     * @return hashing og the sequence generated using CRC64 algorithm.
     */
    public String getCrc64() {
        return crc64;
    }

    /**
     * Sets hashing og the sequence generated using CRC64 algorithm.
     *
     * @param crc64 hashing og the sequence generated using CRC64 algorithm.
     */
    public void setCrc64( String crc64 ) {
        this.crc64 = crc64;
    }

    /**
     * Returns amino Acis sequence of the protein.
     *
     * @return amino Acis sequence of the protein.
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Sets amino Acis sequence of the protein.
     *
     * @param sequence amino Acis sequence of the protein.
     */
    public void setSequence( String sequence ) {
        this.sequence = sequence;
    }

    /**
     * Returns length of the sequence.
     *
     * @return length of the sequence.
     */
    public int getSequenceLength() {
        return sequenceLength;
    }

    /**
     * Sets length of the sequence.
     *
     * @param sequenceLength length of the sequence.
     */
    public void setSequenceLength( int sequenceLength ) {
        this.sequenceLength = sequenceLength;
    }

    /**
     * Returns release version of the protein.
     *
     * @return release version of the protein.
     */
    public String getReleaseVersion() {
        return releaseVersion;
    }

    /**
     * Sets release version of the protein.
     *
     * @param releaseVersion release version of the protein.
     */
    public void setReleaseVersion( String releaseVersion ) {
        this.releaseVersion = releaseVersion;
    }

    /**
     * Getter for property 'lastAnnotationUpdate'.
     *
     * @return Value for property 'lastAnnotationUpdate'.
     */
    public Date getLastAnnotationUpdate() {
        return lastAnnotationUpdate;
    }

    /**
     * Setter for property 'lastAnnotationUpdate'.
     *
     * @param lastAnnotationUpdate Value to set for property 'lastAnnotationUpdate'.
     */
    public void setLastAnnotationUpdate( Date lastAnnotationUpdate ) {
        this.lastAnnotationUpdate = lastAnnotationUpdate;
    }

    /**
     * Getter for property 'lastSequenceUpdate'.
     *
     * @return Value for property 'lastSequenceUpdate'.
     */
    public Date getLastSequenceUpdate() {
        return lastSequenceUpdate;
    }

    /**
     * Setter for property 'lastSequenceUpdate'.
     *
     * @param lastSequenceUpdate Value to set for property 'lastSequenceUpdate'.
     */
    public void setLastSequenceUpdate( Date lastSequenceUpdate ) {
        this.lastSequenceUpdate = lastSequenceUpdate;
    }

    /////////////////////////
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

        final UniprotProtein that = (UniprotProtein) o;

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
        result = 29 * result + organism.hashCode();
        return result;
    }

    public static final String NEW_LINE = "";

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "UniprotProtein" );
        sb.append( "{id='" ).append( id ).append( '\'' ).append( NEW_LINE );
        sb.append( ", primaryAc='" ).append( primaryAc ).append( '\'' ).append( NEW_LINE );
        sb.append( ", secondaryAcs=" ).append( secondaryAcs ).append( NEW_LINE );
        sb.append( ", organism='" ).append( organism ).append( '\'' ).append( NEW_LINE );
        sb.append( ", description='" ).append( description ).append( '\'' ).append( NEW_LINE );
        sb.append( ", genes=" ).append( genes ).append( NEW_LINE );
        sb.append( ", orfs=" ).append( orfs ).append( NEW_LINE );
        sb.append( ", synomyms=" ).append( synomyms ).append( NEW_LINE );
        sb.append( ", locuses=" ).append( locuses ).append( NEW_LINE );
        sb.append( ", keywords=" ).append( keywords ).append( NEW_LINE );
        sb.append( ", diseases=" ).append( diseases ).append( NEW_LINE );
        sb.append( ", crossReferences=" ).append( crossReferences ).append( NEW_LINE );
        sb.append( ", spliceVariants=" ).append( spliceVariants ).append( NEW_LINE );
        sb.append( ", proteinChains=" ).append( featureChains ).append( NEW_LINE );
        sb.append( ", crc64='" ).append( crc64 ).append( '\'' ).append( NEW_LINE );
        sb.append( ", sequence='" ).append( sequence ).append( '\'' ).append( NEW_LINE );
        sb.append( ", sequenceLength=" ).append( sequenceLength ).append( NEW_LINE );
        sb.append( '}' );
        return sb.toString();
    }
}