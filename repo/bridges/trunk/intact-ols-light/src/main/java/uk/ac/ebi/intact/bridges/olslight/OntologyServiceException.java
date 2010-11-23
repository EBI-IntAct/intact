package uk.ac.ebi.intact.bridges.olslight;

/**
 * OLS Service exception.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.4
 */
public class OntologyServiceException extends Exception {
    public OntologyServiceException() {
        super();
    }

    public OntologyServiceException( String message ) {
        super( message );
    }

    public OntologyServiceException( String message, Throwable cause ) {
        super( message, cause );
    }

    public OntologyServiceException( Throwable cause ) {
        super( cause );
    }
}
