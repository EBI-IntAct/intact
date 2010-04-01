package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.curationTools.actions.PICRSearchProcessWithAccession;
import uk.ac.ebi.intact.curationTools.actions.SwissprotRemappingProcess;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.actions.exception.PicrSearchWithAccessionException;
import uk.ac.ebi.intact.curationTools.actions.exception.SwissprotRemappingException;
import uk.ac.ebi.intact.curationTools.model.contexts.BlastContext;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.contexts.TaxIdContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyWithIdentifierException;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;

import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Mar-2010</pre>
 */

public class StrategyWithIdentifier extends ProteinIdentification {

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

    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyWithIdentifierException {
        IdentificationResults result = new IdentificationResults();

        if (context.getIdentifier() == null){
            throw new StrategyWithIdentifierException("The identifier of the protein must be not null.");
        }
        else{

            try {
                TaxIdContext taxIdContext = new TaxIdContext(context);
                String taxId = getTaxonIdOfBiosource(context.getOrganism());
                taxIdContext.setDeducedTaxId(taxId);

                String uniprot = this.listOfActions.get(0).runAction(taxIdContext);

                result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());
                processIsoforms(uniprot, result);

                if (result.getUniprotId() != null){

                    if (isASpecialOrganism(taxId) && !result.isASwissprotEntry()){

                        UniprotProtein tremblEntry = getUniprotProteinFor(result.getUniprotId());
                        String sequence = tremblEntry.getSequence();

                        BlastContext blastContext = new BlastContext(taxIdContext);

                        if (tremblEntry != null){
                            String ensemblGene = extractENSEMBLGeneAccessionFrom(tremblEntry.getCrossReferences());
                            blastContext.setEnsemblGene(ensemblGene);
                        }
                        else {
                            throw new StrategyWithIdentifierException("We couldn't find any Uniprot entries which match this accession number " + result.getUniprotId());
                        }

                        blastContext.setSequence(sequence);

                        String scientificNameOfOrganism = getScientificNameOfBiosource(blastContext.getOrganism());

                        if (scientificNameOfOrganism == null && blastContext.getOrganism() != null){
                            throw  new StrategyWithIdentifierException(" We couldn't find the scientific name of the organism " + blastContext.getOrganism() + " associated with the identifier " + blastContext.getIdentifier() + ". Could you check that you gave either a valid TaxId or a Biosource shortlabel.");
                        }
                        else if (scientificNameOfOrganism != null){
                            blastContext.setDeducedScientificOrganismName(scientificNameOfOrganism);
                            uniprot = this.listOfActions.get(1).runAction(blastContext);
                            result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());

                            processIsoforms(uniprot, result);
                        }
                    }
                }

            } catch (ActionProcessingException e) {

                if (e instanceof PicrSearchWithAccessionException){
                    throw  new StrategyWithIdentifierException("PICR couldn't match anything to the identifier " + context.getIdentifier() + ". Check your identifier and/or organism.", e);
                }
                else if (e instanceof SwissprotRemappingException){
                    throw  new StrategyWithIdentifierException("A problem occured while trying to remap a trembl entry to a swissprot entry using a blast on swissprot. Check your organism.", e);                    
                }

            }
            return result;
        }
    }
}
