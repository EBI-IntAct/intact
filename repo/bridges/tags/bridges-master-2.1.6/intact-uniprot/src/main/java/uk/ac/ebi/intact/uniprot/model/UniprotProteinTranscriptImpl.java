package uk.ac.ebi.intact.uniprot.model;

import java.io.Serializable;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02-Aug-2010</pre>
 */

public abstract class UniprotProteinTranscriptImpl implements UniprotProteinTranscript, Serializable{

    private String id;

    /**
     * Sequence of the protein transcript.
     */
    private String sequence;

    /**
     * primaryAc of the protein transcript.
     */
    private String primaryAc;

    /**
     * Organism of a protein transcript.
     */
    private Organism organism;

    /**
     * Start range of the protein transcript.
     */
    private Integer start;

    /**
     * End range of the protein transcript.
     */
    private Integer end;

    private UniprotProtein masterProtein;

    public UniprotProteinTranscriptImpl(String primaryAc, Organism organism, String sequence){
        this.id = primaryAc;
        setPrimaryAc(primaryAc);
        setOrganism(organism);
        setSequence(sequence);
    }

    public String getId() {
        return id;
    }

    public String getPrimaryAc() {
        return this.primaryAc;
    }

    public void setPrimaryAc(String primaryAc) {
        if ( primaryAc == null ) {
            throw new IllegalArgumentException( "A protein transcript must have a primary AC." );
        }
        if ( primaryAc.trim().isEmpty() ) {
            throw new IllegalArgumentException( "A protein transcript must have a non empty primary AC." );
        }
        this.primaryAc = primaryAc;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public Organism getOrganism() {
        return organism;
    }

    public void setOrganism(Organism organism) {
        if ( organism == null ) {
            throw new IllegalArgumentException( "Organism must not be null." );
        }
        this.organism = organism;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        if ( start != null ) {
            if ( end != null && start > end ) {
                throw new IllegalArgumentException( "Start (" + start + ") must be lower than end (" + end + ") !" );
            }
            if ( start != -1 && start < 1 ) {
                throw new IllegalArgumentException( "Start must be 1 or greater." );
            }
        }
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        if ( end != null ) {
            if ( start != null && end > 0 && start > 0 &&  start > end ) {
                throw new IllegalArgumentException( "End (" + end + ") must be greater than start (" + start + ") !" );
            }

            if ( end != -1 && end < 1){
                throw new IllegalArgumentException( "End must be 1 or greater." );
            }
        }
        this.end = end;
    }

    @Override
    public UniprotProtein getMasterProtein() {
        return masterProtein;
    }


    @Override
    public void setMasterProtein(UniprotProtein uniprotProtein) {
        this.masterProtein = uniprotProtein;
    }

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

        UniprotProteinTranscript that = ( UniprotProteinTranscript ) o;

        if ( !primaryAc.equals( that.getPrimaryAc() ) ) {
            return false;
        }

        if ( !organism.equals( that.getOrganism() ) ) {
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
        result = primaryAc.hashCode();
        result = 31 * result + organism.hashCode();
        return result;
    }
}
