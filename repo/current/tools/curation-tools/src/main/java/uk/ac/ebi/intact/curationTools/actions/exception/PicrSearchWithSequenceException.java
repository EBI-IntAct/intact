package uk.ac.ebi.intact.curationTools.actions.exception;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class PicrSearchWithSequenceException extends ActionProcessingException{
    public PicrSearchWithSequenceException() {
        super();
    }

    public PicrSearchWithSequenceException(String message) {
        super(message);
    }

    public PicrSearchWithSequenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PicrSearchWithSequenceException(Throwable cause) {
        super(cause);
    }
}
