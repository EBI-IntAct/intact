package uk.ac.ebi.intact.util.uniprotExport.exporters;

import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.util.uniprotExport.filters.config.FilterConfig;
import uk.ac.ebi.intact.util.uniprotExport.filters.config.FilterContext;

import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

/**
 * The class provides pre-formatted queries for extracting proteins from the database with specific criteria
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>22-Oct-2010</pre>
 */

public class QueryFactory {

    private final static String AUTHOR_SCORE = "author-score";
    private final String interactionInvolvedInComponents = "select distinct(c1.interaction.ac) from Component c1";

    private final String interactionsInvolvingInteractorsNoUniprotUpdate = "select distinct(i2.ac) from Component c2 join " +
            "c2.interaction as i2 join c2.interactor as p join p.annotations as a where a.cvTopic.shortLabel = :noUniprotUpdate";
    // negative interactions
    private final String negativeInteractions = "select distinct(i3.ac) from Component c3 join c3.interaction as i3 join " +
            "i3.annotations as a2 where a2.cvTopic.shortLabel = :negative";
    // interactors with an uniprot identity cross reference
    private final String interactorUniprotIdentity = "select distinct(p2.ac) from InteractorImpl p2 join p2.xrefs as refs where " +
            "refs.cvDatabase.identifier = :uniprot and refs.cvXrefQualifier.identifier = :identity";
    // interactors which are not a protein
    private final String nonProteinInteractor = "select distinct(p3.ac) from InteractorImpl p3 where p3.objClass <> :protein";
    // interactions with at least one interactor which either doesn't have any uniprot identity cross reference or is not a protein
    private final String interactionInvolvingNonUniprotOrNonProtein = "select distinct(i4.ac) from Component c4 join " +
            "c4.interaction as i4 join c4.interactor as p4 where p4.ac not in ("+interactorUniprotIdentity+") or p4.ac " +
            "in ("+nonProteinInteractor+")";

    private final String interactionsFromExperimentNoExport = "select distinct(i7.ac) from InteractionImpl i7 " +
            "join i7.experiments as e3 join e3.annotations as an3 where an3.cvTopic.shortLabel = :drExport " +
            "and trim(upper(an3.annotationText)) = :no";
    private final String interactionsFromExperimentExportYes = "select distinct(i8.ac) from InteractionImpl i8 " +
            "join i8.experiments as e4 join e4.annotations as an4 where an4.cvTopic.shortLabel = :drExport " +
            "and trim(upper(an4.annotationText)) = :yes";
    /*private final String interactionsFromExperimentExportConditional = "select distinct(i9.ac) from InteractionImpl i9 " +
            "join i9.annotations as an5 join i9.experiments as e5 join e5.annotations as an6 where " +
            "an6.cvTopic.shortLabel = :drExport and an5.cvTopic.identifier = :confidence and trim(upper(an5.annotationText)) = trim(upper(an6.annotationText))";*/
    private final String interactionsFromExperimentExportConditional = "select distinct(i9.ac) from InteractionImpl i9 " +
            "join i9.confidences as confidence join i9.experiments as e5 join e5.annotations as an6 where " +
            "an6.cvTopic.shortLabel = :drExport and confidence.cvConfidenceType.shortLabel = :confidence and trim(upper(confidence.value)) = trim(upper(an6.annotationText))";
    private final String interactionsFromExperimentExportSpecified = "select distinct(i10.ac) from InteractionImpl i10 " +
            "join i10.experiments as e6 join e6.annotations as an7 where an7.cvTopic.shortLabel = :drExport";

    private final String interactionsDrExportNotPassed = "select distinct(i11.ac) from InteractionImpl i11 " +
            "where i11.ac in (" + interactionsFromExperimentExportSpecified + ") " +
            "and i11.ac not in (" + interactionsFromExperimentExportYes + ") " +
            "and i11.ac not in ("+interactionsFromExperimentExportConditional+")";

