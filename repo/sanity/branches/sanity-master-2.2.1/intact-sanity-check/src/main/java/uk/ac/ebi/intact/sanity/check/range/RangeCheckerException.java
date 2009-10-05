package uk.ac.ebi.intact.sanity.check.range;

/**
 * Range checker specific exception.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class RangeCheckerException extends Exception{
    public RangeCheckerException() {
        super();
    }

    public RangeCheckerException( String message ) {
        super( message );
    }

    public RangeCheckerException( String message, Throwable cause ) {
        super( message, cause );
    }

    public RangeCheckerException( Throwable cause ) {
        super( cause );
    }
}
