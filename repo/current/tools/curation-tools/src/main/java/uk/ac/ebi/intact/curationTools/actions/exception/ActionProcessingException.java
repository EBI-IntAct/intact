package uk.ac.ebi.intact.curationTools.actions.exception;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public class ActionProcessingException extends Exception{public ActionProcessingException() {
    super();
}

    public ActionProcessingException(String message) {
        super(message);
    }

    public ActionProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionProcessingException(Throwable cause) {
        super(cause);
    }
}
