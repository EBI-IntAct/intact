package uk.ac.ebi.intact.bridges.imexcentral;

/**
 * IMEx Central exception.
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.2
 */
public class ImexCentralException extends Exception{
    public ImexCentralException() {
        super();
    }

    public ImexCentralException( String message ) {
        super( message );
    }

    public ImexCentralException( String message, Throwable cause ) {
        super( message, cause );
    }

    public ImexCentralException( Throwable cause ) {
        super( cause );
    }
}
