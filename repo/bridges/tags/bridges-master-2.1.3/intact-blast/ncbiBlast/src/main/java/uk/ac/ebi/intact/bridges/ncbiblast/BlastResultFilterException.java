package uk.ac.ebi.intact.bridges.ncbiblast;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18-Mar-2010</pre>
 */

public class BlastResultFilterException extends RuntimeException {
    public BlastResultFilterException(String message) {
        super(message);  
    }

    public BlastResultFilterException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlastResultFilterException(Throwable cause) {
        super(cause);
    }

    public BlastResultFilterException() {
        super();
    }
}
