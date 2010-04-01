package uk.ac.ebi.intact.curationTools.actions.exception;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public class PicrSearchWithAccessionException extends ActionProcessingException{
    public PicrSearchWithAccessionException() {
        super();
    }

    public PicrSearchWithAccessionException(String message) {
        super(message);
    }

    public PicrSearchWithAccessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PicrSearchWithAccessionException(Throwable cause) {
        super(cause);
    }
}
