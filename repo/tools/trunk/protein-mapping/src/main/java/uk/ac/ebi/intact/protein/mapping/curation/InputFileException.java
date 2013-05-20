package uk.ac.ebi.intact.protein.mapping.curation;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Mar-2010</pre>
 */

public class InputFileException extends RuntimeException {
    public InputFileException() {
        super();
    }

    public InputFileException(String message) {
        super(message);
    }

    public InputFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputFileException(Throwable cause) {
        super(cause);
    }
}
