package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.curationTools.actions.IntactNameSearchProcess;
import uk.ac.ebi.intact.curationTools.actions.UniprotNameSearchProcess;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
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

public class StrategyWithName extends IdentificationStrategyImpl {
    private IntactContext intactContext;

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyWithSequence.class );

    public StrategyWithName(){
        super();
        this.intactContext = null;
    }

    public void setIntactContext(IntactContext context){
        this.intactContext = context;
    }

    @Override
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {

        IdentificationResults result = new IdentificationResults();

        if (context.getProtein_name() == null && context.getGene_name() == null && context.getGlobalName() == null){
            throw new StrategyException("At least of of these names should be not null : protein name, gene name or a general name.");
        }
        else{

            try {
                String uniprot = this.listOfActions.get(0).runAction(context);
                processIsoforms(uniprot, result);
                result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());

                if (uniprot == null && result.getLastAction().getPossibleAccessions().isEmpty()){
                    if (this.intactContext != null){
                        IntactNameSearchProcess intactProcess = (IntactNameSearchProcess) this.listOfActions.get(1);
                        intactProcess.setIntactContext(this.intactContext);

                        intactProcess.runAction(context);
                        result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());
                    }
                }

            } catch (ActionProcessingException e) {
                throw new StrategyException("Problem trying to match a gene name/protein name to an uniprot entry.");
            }
            processIsoforms(result.getUniprotId(), result);
        }
        return result;
    }

    @Override
    protected void initialiseSetOfActions() {
        UniprotNameSearchProcess firstAction = new UniprotNameSearchProcess();
        this.listOfActions.add(firstAction);

        IntactNameSearchProcess secondAction = new IntactNameSearchProcess();
        this.listOfActions.add(secondAction);
    }
}
