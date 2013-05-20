package uk.ac.ebi.intact.protein.mapping.update;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.core.IntactTransactionException;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultBlastReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultPICRReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.UpdateContext;
import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.results.PICRCrossReferences;
import uk.ac.ebi.intact.protein.mapping.strategies.StrategyForProteinUpdate;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;

import javax.persistence.Query;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

//import uk.ac.ebi.intact.dbupdate.prot.ProteinUpdateProcessor;

/**
 * This class can update the Intact proteins following a specific update strategy
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Mar-2010</pre>
 */

public class ProteinUpdateManager {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( ProteinUpdateManager.class );

    /**
     * the list of proteins to update
     */
    private List<ProteinImpl> proteinToUpdate = new ArrayList<ProteinImpl>();

    /**
     * the strategy used to update the proteins
     */
    private StrategyForProteinUpdate strategy;

    /**
     * the context of the protein to update
     */
    private UpdateContext context;

    /**
     * create a new ProteinUpdate manager.The strategy for update doesn't take into account the isoforms and keep the canonical sequence.
     */
    public ProteinUpdateManager(){
        this.strategy = new StrategyForProteinUpdate();
        this.strategy.enableIsoforms(false);
        this.strategy.setBasicBlastProcessRequired(false);
        this.context = new UpdateContext();
    }

    protected Query getProteinsWithoutUniprotXrefs(DataContext dataContext){
        // get all the intact entries without any uniprot cross reference or with uniprot cross reference with a qualifier different from 'identity' and which can only be uniprot-removed-ac
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
                "and xrefs.cvDatabase.ac = 'EBI-31' " +
                "and xrefs.cvXrefQualifier.shortLabel <> 'uniprot-removed-ac' )");

