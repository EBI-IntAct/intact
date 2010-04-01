package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.curationTools.actions.NameSearchProcess;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.contexts.TaxIdContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyWithNameException;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-Mar-2010</pre>
 */

public class StrategyWithName extends ProteinIdentification {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyWithSequence.class );

    public StrategyWithName(){
        super();
    }
    @Override
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {

        IdentificationResults result = new IdentificationResults();

        if (context.getProtein_name() == null && context.getGene_name() == null){
            throw new StrategyWithNameException("At least of of these names should be not null : protein or gene name.");
        }
        else{
            TaxIdContext taxIdContext = new TaxIdContext(context);

            String taxId = getTaxonIdOfBiosource(taxIdContext.getOrganism());
            taxIdContext.setDeducedTaxId(taxId);

            try {
                String uniprot = this.listOfActions.get(0).runAction(taxIdContext);

                result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());
                processIsoforms(uniprot, result);

            } catch (ActionProcessingException e) {
                throw new StrategyWithNameException("Problem trying to match a gene name/protein name to an uniprot entry.");
            }
            processIsoforms(result.getUniprotId(), result);
        }
        return result;
    }

    @Override
    protected void initialiseSetOfActions() {
        NameSearchProcess firstAction = new NameSearchProcess();
        this.listOfActions.add(firstAction);
    }
}
