package uk.ac.ebi.intact.application.graph2MIF;

/**
 * This Exception should be thrown, if there were no interactors found for the ac.
 * @author Henning Mersch <hmersch@ebi.ac.uk>
 */
public class NoInteractorFoundException extends Exception {
    public NoInteractorFoundException() {
    };

    public NoInteractorFoundException(String message) {
        super(message);
    };
}
