package uk.ac.ebi.intact.application.graph2MIF;

/**
 * This Exception should be thrown, if there the DOM-Object could not be serialized
 * @author Henning Mersch <hmersch@ebi.ac.uk>
 */
public class MIFSerializeException extends Exception {
    public MIFSerializeException() {
    };

    public MIFSerializeException(String message) {
        super(message);
    };
}
