package uk.ac.ebi.intact.curationTools.proteinUpdateTool;

import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.core.IntactTransactionException;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.contexts.UpdateContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.StrategyForProteinUpdate;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.dbupdate.prot.ProteinUpdateProcessor;
import uk.ac.ebi.intact.dbupdate.prot.ProteinUpdateProcessorConfig;
import uk.ac.ebi.intact.dbupdate.prot.report.FileReportHandler;
import uk.ac.ebi.intact.dbupdate.prot.report.UpdateReportHandler;
import uk.ac.ebi.intact.model.*;

import javax.persistence.Query;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Mar-2010</pre>
 */

public class ProteinUpdateManager {

    private List<ProteinImpl> proteinToUpdate = new ArrayList<ProteinImpl>();
    private StrategyForProteinUpdate strategy;

    private IntactContext intactContext;

    public ProteinUpdateManager(){
        this.strategy = new StrategyForProteinUpdate();
        this.strategy.enableIsoforms(false);
        this.strategy.setBasicBlastProcessRequired(false);
    }

    public ProteinUpdateManager(IntactContext context){
        this.strategy = new StrategyForProteinUpdate();
        this.strategy.enableIsoforms(false);
        this.strategy.setBasicBlastProcessRequired(false);
        setIntactContext(context);
    }

    public void setIntactContext(IntactContext intactContext) {
        this.intactContext = intactContext;
        this.strategy.setIntactContextForFeatureRangeChecking(intactContext);
    }

    private HashMap<String, UpdateContext> getListOfContextsForProteinToUpdate() throws ProteinUpdateException, StrategyException {

        if (this.intactContext == null){
            throw new StrategyException("We can't update the proteins without any IntactContext instance. please set the intactContext of the ProteinUpdateManager.");
        }

        final DataContext dataContext = this.intactContext.getDataContext();
        TransactionStatus transactionStatus = dataContext.beginTransaction();

        final DaoFactory daoFactory = dataContext.getDaoFactory();
        final Query query = daoFactory.getEntityManager().createQuery("select distinct p from InteractorImpl p "+
                "left join p.sequenceChunks as seq " +
                "left join p.xrefs as xrefs " +
                "where p.objClass = 'uk.ac.ebi.intact.model.ProteinImpl' "+
                "and p not in ( "+
                "select p2 "+
                "from InteractorImpl p2 join p2.xrefs as xrefs "+
                "where p2.objClass = 'uk.ac.ebi.intact.model.ProteinImpl' "+
                "and xrefs.cvDatabase.ac = 'EBI-31' "+
                "and xrefs.cvXrefQualifier.shortLabel = 'identity')");

        HashMap<String, UpdateContext> contexts = new HashMap<String, UpdateContext>();

        proteinToUpdate = query.getResultList();
        System.out.println(proteinToUpdate.size());

        for (ProteinImpl prot : proteinToUpdate){
            String accession = prot.getAc();
            Collection<InteractorXref> refs = prot.getXrefs();
            String sequence = prot.getSequence();
            BioSource organism = prot.getBioSource();

            UpdateContext context = new UpdateContext();
            contexts.put(accession, context);

            context.setSequence(sequence);
            context.setOrganism(organism);
            context.setIntactAccession(accession);
            addIdentityCrossreferencesToContext(refs, context);
        }

        try {
            dataContext.commitTransaction(transactionStatus);
        } catch (IntactTransactionException e) {
            throw new ProteinUpdateException(e);
        }
        return contexts;
    }

    private boolean isIdentityCrossReference(CvXrefQualifier qualifier){
        if (qualifier.getIdentifier() != null){
            if (qualifier.getIdentifier().equals(CvXrefQualifier.IDENTITY_MI_REF)){
                return true;
            }

        }
        else {
            if (qualifier.getShortLabel().equals(CvXrefQualifier.IDENTITY)){
                return true;
            }
        }
        return false;
    }

    private void addIdentityCrossreferencesToContext(Collection<InteractorXref> refs, UpdateContext context){
        for (InteractorXref ref : refs){
            if (ref.getPrimaryId() != null){
                if (ref.getCvXrefQualifier() != null){
                    CvXrefQualifier qualifier = ref.getCvXrefQualifier();

                    if (isIdentityCrossReference(qualifier)){
                        context.addIdentifier(ref.getPrimaryId());
                    }
                }
            }
        }
    }

