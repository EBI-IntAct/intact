package uk.ac.ebi.intact.curationTools.strategies.exceptions;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class StrategyWithNameException extends StrategyException {
    public StrategyWithNameException() {
        super();  
    }

    public StrategyWithNameException(String message) {
        super(message);
    }

    public StrategyWithNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public StrategyWithNameException(Throwable cause) {
        super(cause);
    }
}
