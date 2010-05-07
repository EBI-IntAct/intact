package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.curationTools.actions.IdentificationAction;
import uk.ac.ebi.intact.curationTools.actions.PICRSearchProcessWithAccession;
import uk.ac.ebi.intact.curationTools.actions.SwissprotRemappingProcess;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.contexts.BlastContext;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;

import java.util.ArrayList;
import java.util.List;

/**
 * This strategy aims at identifying a protein using its identifier and organism. It can be also a complex IdentificationAction
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Mar-2010</pre>
 */

public class StrategyWithIdentifier extends IdentificationStrategyImpl implements IdentificationAction{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyWithIdentifier.class );

    /**
     * The list of reports of this action
     */
    private List<ActionReport> listOfReports = new ArrayList<ActionReport> ();

    /**
     * the list of special organisms entirely sequenced
     */
    private static ArrayList<String> organismWithSpecialCase = new ArrayList<String>();

    /**
     * Create a new strategy with identifier
     */
    public StrategyWithIdentifier(){
        super();
    }

    /**
     * Initialise the set of actions for this strategy
     */
    protected void initialiseSetOfActions(){

        // first action = PICRSearchProcessWithAccession
        PICRSearchProcessWithAccession firstAction = new PICRSearchProcessWithAccession();
        this.listOfActions.add(firstAction);

        // second action = SwissprotRemappingProcess
        SwissprotRemappingProcess secondAction = new SwissprotRemappingProcess();
        this.listOfActions.add(secondAction);
    }

    /**
     * initialises the list of special organisms
     */
    public static final void initialiseOrganismWithSpecialCase(){
        organismWithSpecialCase.add("9606");
        organismWithSpecialCase.add("4932");
        organismWithSpecialCase.add("4896");
        organismWithSpecialCase.add("562");
        organismWithSpecialCase.add("1423");
    }

    /**
     *
     * @param taxId : the taxId to check
     * @return  true if it is a special organism entirely sequenced
     */
    private boolean isASpecialOrganism(String taxId){

        if (organismWithSpecialCase.isEmpty()){
            initialiseOrganismWithSpecialCase();
        }

        if (organismWithSpecialCase.contains(taxId)){
            return true;
        }
        return false;
    }

    /**
     * This strategy is using PICR to map the identifier to an unique uniprot AC. If an unique Trembl is found,
     * the strategy will use the SwissprotRemappingProcess to remap the trembl entry to a Swissprot entry.
     * @param context : the context of the protein to identify
     * @return the results
     * @throws StrategyException
     */
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {
        // new result
        IdentificationResults result = new IdentificationResults();

        // the strategy is based on the identifier
        if (context.getIdentifier() == null){
            throw new StrategyException("The identifier of the protein must be not null.");
        }
        else{

            try {

                // result of PICR
                String uniprot = this.listOfActions.get(0).runAction(context);
                // get the reports of the first action
                result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());
                // process the isoforms and set the uniprot id of the result
                processIsoforms(uniprot, result);

                // PICR could map the identifier to a Swissprot accession
                if (result.getUniprotId() != null){
                    PICRReport picrReport = (PICRReport) result.getLastAction();
                    if (!picrReport.isAswissprotEntry()){

                        UniprotProtein tremblEntry = getUniprotProteinFor(result.getUniprotId());
                        String sequence = tremblEntry.getSequence();

                        BlastContext blastContext = new BlastContext(context);
                        blastContext.setSequence(sequence);

                        if (tremblEntry != null){
                            String ensemblGene = extractENSEMBLGeneAccessionFrom(tremblEntry.getCrossReferences());
                            blastContext.setEnsemblGene(ensemblGene);
                        }
                        else {
                            throw new StrategyException("We couldn't find any Uniprot entries which match this accession number " + result.getUniprotId());
                        }
                        uniprot = this.listOfActions.get(1).runAction(blastContext);
                        result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());

                        processIsoforms(uniprot, result);

                        BlastReport blastReport = (BlastReport) result.getLastAction();
                        blastReport.addPossibleAccession(tremblEntry.getPrimaryAc());
                    }
                }

            } catch (ActionProcessingException e) {

                throw  new StrategyException("An error occured while trying to identify the protein using the identifier " + context.getIdentifier(), e);

            }
            return result;
        }
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {
        this.listOfReports.clear();

        String uniprot = this.listOfActions.get(0).runAction(context);
        this.listOfReports.addAll(this.listOfActions.get(0).getListOfActionReports());
        PICRReport report = (PICRReport) this.listOfReports.get(this.listOfReports.size() - 1);

        if (uniprot != null){

            if (!report.isAswissprotEntry()){

                UniprotProtein tremblEntry = getUniprotProteinFor(uniprot);
                String sequence = tremblEntry.getSequence();

                BlastContext blastContext = new BlastContext(context);
                blastContext.setSequence(sequence);

                if (tremblEntry != null){
                    String ensemblGene = extractENSEMBLGeneAccessionFrom(tremblEntry.getCrossReferences());
                    blastContext.setEnsemblGene(ensemblGene);
                }
                else {
                    throw new ActionProcessingException("We couldn't find any Uniprot entries which match this accession number " + uniprot);
                }
                String uniprot2 = this.listOfActions.get(1).runAction(blastContext);

                if (uniprot2 != null){
                    uniprot = uniprot2;
                }
                this.listOfReports.addAll(this.listOfActions.get(1).getListOfActionReports());

                BlastReport blastReport = (BlastReport) this.listOfReports.get(this.listOfReports.size() - 1);
                blastReport.addPossibleAccession(tremblEntry.getPrimaryAc());
            }
        }
        return uniprot;
    }

    public List<ActionReport> getListOfActionReports() {
        return this.listOfReports;
    }
}
