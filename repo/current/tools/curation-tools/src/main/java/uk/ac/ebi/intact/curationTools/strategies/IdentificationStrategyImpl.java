package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.curationTools.actions.IdentificationAction;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.model.UniprotXref;
import uk.ac.ebi.intact.uniprot.service.IdentifierChecker;
import uk.ac.ebi.intact.uniprot.service.UniprotRemoteService;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public abstract class IdentificationStrategyImpl implements IdentificationStrategy{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( IdentificationStrategyImpl.class );

    private static UniprotRemoteService uniprotService = new UniprotRemoteService();

    protected ArrayList<IdentificationAction> listOfActions = new ArrayList<IdentificationAction>();
    private static final Pattern taxIdExpr = Pattern.compile("[0-9]+");
    private static Pattern ensemblGenePattern = Pattern.compile("ENS[A-Z]*G[0-9]+");

    private boolean enableIsoformId = false;

    public IdentificationStrategyImpl(){
        initialiseSetOfActions();
    }

    public abstract IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException;

    public void enableIsoforms(boolean enableIsoformId) {
        this.enableIsoformId = enableIsoformId;
    }

    public boolean isIsoformEnabled(){
        return enableIsoformId;
    }

    protected abstract void initialiseSetOfActions();

    protected static String extractENSEMBLGeneAccessionFrom(Collection<UniprotXref> crossReferences){

        for (UniprotXref xRef : crossReferences){
            if (xRef.getDatabase() != null){
                if (DatabaseType.ENSEMBL.toString().equalsIgnoreCase(xRef.getDatabase())){
                    String accession = xRef.getAccession();

                    if (ensemblGenePattern.matcher(accession).matches()){
                        return accession;
                    }
                    return xRef.getAccession();
                }
            }
        }

        return null;
    }

    public static String extractENSEMBLGeneAccessionFrom(String uniprotAccession) throws StrategyException{
        UniprotProtein entry = getUniprotProteinFor(uniprotAccession);

        if (entry != null){
            String ensemblGene = extractENSEMBLGeneAccessionFrom(entry.getCrossReferences());
            return ensemblGene;
        }
        else {
            throw new StrategyException("We couldn't find an Uniprot entry which matches this accession number " + uniprotAccession);
        }
    }

    public static String extractENSEMBLGeneAccessionFrom(UniprotProtein protein){

        if (protein != null){
            String ensemblGene = extractENSEMBLGeneAccessionFrom(protein.getCrossReferences());
            return ensemblGene;
        }
        return null;
    }

    protected void processIsoforms(String matchingId, IdentificationResults result) {
        if (matchingId != null){
            if (!this.enableIsoformId){
                if (IdentifierChecker.isSpliceVariantId(matchingId)){
                    if (result == null){
                        result = new IdentificationResults();
                    }

                    if (result.getLastAction() != null){
                        result.getLastAction().addWarning("The identified Uniprot Id is the isoform "+ matchingId +". However, the canonical sequence has been kept.");                        
                    }
                    else {
                        log.error("A uniprot id has been set : " + result.getUniprotId() + ", but no action have been reported.");
                    }
                    matchingId = matchingId.substring(0, matchingId.indexOf("-"));
                    result.setUniprotId(matchingId);
                }

            }
        }

    }

    protected static UniprotProtein getUniprotProteinFor(String accession){
        if (accession == null){
            log.error("You must give a non null Uniprot accession");
        }
        else {
            Collection<UniprotProtein> entries = uniprotService.retrieve(accession);

            if (entries.isEmpty()){
                log.error("The uniprot accession " + accession + " is not valid and couldn't match any UniprotEntry.");
            }
            else if (entries.size() != 1){
                log.error("The uniprot accession " + accession + " is matching several UniprotEntry instances.");
            }
            else {

                return entries.iterator().next();
            }

        }
        return null;
    }

    protected boolean isATaxId(String organism){
        if (organism != null){
            if (IdentificationStrategyImpl.taxIdExpr.matcher(organism).matches()){
                return true;
            }
        }
        return false;
    }
}
