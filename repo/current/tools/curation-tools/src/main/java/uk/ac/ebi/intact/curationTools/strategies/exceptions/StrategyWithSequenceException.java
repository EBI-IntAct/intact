package uk.ac.ebi.intact.curationTools.strategies.exceptions;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class StrategyWithSequenceException extends StrategyException {
    public StrategyWithSequenceException() {
        super();
    }

    public StrategyWithSequenceException(String message) {
        super(message);
    }

    public StrategyWithSequenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public StrategyWithSequenceException(Throwable cause) {
        super(cause);
    }
}
