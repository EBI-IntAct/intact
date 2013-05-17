package uk.ac.ebi.intact.bridges.citexplore.exceptions;

/**
 * When an unexpected error occurs.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/12/12</pre>
 */

public class UnexpectedException extends Exception {
    public UnexpectedException( String message, Throwable cause ) {
        super( message, cause );
    }
}
