package uk.ac.ebi.intact.plugins;

/**
 * Mitab exporter exception.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.9.1
 */
public class MitabExporterException extends Exception {
    public MitabExporterException() {
        super();
    }

    public MitabExporterException( String message ) {
        super( message );
    }

    public MitabExporterException( String message, Throwable cause ) {
        super( message, cause );
    }

    public MitabExporterException( Throwable cause ) {
        super( cause );
    }
}