        return query;
    }

    protected Query getProteinsWithUniprotXrefsWithoutIdentity(DataContext dataContext){
        // get all the intact entries without any uniprot cross reference or with uniprot cross reference with a qualifier different from 'identity' and which can only be uniprot-removed-ac
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
                "and xrefs.cvDatabase.ac = 'EBI-31' " +
                "and xrefs.cvXrefQualifier.shortLabel = 'identity') " +
                "and p in ( " +
                "select p2 " +
                "from InteractorImpl p2 join p2.xrefs as xrefs " +
                "where p2.objClass = 'uk.ac.ebi.intact.model.ProteinImpl' " +
                "and xrefs.cvDatabase.ac = 'EBI-31' " +
                "and xrefs.cvXrefQualifier.shortLabel <> 'uniprot-removed-ac')");

        return query;
    }

    /**
     * This method query IntAct to get the list of protein to update and for each one create an updateContext
     * Write the results of the protein update process
     * @return the list of UpdateContext created from the protein to update
     * @throws ProteinUpdateException
     * @throws StrategyException
     */
    public void writeResultsOfProteinUpdate() throws ProteinUpdateException, StrategyException {
        // disable the update
        this.strategy.setUpdateEnabled(false);

        try {
            IntactContext intactContext = IntactContext.getCurrentInstance();

            File file = new File("updateReport_"+ Calendar.getInstance().getTime().getTime() +".txt");
            Writer writer = new FileWriter(file);

            // set the intact data context
            final DataContext dataContext = intactContext.getDataContext();
            TransactionStatus transactionStatus = dataContext.beginTransaction();

            // get all the intact entries without any uniprot cross reference or with uniprot cross reference with a qualifier different from 'identity' and which can only be uniprot-removed-ac
            final Query query = getProteinsWithoutUniprotXrefs(dataContext);

            proteinToUpdate = query.getResultList();
            log.info(proteinToUpdate.size());

            for (ProteinImpl prot : proteinToUpdate){
                this.context.clean();

                String accession = prot.getAc();
                Collection<InteractorXref> refs = prot.getXrefs();
                String sequence = prot.getSequence();
                BioSource organism = prot.getBioSource();

                context.setSequence(sequence);
                context.setOrganism(organism);
                context.setIntactAccession(accession);
                addIdentityCrossreferencesToContext(refs, context);

                log.info("protAc = " + accession);
                IdentificationResults result = this.strategy.identifyProtein(context);
                writeResultReports(accession, result, writer);

            }
            dataContext.commitTransaction(transactionStatus);
            writer.close();
        } catch (IntactTransactionException e) {
            throw new ProteinUpdateException(e);
        } catch (IOException e) {
            throw new ProteinUpdateException(e);
        }
    }

    /**
     *
     * @param qualifier : the qualifier of the cross reference
     * @return true if the qualifier is 'identity'
     */
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

    /**
     * Add all the cross references with qualifier 'identity' to the list of identifiers of the protein (intact cross references are ignored)
     * @param refs : the refs of the protein
     * @param context : the context of the protein
     */
    private void addIdentityCrossreferencesToContext(Collection<InteractorXref> refs, UpdateContext context){
        for (InteractorXref ref : refs){
            if (ref.getPrimaryId() != null){
                if (ref.getCvXrefQualifier() != null){
                    CvXrefQualifier qualifier = ref.getCvXrefQualifier();

                    if (isIdentityCrossReference(qualifier)){
                        CvDatabase database = ref.getCvDatabase();
                        if (database != null){
                            if (database.getIdentifier() != null && !CvDatabase.INTACT_MI_REF.equals(database.getIdentifier())){
                                context.addIdentifier(database.getIdentifier(), ref.getPrimaryId());
                            }
                            else if (database.getShortLabel() != null && !CvDatabase.INTACT.equals(database.getShortLabel())) {
                                context.addIdentifier(database.getShortLabel(), ref.getPrimaryId());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * get the annotation 'no-uniprot-update' if there is one in the list of annotations
     * @param annotations : the annotations of the protein
     * @return the annotation 'no-uniprot-update' if there is one in the list of annotations, null otherwise
     */
    private Annotation collectNo_Uniprot_UpdateAnnotation(Collection<Annotation> annotations){
        for (Annotation a : annotations){
            if (a.getCvTopic() != null){
                CvTopic topic = a.getCvTopic();

                if (topic.getShortLabel() != null){
                    if (topic.getShortLabel().equals(CvTopic.NON_UNIPROT)){
                        return a;
                    }
                }
            }
        }
        return null;
    }

    private Annotation collectObsoleteAnnotation(Collection<Annotation> annotations){
        String cautionMessage = "The sequence has been withdrawn from uniprot.";
        for (Annotation a : annotations){
            if (a.getCvTopic() != null){
                CvTopic topic = a.getCvTopic();

                if (a.getAnnotationText() != null){
                    if (a.getAnnotationText().equalsIgnoreCase(cautionMessage)){
                        if (topic.getIdentifier() != null){
                            if (topic.getIdentifier().equals(CvTopic.CAUTION_MI_REF)){
                                return a;
                            }
                        }
                        else if (topic.getShortLabel() != null){
                            if (topic.getShortLabel().equals(CvTopic.CAUTION)){
                                return a;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Create a new InteractorXref for the protein
     * @param uniprotAc : the uniprot accession
     * @return the InteractorXref with the uniprot ac and qualifier identity
     */
    private InteractorXref createIdentityInteractorXrefForUniprotAc(String uniprotAc){
        IntactContext intactContext = IntactContext.getCurrentInstance();

        if (uniprotAc == null){
            return null;
        }

        final CvDatabase uniprot = intactContext.getDaoFactory().getCvObjectDao(CvDatabase.class).getByPsiMiRef( CvDatabase.UNIPROT_MI_REF );
        final CvXrefQualifier identity = intactContext.getDaoFactory().getCvObjectDao(CvXrefQualifier.class).getByPsiMiRef(CvXrefQualifier.IDENTITY_MI_REF);

        InteractorXref xRef = new InteractorXref(intactContext.getInstitution(), uniprot, uniprotAc, identity);

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

    /**
     * add a new uniprot cross reference with qualifier identity to the list of cross references of the protein
     * @param prot : the protein
     * @param uniprotAc : the uniprot accession
     * @param factory : the Intact factory (not used)
     */
    private void addUniprotCrossReferenceTo(ProteinImpl prot, String uniprotAc, DaoFactory factory){
        InteractorXref ref = createIdentityInteractorXrefForUniprotAc(uniprotAc);

        if (ref != null){
            log.info("cross reference to uniprot "+ uniprotAc +" added to the cross references of " + prot.getAc());
            //factory.getXrefDao(InteractorXref.class).persist( ref );
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

    /**
     * Update the proteins with no uniprot cross references and the proteins with uniprot cross references set to 'uniprot-removed-ac'
     * @throws ProteinUpdateException
     */
    public void updateProteins() throws ProteinUpdateException {
        IntactContext intactContext = IntactContext.getCurrentInstance();

        // enable the update
        this.strategy.setUpdateEnabled(true);

        // get the data context
        final DataContext dataContext = intactContext.getDataContext();
        TransactionStatus transactionStatus = dataContext.beginTransaction();
        try {

            // create a new file where the results are stored in
            File file = new File("updateReport_"+ Calendar.getInstance().getTime().getTime() +".txt");

            Writer writer = new FileWriter(file);

            final DaoFactory daoFactory = dataContext.getDaoFactory();
            final Query query = getProteinsWithoutUniprotXrefs(dataContext);

            proteinToUpdate = query.getResultList();
            log.info(proteinToUpdate.size());

            ArrayList<String> accessionsToUpdate = new ArrayList<String>();

            for (ProteinImpl prot : proteinToUpdate){

                this.context.clean();
                String accession = prot.getAc();
                String shortLabel = prot.getShortLabel();
                log.info("Protein AC = " + accession + " shortLabel = " + shortLabel);

                Collection<InteractorXref> refs = prot.getXrefs();
                Collection<Annotation> annotations = prot.getAnnotations();
                String sequence = prot.getSequence();
                BioSource organism = prot.getBioSource();

                // context
                context.setSequence(sequence);
                context.setOrganism(organism);
                context.setIntactAccession(accession);
                addIdentityCrossreferencesToContext(refs, context);

                // result
                IdentificationResults result = this.strategy.identifyProtein(context);
                writeResultReports(accession, result, writer);

                // update
                if (result != null && result.getFinalUniprotId() != null){
                    Annotation a = collectNo_Uniprot_UpdateAnnotation(annotations);

                    if (a != null){
                        log.info("annotation no_uniprot_update removed from the annotations of " + accession);
                        prot.removeAnnotation(a);
                        daoFactory.getAnnotationDao().delete(a);
                    }

                    Annotation a2 = collectObsoleteAnnotation(annotations);

                    if (a2 != null){
                        log.info("caution removed from the annotations of " + accession);
                        prot.removeAnnotation(a2);
                        daoFactory.getAnnotationDao().delete(a2);
                    }
                    addUniprotCrossReferenceTo(prot, result.getFinalUniprotId(), daoFactory);
                    daoFactory.getProteinDao().update( prot );
                    accessionsToUpdate.add(accession);
                }
            }

            // commit the changes
            log.info("commit the change in the database.");
            log.info(accessionsToUpdate.size() + " proteins have been modified.");
            dataContext.commitTransaction(transactionStatus);
            writer.close();

            // update the database
            log.info("Processing the update of the proteins in Intact");
            //UpdateReportHandler reportHandler = new FileReportHandler(new File("target"));
            //ProteinUpdateProcessorConfig configUpdate = new ProteinUpdateProcessorConfig(reportHandler);

            //ProteinUpdateProcessor protUpdateProcessor = new ProteinUpdateProcessor(configUpdate);
            //ProteinUpdateProcessor protUpdateProcessor = new ProteinUpdateProcessor();
            //protUpdateProcessor.updateByACs(accessionsToUpdate);

        } catch (IntactTransactionException e) {
            throw new ProteinUpdateException(e);
        } catch (StrategyException e) {
            throw new ProteinUpdateException("There is a problem when executing the protein update strategy. Check the protein contexts.", e);
        } catch (IOException e) {
            throw new ProteinUpdateException("We can't write the results in a file.", e);
        } catch (Exception e){
            throw new ProteinUpdateException( e);
        }
    }

    /**
     * Write a report for the identification of the proteins without any uniprot cross references set to identity but another uniprot cross reference
     * @throws ProteinUpdateException
     */
    public void writeUpdateReportForProteinsWithUniprotCrossReferences() throws ProteinUpdateException {
        IntactContext intactContext = IntactContext.getCurrentInstance();

        // disable the update
        this.strategy.setUpdateEnabled(false);

        // create the data context
        final DataContext dataContext = intactContext.getDataContext();
        TransactionStatus transactionStatus = dataContext.beginTransaction();
        try {

            // create the file where to write the report
            File file = new File("updateReportForProteinWithUniprotCrossReferences_"+Calendar.getInstance().getTime().getTime()+".txt");
            Writer writer = new FileWriter(file);

            final Query query = getProteinsWithUniprotXrefsWithoutIdentity(dataContext);

            proteinToUpdate = query.getResultList();
            log.info(proteinToUpdate.size());

            ArrayList<String> accessionsToUpdate = new ArrayList<String>();

            for (ProteinImpl prot : proteinToUpdate){
                this.context.clean();
                String accession = prot.getAc();
                String shortLabel = prot.getShortLabel();
                log.info("Protein AC = " + accession + " shortLabel = " + shortLabel);

                Collection<InteractorXref> refs = prot.getXrefs();
                String sequence = prot.getSequence();
                BioSource organism = prot.getBioSource();

                // context
                context.setSequence(sequence);
                context.setOrganism(organism);
                context.setIntactAccession(accession);
                addIdentityCrossreferencesToContext(refs, context);

                // result
                IdentificationResults result = this.strategy.identifyProtein(context);
                writeResultReports(accession, result, writer);

                // update
                if (result != null && result.getFinalUniprotId() != null){
                    accessionsToUpdate.add(accession);
                }
            }
            log.info(accessionsToUpdate.size() + " proteins have been identified and could be updated.");
            dataContext.commitTransaction(transactionStatus);
            writer.close();

        } catch (IntactTransactionException e) {
            throw new ProteinUpdateException(e);
        } catch (StrategyException e) {
            throw new ProteinUpdateException("There is a problem when executing the protein update strategy. Check the protein contexts.", e);
        } catch (IOException e) {
            throw new ProteinUpdateException("We can't write the results in a file.", e);
        } catch (Exception e){
            throw new ProteinUpdateException( e);
        }
    }

    /**
     * write the results in a file
     * @param protAc : the intact accession
     * @param result : the result
     * @param writer : the file writer
     * @throws ProteinUpdateException
     */
    private void writeResultReports(String protAc, IdentificationResults<MappingReport> result, Writer writer) throws ProteinUpdateException {
        try {
            writer.write("************************" + protAc + "************************************ \n");

            writer.write("Uniprot accession found : " + result.getFinalUniprotId() + "\n");
            for (MappingReport report : result.getListOfActions()){
                writer.write(report.getName().toString() + " : " + report.getStatus().getLabel() + ", " + report.getStatus().getDescription() + "\n");

                for (String warn : report.getWarnings()){
                    writer.write(warn + "\n");
                }

                for (String ac : report.getPossibleAccessions()){
                    writer.write("possible accession : " + ac + "\n");
                }

                if (report instanceof DefaultPICRReport){
                    DefaultPICRReport picr = (DefaultPICRReport) report;
                    writer.write("Is a Swissprot entry : " + picr.isASwissprotEntry() + "\n");

                    for (PICRCrossReferences xrefs : picr.getCrossReferences()){
                        writer.write(xrefs.getDatabase() + " cross reference : " + xrefs.getAccessions() + "\n");
                    }
                }
                else if (report instanceof DefaultBlastReport){
                    DefaultBlastReport blast = (DefaultBlastReport) report;

                    for (BlastResults prot : blast.getBlastMatchingProteins()){
                        writer.write("BLAST Protein " + prot.getAccession() + " : identity = " + prot.getIdentity() + "\n");
                        writer.write("Query start = " + prot.getStartQuery() + ", end = " + prot.getEndQuery() + "\n");
                        writer.write("Match start = " + prot.getStartMatch() + ", end = " + prot.getEndMatch() + "\n");
                    }
                }
            }
            writer.flush();
        } catch (IOException e) {
            throw new ProteinUpdateException("We can't write the results of the protein " + protAc, e);
        }
    }
}
