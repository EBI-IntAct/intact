package uk.ac.ebi.intact.curationTools.actions.exception;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public class SwissprotRemappingException extends ActionProcessingException{
    public SwissprotRemappingException() {
        super();
    }

    public SwissprotRemappingException(String message) {
        super(message);
    }

    public SwissprotRemappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SwissprotRemappingException(Throwable cause) {
        super(cause);  
    }
}
