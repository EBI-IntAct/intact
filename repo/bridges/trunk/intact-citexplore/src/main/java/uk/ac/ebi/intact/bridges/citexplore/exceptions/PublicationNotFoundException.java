package uk.ac.ebi.intact.bridges.citexplore.exceptions;

/**
 * When the searched pubmed ID is not found.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/12/12</pre>
 */

public class PublicationNotFoundException extends Exception {
    public PublicationNotFoundException( String message ) {
        super( message );
    }
}
