package uk.ac.ebi.intact.protein.mapping.strategies;

import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.factories.ResultsFactory;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;

/**
 * An Identification strategy is a logical set of actions done to identify the protein. A strategy follows the Intact curation rules
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-Mar-2010</pre>
 */

public interface IdentificationStrategy {

    /**
     * Identify the protein depending on the context and following a specific strategy
     * @param context : the context of the protein to identify
     * @return an DefaultIdentificationResults instance containing the results of the strategy
     * @throws uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException
     */
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException;

    /**
     * This method allows or not to keep the isoforms results. If set to false, all the isoforms results are ignored and remapped to
     * the canonical sequence
     * @param enableIsoformId : the boolean value
     */
    public void enableIsoforms(boolean enableIsoformId);

    public ReportsFactory getReportsFactory();

    public void setReportsFactory(ReportsFactory factory);

    public ResultsFactory getResultsFactory();

    public void setResultsFactory(ResultsFactory factory);

}
