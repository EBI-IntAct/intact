package uk.ac.ebi.intact.protein.mapping.results;

import uk.ac.ebi.intact.uniprot.model.UniprotProtein;

/**
 * Interface for blast results
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface BlastResults {

    public String getTremblAccession();

    public void setTremblAccession(String ac);

    /**
     *
     * @return The taxId
     */
    public int getTaxId();

    /**
     *
     * @return the accession of the protein
     */
    public String getAccession();

    /**
     *
     * @return the start position of the alignment in the query
     */
    public int getStartQuery();

    /**
     *
     * @return the end position of the alignment in the query
     */
    public int getEndQuery();

    /**
     *
     * @return The uniprot protein
     */
    public UniprotProtein getUniprotProtein();

    /**
     *
     * @return The sequence
     */
    public String getSequence();

    /**
     *
     * @return  the database name
     */
    public String getDatabase();

    /**
     *
     * @return the identity
     */
    public float getIdentity();

    /**
     *
     * @return the start match
     */
    public int getStartMatch();

    /**
     *
     * @return  the end match
     */
    public int getEndMatch();

    /**
     *
     * @return  the description
     */
    public String getDescription();

    /**
     *
     * @return the alignment
     */
    public String getAlignment();
}
