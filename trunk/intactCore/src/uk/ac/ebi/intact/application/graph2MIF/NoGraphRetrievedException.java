package uk.ac.ebi.intact.application.graph2MIF;

/**
 * This Exception should be thrown, if there were occoured a exception while retrieving the graph
 * @author Henning Mersch <hmersch@ebi.ac.uk>
 */
public class NoGraphRetrievedException extends Exception {
    public NoGraphRetrievedException() {
    };

    public NoGraphRetrievedException(String message) {
        super(message);
    };
}
