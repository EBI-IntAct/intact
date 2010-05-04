package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.curationTools.actions.*;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.IntactCrc64Report;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.contexts.BlastContext;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class StrategyWithSequence extends IdentificationStrategyImpl implements IdentificationAction {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyWithSequence.class );

    private IntactContext intactContext;

    private List<ActionReport> listOfReports = new ArrayList<ActionReport>();

    public StrategyWithSequence(){
        super();
        this.intactContext = null;
    }

    public void setIntactContext(IntactContext context){
        this.intactContext = context;
    }

    private String processLastAction(IdentificationContext context, IdentificationResults result) throws ActionProcessingException {
        BlastContext blastContext = new BlastContext(context);

        String uniprot = this.listOfActions.get(3).runAction(blastContext);

        if (result != null){
            result.getListOfActions().addAll(this.listOfActions.get(3).getListOfActionReports());
            processIsoforms(uniprot, result);
        }
        else {
            this.listOfReports.addAll(this.listOfActions.get(3).getListOfActionReports());
        }

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
                PICRReport lastAction = (PICRReport) result.getLastAction();

                if (!result.hasUniqueUniprotId() && lastAction.getPossibleAccessions().isEmpty()){
                    if (this.intactContext != null){
                        IntactCrc64SearchProcess intactProcess = (IntactCrc64SearchProcess) this.listOfActions.get(1);
                        intactProcess.setIntactContext(this.intactContext);

                        intactProcess.runAction(context);
                        result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());

                        IntactCrc64Report lastReport = (IntactCrc64Report) result.getLastAction();
                        if (lastReport.getIntactid() == null && lastReport.getIntactMatchingProteins().isEmpty()){
                            processLastAction(context, result);
                        }
                    }
                    else {
                        processLastAction(context, result);
                    }
                }
                else {
                    if (result.hasUniqueUniprotId() && !lastAction.isAswissprotEntry()){
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

                        uniprot = this.listOfActions.get(2).runAction(blastContext);
                        result.getListOfActions().addAll(this.listOfActions.get(2).getListOfActionReports());

                        processIsoforms(uniprot, result);

                        BlastReport blastReport = (BlastReport) result.getLastAction();
                        blastReport.addPossibleAccession(tremblEntry.getPrimaryAc());
                    }
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

        SwissprotRemappingProcess thirdAction = new SwissprotRemappingProcess();
        this.listOfActions.add(thirdAction);

        BasicBlastProcess lastAction = new BasicBlastProcess();
        this.listOfActions.add(lastAction);
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {

        String uniprot = this.listOfActions.get(0).runAction(context);
        this.listOfReports.addAll(this.listOfActions.get(0).getListOfActionReports());
        PICRReport report = (PICRReport) this.listOfReports.get(this.listOfReports.size() - 1);

        if (uniprot != null && !report.getPossibleAccessions().isEmpty()){
            if (this.intactContext != null){
                IntactCrc64SearchProcess intactProcess = (IntactCrc64SearchProcess) this.listOfActions.get(1);
                intactProcess.setIntactContext(this.intactContext);

                intactProcess.runAction(context);
                this.listOfReports.addAll(this.listOfActions.get(1).getListOfActionReports());
                IntactCrc64Report report2 = (IntactCrc64Report) this.listOfReports.get(this.listOfReports.size() - 1);

                if (report2.getIntactid() == null && report2.getIntactMatchingProteins().isEmpty()){
                    processLastAction(context, null);
                }
            }
            else {
                processLastAction(context, null);
            }
        }
        else {
            if (uniprot != null && !report.isAswissprotEntry()){
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

                uniprot = this.listOfActions.get(2).runAction(blastContext);
                this.listOfReports.addAll(this.listOfActions.get(2).getListOfActionReports());
            }
        }
        return uniprot;

    }

    public List<ActionReport> getListOfActionReports() {
        return this.listOfReports;
    }
}
