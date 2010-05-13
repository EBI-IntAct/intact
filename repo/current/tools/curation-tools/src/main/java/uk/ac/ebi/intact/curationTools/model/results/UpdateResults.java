package uk.ac.ebi.intact.curationTools.model.results;

/**
 * An UpdateResult contains all the results and ActionReports of the update process of a protein
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-May-2010</pre>
 */

public class UpdateResults extends IdentificationResults {

    /**
     * The intact accession of the protein to update
     */
    private String intactAccession;

    /**
     * Create a new UpdateResult instance
     */
    public UpdateResults(){
        super();
        this.intactAccession = null;
    }

    /**
     *
     * @return  the intact accession
     */
    public String getIntactAccession() {
        return intactAccession;
    }

    /**
     * set the intact accession
     * @param intactAccession
     */
    public void setIntactAccession(String intactAccession) {
        this.intactAccession = intactAccession;
    }

}