    private final String interactionsAccepted = "select distinct(i2.ac) from Component c1 join c1.interaction as i2 join i2.experiments as e " +
            "join e.annotations as an where an.cvTopic.shortLabel = :accepted";
    // interactions with at least one experiment 'on-hold'
    private final String interactionsOnHold = "select distinct(i3.ac) from Component c2 join c2.interaction as i3 join i3.experiments" +
            " as e2 join e2.annotations as an2 where an2.cvTopic.shortLabel = :onhold";

    // select status of the different methods in IntAct
    private final String methodStatus = "select ci.identifier, a.annotationText from CvInteraction ci join ci.annotations as a join a.cvTopic as ct" +
            " where ct.shortLabel = :export";

    // select components with only one interactor (self interactions)
    private final String selfInteractions= "select distinct(il.ac) from InteractionImpl il join il.components as ct group by il.ac having count(ct.ac) == 1";

    public List<Object[]> getMethodStatusInIntact() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(methodStatus);
        query.setParameter("export", CvTopic.UNIPROT_DR_EXPORT);

        List<Object []> methods = query.getResultList();
        dataContext.commitTransaction(transactionStatus);

        return methods;
    }

    public List<String> getInteractionInvolvedInComponents() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(interactionInvolvedInComponents);

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    public List<String> getInteractionsInvolvingInteractorsNoUniprotUpdate() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(interactionsInvolvingInteractorsNoUniprotUpdate);
        query.setParameter("noUniprotUpdate", CvTopic.NON_UNIPROT);

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    public List<String> getNegativeInteractions() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(negativeInteractions);
        query.setParameter("negative", CvTopic.NEGATIVE);

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    public List<String> getInteractorUniprotIdentity() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(interactorUniprotIdentity);
        query.setParameter("uniprot", CvDatabase.UNIPROT_MI_REF);
        query.setParameter("identity", CvXrefQualifier.IDENTITY_MI_REF);

        List<String> interactors = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactors;
    }

    public List<String> getNonProteinInteractor() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(nonProteinInteractor);
        query.setParameter("protein", "uk.ac.ebi.intact.model.ProteinImpl");

        List<String> interactors = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactors;
    }

    public List<String> getInteractionInvolvingNonUniprotOrNonProtein() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(interactionInvolvingNonUniprotOrNonProtein);
        query.setParameter("uniprot", CvDatabase.UNIPROT_MI_REF);
        query.setParameter("identity", CvXrefQualifier.IDENTITY_MI_REF);
        query.setParameter("protein", "uk.ac.ebi.intact.model.ProteinImpl");

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    public List<String> getInteractionsFromExperimentNoExport() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(interactionsFromExperimentNoExport);
        query.setParameter("drExport", CvTopic.UNIPROT_DR_EXPORT);
        query.setParameter("no", "NO");

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    public List<String> getInteractionsFromExperimentExportYes() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(interactionsFromExperimentExportYes);
        query.setParameter("drExport", CvTopic.UNIPROT_DR_EXPORT);
        query.setParameter("yes", "YES");

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    public List<String> getInteractionsFromExperimentExportConditional() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(interactionsFromExperimentExportConditional);
        query.setParameter("drExport", CvTopic.UNIPROT_DR_EXPORT);
        query.setParameter("confidence", AUTHOR_SCORE);

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    public List<String> getInteractionsFromExperimentExportSpecified() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(interactionsFromExperimentExportSpecified);
        query.setParameter("drExport", CvTopic.UNIPROT_DR_EXPORT);

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    public List<String> getInteractionsDrExportNotPassed() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(interactionsDrExportNotPassed);
        query.setParameter("drExport", CvTopic.UNIPROT_DR_EXPORT);
        query.setParameter("yes", "YES");
        query.setParameter("confidence", AUTHOR_SCORE);

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    public List<String> getInteractionsAccepted() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(interactionsAccepted);
        query.setParameter("accepted", CvTopic.ACCEPTED);
        //query.setParameter("september2005", "01/09/2005");
        //query.setParameter("dateFormat", "dd/mm/yyyy");

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    public List<String> getInteractionsOnHold() {
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(interactionsOnHold);
        query.setParameter("onhold", CvTopic.ON_HOLD);

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    /**
     *
     * @return the list of interaction accessions which are involving only uniprot proteins, are not negative and
     * which passed the dr-export annotation at the level of the experiment
     */
    public List<String> getInteractionAcsToBeProcessedForUniprotExport(){
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        String queryString = "select distinct(i.ac) from InteractionImpl i where i.ac in ("+interactionInvolvedInComponents + ") " +
                "and i.ac not in ("+interactionsInvolvingInteractorsNoUniprotUpdate+") and i.ac not in ("+negativeInteractions+") " +
                "and i.ac not in ("+interactionInvolvingNonUniprotOrNonProtein+") " +
                "and i.ac not in (" + interactionsDrExportNotPassed + ") " +
                "and i.ac not in (" + interactionsFromExperimentNoExport + ")";
        // we want all the interactions which :
        // no participant has a 'no-uniprot-update' annotation
        // the interaction doesn't have any 'negative' annotation
        // the participants have a uniprot 'identity' cross reference
        // the participants are proteins
        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(queryString);

        query.setParameter("noUniprotUpdate", CvTopic.NON_UNIPROT);
        query.setParameter("drExport", CvTopic.UNIPROT_DR_EXPORT);
        query.setParameter("no", "NO");
        query.setParameter("yes", "YES");
        query.setParameter("confidence", AUTHOR_SCORE);
        query.setParameter("negative", CvTopic.NEGATIVE);
        query.setParameter("uniprot", CvDatabase.UNIPROT_MI_REF);
        query.setParameter("identity", CvXrefQualifier.IDENTITY_MI_REF);
        query.setParameter("protein", "uk.ac.ebi.intact.model.ProteinImpl");

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    /**
     *
     * @return the list of interaction accessions which are publicly released, which are involving only uniprot proteins, are not negative and
     * which passed the dr-export annotation at the level of the experiment
     */
    public List<String> getInteractionAcsFromReleasedExperimentsToBeProcessedForUniprotExport(){
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        String queryString = "select distinct(i.ac) from InteractionImpl i " +
                "where i.ac in ("+interactionsAccepted + ") " +
                "and i.ac not in ("+interactionsOnHold+") " +
                "and i.ac not in (" + interactionsDrExportNotPassed + ") " +
                "and i.ac not in (" + interactionsFromExperimentNoExport + ") " +
                "and i.ac not in ("+interactionsInvolvingInteractorsNoUniprotUpdate+") " +
                "and i.ac not in ("+negativeInteractions+") " +
                "and i.ac not in ("+interactionInvolvingNonUniprotOrNonProtein+")";

        // we want all the interactions which :
        // no participant has a 'no-uniprot-update' annotation
        // the interaction doesn't have any 'negative' annotation
        // the participants have a uniprot 'identity' cross reference
        // the participants are proteins
        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(queryString);

        query.setParameter("accepted", CvTopic.ACCEPTED);
        //query.setParameter("september2005", "01/09/2005");
        //query.setParameter("dateFormat", "dd/mm/yyyy");
        query.setParameter("onhold", CvTopic.ON_HOLD);
        query.setParameter("drExport", CvTopic.UNIPROT_DR_EXPORT);
        query.setParameter("no", "NO");
        query.setParameter("yes", "YES");
        query.setParameter("confidence", AUTHOR_SCORE);
        query.setParameter("noUniprotUpdate", CvTopic.NON_UNIPROT);
        query.setParameter("negative", CvTopic.NEGATIVE);
        query.setParameter("uniprot", CvDatabase.UNIPROT_MI_REF);
        query.setParameter("identity", CvXrefQualifier.IDENTITY_MI_REF);
        query.setParameter("protein", "uk.ac.ebi.intact.model.ProteinImpl");

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    /**
     *
     * @return the list of interaction accessions which are publicly released and pass the filtering options set in the FilterConfig
     */
    public List<String> getReleasedInteractionAcsPassingFilters(){
        FilterConfig config = FilterContext.getInstance().getConfig();
        boolean excludeSpokeExpanded = config.excludeSpokeExpandedInteractions();
        boolean excludeLowConfidenceInteractions = config.excludeLowConfidenceInteractions();
        boolean excludeNonUniprotInteractors = config.excludeNonUniprotInteractors();

        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        StringBuffer queryString = new StringBuffer();
        queryString.append("select distinct(i.ac) from InteractionImpl i ");
        queryString.append("where i.ac in (");
        queryString.append(interactionsAccepted);
        queryString.append(") and i.ac not in (");
        queryString.append(interactionsOnHold);
        queryString.append(") ");

        // negative interactions will be processed differently and are not in the same list
        queryString.append("and i.ac not in (");
        queryString.append(negativeInteractions);
        queryString.append(") ");

        if (excludeLowConfidenceInteractions){
            queryString.append("and i.ac not in (");
            queryString.append(interactionsDrExportNotPassed);
            queryString.append(") and i.ac not in (");
            queryString.append(interactionsFromExperimentNoExport);
            queryString.append(") ");
        }

        if (excludeSpokeExpanded && excludeNonUniprotInteractors){
            queryString.append("and i.ac not in (");
            queryString.append(interactionsInvolvingInteractorsNoUniprotUpdate);
            queryString.append(") and i.ac not in (");
            queryString.append(interactionInvolvingNonUniprotOrNonProtein);
            queryString.append(") ");
        }

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(queryString.toString());

        query.setParameter("accepted", CvTopic.ACCEPTED);
        query.setParameter("onhold", CvTopic.ON_HOLD);
        query.setParameter("negative", CvTopic.NEGATIVE);

        if (excludeLowConfidenceInteractions){
            query.setParameter("drExport", CvTopic.UNIPROT_DR_EXPORT);
            query.setParameter("no", "NO");
            query.setParameter("yes", "YES");
            query.setParameter("confidence", AUTHOR_SCORE);
        }

        if (excludeSpokeExpanded && excludeNonUniprotInteractors){
            query.setParameter("noUniprotUpdate", CvTopic.NON_UNIPROT);
            query.setParameter("uniprot", CvDatabase.UNIPROT_MI_REF);
            query.setParameter("identity", CvXrefQualifier.IDENTITY_MI_REF);
            query.setParameter("protein", "uk.ac.ebi.intact.model.ProteinImpl");
        }


        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    public List<String> getNegativeInteractionsPassingFilter() {
        FilterConfig config = FilterContext.getInstance().getConfig();
        boolean excludeSpokeExpanded = config.excludeSpokeExpandedInteractions();
        boolean excludeNegativeInteractions = config.excludeNegativeInteractions();
        boolean excludeLowConfidenceInteractions = config.excludeLowConfidenceInteractions();
        boolean excludeNonUniprotInteractors = config.excludeNonUniprotInteractors();

        if (!excludeNegativeInteractions){
            DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

            TransactionStatus transactionStatus = dataContext.beginTransaction();

            StringBuffer queryString = new StringBuffer();
            queryString.append("select distinct i.ac from InteractionImpl i ");
            queryString.append("where i.ac in (");
            queryString.append(interactionsAccepted);
            queryString.append(") and i.ac not in (");
            queryString.append(interactionsOnHold);
            queryString.append(") and i.ac in (");
            queryString.append(negativeInteractions);
            queryString.append(") ");

            if (excludeLowConfidenceInteractions){
                queryString.append("and i.ac not in (");
                queryString.append(interactionsDrExportNotPassed);
                queryString.append(") and i.ac not in (");
                queryString.append(interactionsFromExperimentNoExport);
                queryString.append(") ");
            }

            if (excludeSpokeExpanded && excludeNonUniprotInteractors){
                queryString.append("and i.ac not in (");
                queryString.append(interactionsInvolvingInteractorsNoUniprotUpdate);
                queryString.append(") and i.ac not in (");
                queryString.append(interactionInvolvingNonUniprotOrNonProtein);
                queryString.append(") ");
            }

            Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(queryString.toString());

            query.setParameter("accepted", CvTopic.ACCEPTED);
            query.setParameter("onhold", CvTopic.ON_HOLD);
            query.setParameter("negative", CvTopic.NEGATIVE);

            if (excludeLowConfidenceInteractions){
                query.setParameter("drExport", CvTopic.UNIPROT_DR_EXPORT);
                query.setParameter("no", "NO");
                query.setParameter("yes", "YES");
                query.setParameter("confidence", AUTHOR_SCORE);
            }

            if (excludeSpokeExpanded && excludeNonUniprotInteractors){
                query.setParameter("noUniprotUpdate", CvTopic.NON_UNIPROT);
                query.setParameter("uniprot", CvDatabase.UNIPROT_MI_REF);
                query.setParameter("identity", CvXrefQualifier.IDENTITY_MI_REF);
                query.setParameter("protein", "uk.ac.ebi.intact.model.ProteinImpl");
            }

            List<String> interactions = query.getResultList();

            dataContext.commitTransaction(transactionStatus);

            return interactions;
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }

    public List<String> getReleasedSelfInteractionAcsPassingFilters(){
        FilterConfig config = FilterContext.getInstance().getConfig();
        boolean excludeSpokeExpanded = config.excludeSpokeExpandedInteractions();
        boolean excludeNegativeInteractions = config.excludeNegativeInteractions();
        boolean excludeLowConfidenceInteractions = config.excludeLowConfidenceInteractions();
        boolean excludeNonUniprotInteractors = config.excludeNonUniprotInteractors();

        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        StringBuffer queryString = new StringBuffer();
        queryString.append("select distinct(i.ac) from InteractionImpl i ");
        queryString.append("where i.ac in (");
        queryString.append(interactionsAccepted);
        queryString.append(") and i.ac not in (");
        queryString.append(interactionsOnHold);
        queryString.append(") ");
        queryString.append(") and i.ac in (");
        queryString.append(selfInteractions);
        queryString.append(") ");

        if (excludeLowConfidenceInteractions){
            queryString.append("and i.ac not in (");
            queryString.append(interactionsDrExportNotPassed);
            queryString.append(") and i.ac not in (");
            queryString.append(interactionsFromExperimentNoExport);
            queryString.append(") ");
        }

        if (excludeNegativeInteractions){
            queryString.append("and i.ac not in (");
            queryString.append(negativeInteractions);
            queryString.append(") ");
        }

        if (excludeSpokeExpanded && excludeNonUniprotInteractors){
            queryString.append("and i.ac not in (");
            queryString.append(interactionsInvolvingInteractorsNoUniprotUpdate);
            queryString.append(") and i.ac not in (");
            queryString.append(interactionInvolvingNonUniprotOrNonProtein);
            queryString.append(") ");
        }

        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(queryString.toString());

        query.setParameter("accepted", CvTopic.ACCEPTED);
        query.setParameter("onhold", CvTopic.ON_HOLD);

        if (excludeLowConfidenceInteractions){
            query.setParameter("drExport", CvTopic.UNIPROT_DR_EXPORT);
            query.setParameter("no", "NO");
            query.setParameter("yes", "YES");
            query.setParameter("confidence", AUTHOR_SCORE);
        }

        if (excludeNegativeInteractions){
            query.setParameter("negative", CvTopic.NEGATIVE);
        }

        if (excludeSpokeExpanded && excludeNonUniprotInteractors){
            query.setParameter("noUniprotUpdate", CvTopic.NON_UNIPROT);
            query.setParameter("uniprot", CvDatabase.UNIPROT_MI_REF);
            query.setParameter("identity", CvXrefQualifier.IDENTITY_MI_REF);
            query.setParameter("protein", "uk.ac.ebi.intact.model.ProteinImpl");
        }


        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }


    /**
     *
     * @return the list of interaction accessions which are publicly released, which are involving only uniprot proteins, are not negative and
     * which passed the dr-export annotation at the level of the experiment
     */
    public List<String> getInteractionAcsFromReleasedExperimentsContainingNoUniprotProteinsToBeProcessedForUniprotExport(){
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        String queryString = "select distinct(i.ac) from InteractionImpl i " +
                "where i.ac in ("+interactionsAccepted + ") " +
                "and i.ac not in ("+interactionsOnHold+") " +
                "and i.ac not in (" + interactionsDrExportNotPassed + ") " +
                "and i.ac not in (" + interactionsFromExperimentNoExport + ") " +
                "and i.ac not in ("+negativeInteractions+") ";

        // we want all the interactions which :
        // no participant has a 'no-uniprot-update' annotation
        // the interaction doesn't have any 'negative' annotation
        // the participants have a uniprot 'identity' cross reference
        // the participants are proteins
        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(queryString);

        query.setParameter("accepted", CvTopic.ACCEPTED);
        //query.setParameter("september2005", "01/09/2005");
        //query.setParameter("dateFormat", "dd/mm/yyyy");
        query.setParameter("onhold", CvTopic.ON_HOLD);
        query.setParameter("drExport", CvTopic.UNIPROT_DR_EXPORT);
        query.setParameter("no", "NO");
        query.setParameter("yes", "YES");
        query.setParameter("confidence", AUTHOR_SCORE);
        query.setParameter("negative", CvTopic.NEGATIVE);

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

    /**
     *
     * @return the list of interaction accessions which are involving only uniprot proteins, are not negative and
     * which are publicly released
     */
    public List<String> getInteractionAcsFromReleasedExperimentsNoFilterDrExport(){
        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        TransactionStatus transactionStatus = dataContext.beginTransaction();

        String queryString = "select distinct(i.ac) from InteractionImpl i " +
                "where i.ac in ("+interactionsAccepted + ") " +
                "and i.ac not in ("+interactionsOnHold+") " +
                "and i.ac not in ("+interactionsInvolvingInteractorsNoUniprotUpdate+") " +
                "and i.ac not in ("+negativeInteractions+") " +
                "and i.ac not in ("+interactionInvolvingNonUniprotOrNonProtein+")";

        // we want all the interactions which :
        // no participant has a 'no-uniprot-update' annotation
        // the interaction doesn't have any 'negative' annotation
        // the participants have a uniprot 'identity' cross reference
        // the participants are proteins
        Query query = IntactContext.getCurrentInstance().getDaoFactory().getEntityManager().createQuery(queryString);

        query.setParameter("accepted", CvTopic.ACCEPTED);
        //query.setParameter("september2005", "01/09/2005");
        //query.setParameter("dateFormat", "dd/mm/yyyy");
        query.setParameter("onhold", CvTopic.ON_HOLD);
        query.setParameter("noUniprotUpdate", CvTopic.NON_UNIPROT);
        query.setParameter("negative", CvTopic.NEGATIVE);
        query.setParameter("uniprot", CvDatabase.UNIPROT_MI_REF);
        query.setParameter("identity", CvXrefQualifier.IDENTITY_MI_REF);
        query.setParameter("protein", "uk.ac.ebi.intact.model.ProteinImpl");

        List<String> interactions = query.getResultList();

        dataContext.commitTransaction(transactionStatus);

        return interactions;
    }

}
