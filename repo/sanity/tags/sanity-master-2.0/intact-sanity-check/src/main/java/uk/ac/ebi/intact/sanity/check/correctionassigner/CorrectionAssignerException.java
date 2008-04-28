package uk.ac.ebi.intact.sanity.check.correctionassigner;

/**
 * TODO comment that class header
 *
 * @author Samuel Kerrien
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class CorrectionAssignerException extends Exception{
    public CorrectionAssignerException() {
        super();
    }

    public CorrectionAssignerException( String message ) {
        super( message );
    }

    public CorrectionAssignerException( String message, Throwable cause ) {
        super( message, cause );
    }

    public CorrectionAssignerException( Throwable cause ) {
        super( cause );
    }
}
