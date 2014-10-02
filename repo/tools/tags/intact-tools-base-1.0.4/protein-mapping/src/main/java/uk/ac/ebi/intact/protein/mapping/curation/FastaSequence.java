package uk.ac.ebi.intact.protein.mapping.curation;

/**
 * Represents a fast sequence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/08/12</pre>
 */

public class FastaSequence {

    private String sequence;
    private String identifier;

    public FastaSequence(String identifier, String sequence){

        if (sequence == null){
            throw new IllegalArgumentException("The sequence must be non null");
        }

        if (identifier == null){
            throw new IllegalArgumentException("The sequence identifier must be non null");
        }

        this.sequence = sequence;
        this.identifier = identifier;
    }

    public String getSequence() {
        return sequence;
    }

    public String getIdentifier() {
        return identifier;
    }
}
