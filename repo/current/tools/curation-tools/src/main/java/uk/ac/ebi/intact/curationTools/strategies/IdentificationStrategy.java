package uk.ac.ebi.intact.curationTools.strategies;

import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-Mar-2010</pre>
 */

public interface IdentificationStrategy {

    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException;

    public void enableIsoforms(boolean enableIsoformId);

}