    private Annotation collectNo_Uniprot_UpdateAnnotation(Collection<Annotation> annotations){
        for (Annotation a : annotations){
            if (a.getCvTopic() != null){
                CvTopic topic = a.getCvTopic();

                if (topic.getIdentifier() != null){
                    if (topic.getIdentifier().equals("IA:0280")){
                        return a;
                    }
                }
                else if (topic.getShortLabel() != null){
                    if (topic.getShortLabel().equals("no-uniprot-update")){
                        return a;
                    }
                }
            }
        }
        return null;
    }

    private InteractorXref createIdentityInteractorXrefForUniprotAc(String uniprotAc){
        if (uniprotAc == null){
            return null;
        }

        final CvDatabase uniprot = this.intactContext.getDaoFactory().getCvObjectDao(CvDatabase.class).getByPsiMiRef( CvDatabase.UNIPROT_MI_REF );
        final CvXrefQualifier identity = this.intactContext.getDaoFactory().getCvObjectDao(CvXrefQualifier.class).getByPsiMiRef(CvXrefQualifier.IDENTITY_MI_REF);

        InteractorXref xRef = new InteractorXref(this.intactContext.getInstitution(), uniprot, uniprotAc, identity);

        return xRef;
    }

    /*private ProteinImpl getProteinWithIdentityUniprotCrossReference(String uniprotAc){
        List<ProteinImpl> existingProt = this.intactContext.getDaoFactory().getProteinDao().getByUniprotId(uniprotAc);

        if (existingProt.isEmpty()){
            return null;
        }
        else if (existingProt.size() > 1){
            System.err.println(existingProt.size() + "proteins already exist with the same uniprot cross reference " + uniprotAc + ". We will take only the first protein and remap the identified protein to this protein.");
            return existingProt.get(0);
        }
        else {
            return existingProt.get(0);
        }
    }*/

    private void addUniprotCrossReferenceTo(ProteinImpl prot, String uniprotAc){
        InteractorXref ref = createIdentityInteractorXrefForUniprotAc(uniprotAc);

        if (ref != null){
            prot.addXref(ref);
        }
    }

    /*private void replaceInInteractions (ProteinImpl proteinToReplace, ProteinImpl replacingProtein){
        List<Interaction> interactions = this.intactContext.getDaoFactory().getInteractionDao().getInteractionsByInteractorAc(proteinToReplace.getAc());

        for (Interaction interaction : interactions){
            Collection<Component> components = interaction.getComponents();

            for (Component component : components){
                if (component.getInteractorAc() != null){
                    if (component.getInteractorAc().equals(proteinToReplace.getAc())){
                        component.setInteractorAc(replacingProtein.getAc());
                        component.setInteractor(replacingProtein);
                        this.intactContext.getCorePersister().saveOrUpdate(component);
                        break;
                    }
                }
            }
            this.intactContext.getCorePersister().saveOrUpdate(interaction);
        }
    }*/

