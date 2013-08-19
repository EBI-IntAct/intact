package uk.ac.ebi.intact.bridges.picr;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>25-Mar-2010</pre>
 */

public class PicrClientException extends Exception {
    public PicrClientException() {
        super();
    }

    public PicrClientException(String message) {
        super(message);
    }

    public PicrClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public PicrClientException(Throwable cause) {
        super(cause);
    }
}
