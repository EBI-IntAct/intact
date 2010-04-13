package uk.ac.ebi.intact.bridges.ncbiblast.model;

/**
 * The object containing the wswublast information for one hit of the wswublast results
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-Mar-2010</pre>
 */

public class BlastProtein {

    private String accession;
    private String sequence;
    private String alignment;
    private int score;
    private float identity;
    private int start;
    private int end;
    private String description;

    public String getAccession() {
        return accession;
    }

    public String getSequence() {
        return sequence;
    }

    public int getScore() {
        return score;
    }

    public float getIdentity() {
        return identity;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public void setIdentity(float identity) {
        this.identity = identity;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }
}
