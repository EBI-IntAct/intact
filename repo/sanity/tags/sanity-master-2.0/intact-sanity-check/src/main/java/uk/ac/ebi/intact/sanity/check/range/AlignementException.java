package uk.ac.ebi.intact.sanity.check.range;

/**
 * TODO comment that class header
 *
 * @author Samuel Kerrien
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class AlignementException extends Exception {
    public AlignementException() {
        super();
    }

    public AlignementException( String message ) {
        super( message );
    }

    public AlignementException( String message, Throwable cause ) {
        super( message, cause );
    }

    public AlignementException( Throwable cause ) {
        super( cause );
    }
}