    public void updateProteins() throws ProteinUpdateException {
        final DataContext dataContext = this.intactContext.getDataContext();
        TransactionStatus transactionStatus = dataContext.beginTransaction();
        try {
            File file = new File("/home/marine/Desktop/updateReportTest.txt");
            FileWriter writer = new FileWriter(file);

            final DaoFactory daoFactory = dataContext.getDaoFactory();
            final Query query = daoFactory.getEntityManager().createQuery("select distinct p from InteractorImpl p "+
                    "left join p.sequenceChunks as seq " +
                    "left join p.xrefs as xrefs " +
                    "left join p.annotations as annotations " +
                    "where p.objClass = 'uk.ac.ebi.intact.model.ProteinImpl' "+
                    "and p not in ( "+
                    "select p2 "+
                    "from InteractorImpl p2 join p2.xrefs as xrefs "+
                    "where p2.objClass = 'uk.ac.ebi.intact.model.ProteinImpl' "+
                    "and xrefs.cvDatabase.ac = 'EBI-31' "+
                    "and xrefs.cvXrefQualifier.shortLabel = 'identity')");

            proteinToUpdate = query.getResultList();
            System.out.println(proteinToUpdate.size());

            ArrayList<String> accessionsToUpdate = new ArrayList<String>();

            for (ProteinImpl prot : proteinToUpdate){

                String accession = prot.getAc();
                String shortLabel = prot.getShortLabel();
                System.out.println("Protein AC = " + accession + " shortLabel = " + shortLabel);

                Collection<InteractorXref> refs = prot.getXrefs();
                Collection<Annotation> annotations = prot.getAnnotations();
                String sequence = prot.getSequence();
                BioSource organism = prot.getBioSource();

                // context
                UpdateContext context = new UpdateContext();
                context.setSequence(sequence);
                context.setOrganism(organism);
                context.setIntactAccession(accession);
                addIdentityCrossreferencesToContext(refs, context);

                // result
                IdentificationResults result = this.strategy.identifyProtein(context);
                writeResultReports(accession, result, writer);

                // update
                if (result != null && result.getUniprotId() != null){
                    Annotation a = collectNo_Uniprot_UpdateAnnotation(annotations);

                    if (a != null){
                        prot.removeAnnotation(a);
                    }
                    addUniprotCrossReferenceTo(prot, result.getUniprotId());

                    this.intactContext.getCorePersister().saveOrUpdate(prot);

                    accessionsToUpdate.add(accession);
                }
            }
            dataContext.commitTransaction(transactionStatus);

            UpdateReportHandler reportHandler = new FileReportHandler(new File("target"));
            ProteinUpdateProcessorConfig configUpdate = new ProteinUpdateProcessorConfig(reportHandler);

            ProteinUpdateProcessor protUpdateProcessor = new ProteinUpdateProcessor(configUpdate);
            protUpdateProcessor.updateByACs(accessionsToUpdate);

        } catch (IntactTransactionException e) {
            throw new ProteinUpdateException(e);
        } catch (StrategyException e) {
            throw new ProteinUpdateException("There is a problem when executing the protein update strategy. Check the protein contexts.", e);
        } catch (IOException e) {
            throw new ProteinUpdateException("We can't write the results in a file.", e);
        }
    }

    public void writeResultsOfProteinUpdate() throws ProteinUpdateException, StrategyException {

        HashMap<String, UpdateContext> contexts= getListOfContextsForProteinToUpdate();
        File file = new File("/home/marine/Desktop/updateReportTest.txt");

        try {
            FileWriter writer = new FileWriter(file);
            for (String protAc : contexts.keySet()){
                System.out.println("protAc = " + protAc);
                UpdateContext context = contexts.get(protAc);
                IdentificationResults result = this.strategy.identifyProtein(context);

                writeResultReports(protAc, result, writer);
            }
            writer.close();
        } catch (IOException e) {
            throw new ProteinUpdateException("We can't write the results in a file.", e);
        } catch (StrategyException e) {
            throw new ProteinUpdateException("There is a problem when executing the protein update strategy. Check the protein contexts.", e);
        }
    }

    private void writeResultReports(String protAc, IdentificationResults result, FileWriter writer) throws ProteinUpdateException {
        try {
            writer.write("************************" + protAc + "************************************ \n");

            writer.write("Uniprot accession found : " + result.getUniprotId() + "\n");
            for (ActionReport report : result.getListOfActions()){
                writer.write(report.getName().toString() + " : " + report.getStatus().getLabel() + ", " + report.getStatus().getDescription() + "\n");

                for (String warn : report.getWarnings()){
                    writer.write(warn + "\n");
                }

                for (String ac : report.getPossibleAccessions()){
                    writer.write("possible accession : " + ac + "\n");
                }

                if (report instanceof PICRReport){
                    PICRReport picr = (PICRReport) report;
                    writer.write("Is a Swissprot entry : " + picr.isAswissprotEntry() + "\n");

                    for (String database : picr.getCrossReferences().keySet()){
                        writer.write(database + " cross reference : " + picr.getCrossReferences().get(database) + "\n");
                    }
                }
                else if (report instanceof BlastReport){
                    BlastReport blast = (BlastReport) report;

                    for (BlastProtein prot : blast.getBlastMatchingProteins()){
                        writer.write("BLAST Protein " + prot.getAccession() + " : identity = " + prot.getIdentity() + "\n");
                    }
                }
            }
        } catch (IOException e) {
            throw new ProteinUpdateException("We can't write the results of the protein " + protAc, e);
        }
    }
}
