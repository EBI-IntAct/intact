package uk.ac.ebi.intact.application.graph2MIF;

/**
 * This Exception should be thrown, if a fatal Error occours - so the PSIFormat
 * will NOT be valid withour this mising Element
 * @author Henning Mersch <hmersch@ebi.ac.uk>
 */
public class GraphNotConvertableException extends Exception {
    public GraphNotConvertableException() {
    };

    public GraphNotConvertableException(String message) {
        super(message);
    };
}
