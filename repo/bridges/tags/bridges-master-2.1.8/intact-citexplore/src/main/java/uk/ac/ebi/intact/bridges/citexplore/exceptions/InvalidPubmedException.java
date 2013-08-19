package uk.ac.ebi.intact.bridges.citexplore.exceptions;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/12/12</pre>
 */

public class InvalidPubmedException extends RuntimeException{

    private String pubmedId;

    public InvalidPubmedException(String pubmedId) {
        super("Invalid Pubmed ID: "+ pubmedId);
        this.pubmedId = pubmedId;
    }

    public InvalidPubmedException(String pubmedId, Throwable cause) {
        super("Invalid Pubmed ID: "+ pubmedId, cause);
        this.pubmedId = pubmedId;
    }

    public String getPubmedId() {
        return pubmedId;
    }
}
