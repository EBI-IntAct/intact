package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.curationTools.actions.PICRSearchProcessWithAccession;
import uk.ac.ebi.intact.curationTools.actions.SwissprotRemappingProcess;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.contexts.BlastContext;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;

import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Mar-2010</pre>
 */

public class StrategyWithIdentifier extends IdentificationStrategyImpl {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyWithIdentifier.class );

    private static ArrayList<String> organismWithSpecialCase = new ArrayList<String>();

    public StrategyWithIdentifier(){
        super();
    }

    protected void initialiseSetOfActions(){
        PICRSearchProcessWithAccession firstAction = new PICRSearchProcessWithAccession();
        this.listOfActions.add(firstAction);

        SwissprotRemappingProcess secondAction = new SwissprotRemappingProcess();
        this.listOfActions.add(secondAction);
    }

    public static final void initialiseOrganismWithSpecialCase(){
        organismWithSpecialCase.add("9606");
        organismWithSpecialCase.add("4932");
        organismWithSpecialCase.add("4896");
        organismWithSpecialCase.add("562");
        organismWithSpecialCase.add("1423");
    }

    private boolean isASpecialOrganism(String taxId){

        if (organismWithSpecialCase.isEmpty()){
            initialiseOrganismWithSpecialCase();
        }

        if (organismWithSpecialCase.contains(taxId)){
            return true;
        }
        return false;
    }

    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {
        IdentificationResults result = new IdentificationResults();

        if (context.getIdentifier() == null){
            throw new StrategyException("The identifier of the protein must be not null.");
        }
        else{

            try {
                String taxId = null;
                if (context.getOrganism() != null){
                    taxId = context.getOrganism().getTaxId();
                }

                String uniprot = this.listOfActions.get(0).runAction(context);

                result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());
                processIsoforms(uniprot, result);

                if (result.getUniprotId() != null){

                    if (isASpecialOrganism(taxId) && !result.isASwissprotEntry()){

                        UniprotProtein tremblEntry = getUniprotProteinFor(result.getUniprotId());
                        String sequence = tremblEntry.getSequence();

                        BlastContext blastContext = new BlastContext(context);

                        if (tremblEntry != null){
                            String ensemblGene = extractENSEMBLGeneAccessionFrom(tremblEntry.getCrossReferences());
                            blastContext.setEnsemblGene(ensemblGene);
                        }
                        else {
                            throw new StrategyException("We couldn't find any Uniprot entries which match this accession number " + result.getUniprotId());
                        }

                        blastContext.setSequence(sequence);

                        uniprot = this.listOfActions.get(1).runAction(blastContext);
                        result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());

                        processIsoforms(uniprot, result);
                    }
                }

            } catch (ActionProcessingException e) {

             throw  new StrategyException("An error occured while trying to identify the protein using the identifier " + context.getIdentifier(), e);

            }
            return result;
        }
    }
}
