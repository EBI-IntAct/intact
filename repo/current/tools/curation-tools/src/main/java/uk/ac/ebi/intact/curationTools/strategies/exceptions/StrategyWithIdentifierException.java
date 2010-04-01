package uk.ac.ebi.intact.curationTools.strategies.exceptions;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-Mar-2010</pre>
 */

public class StrategyWithIdentifierException extends StrategyException {
    public StrategyWithIdentifierException() {
        super();   
    }

    public StrategyWithIdentifierException(String message) {
        super(message);
    }

    public StrategyWithIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }

    public StrategyWithIdentifierException(Throwable cause) {
        super(cause);
    }
}
