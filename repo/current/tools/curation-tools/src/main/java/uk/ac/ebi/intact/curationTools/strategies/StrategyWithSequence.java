package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.curationTools.actions.BasicBlastProcess;
import uk.ac.ebi.intact.curationTools.actions.IntactCrc64SearchProcess;
import uk.ac.ebi.intact.curationTools.actions.PICRSearchProcessWithSequence;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.IntactCrc64Report;
import uk.ac.ebi.intact.curationTools.model.contexts.BlastContext;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class StrategyWithSequence extends IdentificationStrategyImpl {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyWithSequence.class );

    private IntactContext intactContext;

    public StrategyWithSequence(){
        super();
        this.intactContext = null;
    }

    public void setIntactContext(IntactContext context){
        this.intactContext = context;
    }

    private String processLastAction(IdentificationContext context, IdentificationResults result) throws ActionProcessingException {
        BlastContext blastContext = new BlastContext(context);

        String uniprot = this.listOfActions.get(2).runAction(blastContext);
        result.getListOfActions().addAll(this.listOfActions.get(2).getListOfActionReports());
        processIsoforms(uniprot, result);

        return uniprot;
    }

    @Override
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {
        IdentificationResults result = new IdentificationResults();

        if (context.getSequence() == null){
            throw new StrategyException("The sequence of the protein must be not null.");
        }
        else{

            try {

                String uniprot = this.listOfActions.get(0).runAction(context);
                result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());
                processIsoforms(uniprot, result);

                if (!result.hasUniqueUniprotId() && !result.getLastAction().getPossibleAccessions().isEmpty()){
                    if (this.intactContext != null){
                        IntactCrc64SearchProcess intactProcess = (IntactCrc64SearchProcess) this.listOfActions.get(1);
                        intactProcess.setIntactContext(this.intactContext);

                        intactProcess.runAction(context);

                        IntactCrc64Report lastReport = (IntactCrc64Report) result.getLastAction();
                        if (lastReport.getIntactid() == null && lastReport.getIntactMatchingProteins().isEmpty()){
                            processLastAction(context, result);
                        }
                    }
                }
                else {
                    processLastAction(context, result);
                }

            } catch (ActionProcessingException e) {

                throw  new StrategyException("An error occured while trying to identify the protein using the sequence " + context.getSequence(), e);

            }
            return result;
        }
    }

    @Override
    protected void initialiseSetOfActions() {
        PICRSearchProcessWithSequence firstAction = new PICRSearchProcessWithSequence();
        this.listOfActions.add(firstAction);

        IntactCrc64SearchProcess secondAction = new IntactCrc64SearchProcess();

        this.listOfActions.add(secondAction);

        BasicBlastProcess thirdAction = new BasicBlastProcess();
        this.listOfActions.add(thirdAction);
    }
}
