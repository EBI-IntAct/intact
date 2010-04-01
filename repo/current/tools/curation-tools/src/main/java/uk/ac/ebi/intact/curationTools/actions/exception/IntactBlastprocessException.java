package uk.ac.ebi.intact.curationTools.actions.exception;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class IntactBlastprocessException extends ActionProcessingException {
    public IntactBlastprocessException() {
        super();
    }

    public IntactBlastprocessException(String message) {
        super(message);
    }

    public IntactBlastprocessException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntactBlastprocessException(Throwable cause) {
        super(cause);
    }
}
