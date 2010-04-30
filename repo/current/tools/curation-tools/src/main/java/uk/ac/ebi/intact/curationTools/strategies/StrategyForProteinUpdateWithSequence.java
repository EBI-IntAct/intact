package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.curationTools.actions.UniprotIdentityBlastProcess;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27-Apr-2010</pre>
 */

public class StrategyForProteinUpdateWithSequence extends IdentificationStrategyImpl {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyForProteinUpdateWithSequence.class );

    public StrategyForProteinUpdateWithSequence(){
        super();
    }

    @Override
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {
        IdentificationResults result = new IdentificationResults();

        if (context.getSequence() == null){
            throw new StrategyException("The sequence of the protein must be not null.");
        }
        else {
            String uniprot = null;
            try {
                uniprot = this.listOfActions.get(0).runAction(context);
                result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());
                processIsoforms(uniprot, result);

            } catch (ActionProcessingException e) {
                throw  new StrategyException("An error occured while trying to update the protein using the sequence " + context.getSequence(), e);
            }
        }
        return result;
    }

    @Override
    protected void initialiseSetOfActions() {
        UniprotIdentityBlastProcess firstAction = new UniprotIdentityBlastProcess();
        this.listOfActions.add(firstAction);
    }
}
