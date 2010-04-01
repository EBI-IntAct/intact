package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.curationTools.actions.IdentificationAction;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.model.BioSource;
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

public abstract class ProteinIdentification implements IdentificationStrategy{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( ProteinIdentification.class );

    private static UniprotRemoteService uniprotService = new UniprotRemoteService();

    protected ArrayList<IdentificationAction> listOfActions = new ArrayList<IdentificationAction>();
    private static final Pattern taxIdExpr = Pattern.compile("[0-9]+");
    private static Pattern ensemblGenePattern = Pattern.compile("ENS[A-Z]*G[0-9]+");

    private boolean enableIsoformId = false;

    public ProteinIdentification(){
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
                if (xRef.getDatabase().equals(DatabaseType.ENSEMBL.toString())){
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

    protected String getTaxonIdOfBiosource(String organism){

        if (organism == null){
            return null;
        }
        else {
            if (organism.length() == 0){
                return null;
            }
            else if (isATaxId(organism)){
                return organism;
            }
            else{
                String database = "zpro";
                IntactContext.initContext(new String[] {"/META-INF/"+database+".spring.xml"});
                final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
                final DaoFactory daoFactory = dataContext.getDaoFactory();

                BioSource biosource = daoFactory.getBioSourceDao().getByShortLabel(organism);
                if (biosource == null){
                    return null;
                }
                return biosource.getTaxId();
            }
        }
    }

    protected boolean isATaxId(String organism){
        if (organism != null){
            if (ProteinIdentification.taxIdExpr.matcher(organism).matches()){
                return true;
            }
        }
        return false;
    }

    protected String getScientificNameOfBiosource(String organism){

        if (organism == null){
            return null;
        }
        else {
            String database = "zpro";
            IntactContext.initContext(new String[] {"/META-INF/"+database+".spring.xml"});
            final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
            final DaoFactory daoFactory = dataContext.getDaoFactory();

            if (organism.length() == 0){
                return null;
            }
            else if (isATaxId(organism)){
                BioSource biosource = daoFactory.getBioSourceDao().getByTaxonIdUnique(organism);
                if (biosource == null){
                    return null;
                }
                return biosource.getFullName();
            }
            else{
                BioSource biosource = daoFactory.getBioSourceDao().getByShortLabel(organism);
                if (biosource == null){
                    return null;
                }
                return biosource.getFullName();
            }
        }
    }
}
