package uk.ac.ebi.intact.bridges.ncbiblast.model;

import uk.ac.ebi.intact.uniprot.model.UniprotProtein;

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
    private float identity;
    private int startMatch;
    private int endMatch;
    private String description;
    private String database;
    private int startQuery;
    private int endQuery;
    private UniprotProtein proteinInUniprot;

    public BlastProtein(){
        this.accession = null;
        this.sequence = null;
        this.alignment = null;
        this.identity = 0;
        this.startMatch = 0;
        this.endMatch = 0;
        this.description = null;
        this.database = null;
        this.startQuery = 0;
        this.endQuery = 0;
        this.proteinInUniprot = null;
    }
    public String getAccession() {
        return accession;
    }

    public int getStartQuery() {
        return startQuery;
    }

    public int getEndQuery() {
        return endQuery;
    }

    public void setStartQuery(int startQuery) {
        this.startQuery = startQuery;
    }

    public void setEndQuery(int endQuery) {
        this.endQuery = endQuery;
    }

    public UniprotProtein getUniprotProtein() {
        return this.proteinInUniprot;
    }

    public void setUniprotProtein(UniprotProtein prot) {
        this.proteinInUniprot = prot;
    }

    public String getSequence() {
        return sequence;
    }

    public String getDatabase() {
        return database;
    }

    public float getIdentity() {
        return identity;
    }

    public int getStartMatch() {
        return startMatch;
    }

    public int getEndMatch() {
        return endMatch;
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

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setStartMatch(int startMatch) {
        this.startMatch = startMatch;
    }

    public void setEndMatch(int endMatch) {
        this.endMatch = endMatch;
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
