package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.curationTools.actions.IntactBlastProcess;
import uk.ac.ebi.intact.curationTools.actions.PICRSearchProcessWithSequence;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.actions.exception.IntactBlastprocessException;
import uk.ac.ebi.intact.curationTools.actions.exception.PicrSearchWithSequenceException;
import uk.ac.ebi.intact.curationTools.model.contexts.BlastContext;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.contexts.TaxIdContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyWithIdentifierException;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyWithSequenceException;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class StrategyWithSequence extends ProteinIdentification{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyWithSequence.class );

    public StrategyWithSequence(){
        super();
    }

    @Override
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {
        IdentificationResults result = new IdentificationResults();

        if (context.getSequence() == null){
            throw new StrategyWithSequenceException("The sequence of the protein must be not null.");
        }
        else{

            try {
                TaxIdContext taxIdContext = new TaxIdContext(context);
                String taxId = getTaxonIdOfBiosource(context.getOrganism());
                taxIdContext.setDeducedTaxId(taxId);

                String uniprot = this.listOfActions.get(0).runAction(taxIdContext);
                result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());
                processIsoforms(uniprot, result);

                if (!result.hasUniqueUniprotId() && !result.getLastAction().getPossibleAccessions().isEmpty()){
                    BlastContext blastContext = new BlastContext(taxIdContext);

                    String scientificNameOfOrganism = getScientificNameOfBiosource(blastContext.getOrganism());

                    if (scientificNameOfOrganism == null && blastContext.getOrganism() != null){
                        throw  new StrategyWithIdentifierException(" We couldn't find the scientific name of the organism " + blastContext.getOrganism() + " associated with the sequence " + blastContext.getSequence() + ". Could you check that you gave either a valid TaxId or a Biosource shortlabel.");
                    }
                    else if (scientificNameOfOrganism != null){
                        blastContext.setDeducedScientificOrganismName(scientificNameOfOrganism);
                        uniprot = this.listOfActions.get(1).runAction(blastContext);
                        result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());
                        processIsoforms(uniprot, result);
                    }
                }

            } catch (ActionProcessingException e) {

                if (e instanceof PicrSearchWithSequenceException){
                    throw  new StrategyWithSequenceException("PICR couldn't match an Uniprot entry to the sequence " + context.getSequence() + ". Check your sequence and/or organism.", e);
                }
                else if (e instanceof IntactBlastprocessException){
                    throw  new StrategyWithSequenceException("A problem occured while running a blast on Intact and/or Uniprot.", e);                     
                }
            }
            return result;
        }
    }

    @Override
    protected void initialiseSetOfActions() {
        PICRSearchProcessWithSequence firstAction = new PICRSearchProcessWithSequence();
        this.listOfActions.add(firstAction);

        IntactBlastProcess secondAction = new IntactBlastProcess();
        this.listOfActions.add(secondAction);
    }
}
