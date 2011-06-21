package uk.ac.ebi.intact.protein.mapping.update;

/**
 * The exception thrown by a ProetinUpdateManager
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05-May-2010</pre>
 */

public class ProteinUpdateException extends Exception {
    public ProteinUpdateException() {
        super();
    }

    public ProteinUpdateException(String message) {
        super(message);
    }

    public ProteinUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProteinUpdateException(Throwable cause) {
        super(cause);
    }
}
