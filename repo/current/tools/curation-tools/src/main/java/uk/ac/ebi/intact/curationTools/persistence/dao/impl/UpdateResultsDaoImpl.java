package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.results.UpdateResults;
import uk.ac.ebi.intact.curationTools.persistence.dao.UpdateResultsDao;

import javax.persistence.Query;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-May-2010</pre>
 */
@Repository
@Transactional(readOnly = true)
public class UpdateResultsDaoImpl extends UpdateBaseDaoImpl<UpdateResults> implements UpdateResultsDao {
    public UpdateResultsDaoImpl() {
        super(UpdateResults.class);
    }

    public UpdateResults getUpdateResultsWithId(long id) {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u where u.id = :id" );
        query.setParameter( "id", id);

        if (query.getResultList().isEmpty()){
            return null;
        }
        return (UpdateResults) query.getResultList().iterator().next();
    }

    public UpdateResults getUpdateResultsForProteinAc(String proteinAc) {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u where u.intactAccession = :proteinAc" );
        query.setParameter( "proteinAc", proteinAc);

        if (query.getResultList().isEmpty()){
            return null;
        }
        return (UpdateResults) query.getResultList().iterator().next();
    }

    public List<ActionReport> getActionReportsWithWarningsByProteinAc(String proteinAc) {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u join u.listOfActions as a join a.warnings as warn where u.intactAccession = :protac" );
        query.setParameter( "protac", proteinAc);

        return query.getResultList();
    }

    public List<UpdateResults> getResultsContainingAction(ActionName name) {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u join u.listOfActions as a where a.name = :name" );
        query.setParameter( "name", name);

        return query.getResultList();
    }

    public List<UpdateResults> getResultsContainingActionWithLabel(StatusLabel label) {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u join u.listOfActions as a where a.statusLabel = :status" );
        query.setParameter( "status", label.toString());

        return query.getResultList();
    }

    public List<UpdateResults> getUpdateResultsWithSwissprotRemapping() {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u join u.listOfActions as a where a.name = :name" );
        query.setParameter( "name", ActionName.BLAST_Swissprot_Remapping);

        return query.getResultList();
    }

    public List<UpdateResults> getSuccessfulUpdateResults() {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u where u.finalUniprotId <> null" );

        return query.getResultList();
    }

    public List<UpdateResults> getUpdateResultsToBeReviewedByACurator() {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u join u.listOfActions as a where a.statusLabel = :status" );
        query.setParameter( "status", StatusLabel.TO_BE_REVIEWED.toString());

        return query.getResultList();
    }

    public List<UpdateResults> getProteinNotUpdatedBecauseNoSequenceAndNoIdentityXrefs() {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u join u.listOfActions as a where a.statusLabel = :status and a.name = :name" );
        query.setParameter( "status", StatusLabel.FAILED.toString());
        query.setParameter( "name", ActionName.update_Checking);

        return query.getResultList();
    }

    public List<UpdateResults> getUnsuccessfulUpdateResults() {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u join u.listOfActions as a where u.finalUniprotId = null and a.statusLabel = :status and a.name <> :name" );
        query.setParameter( "status", StatusLabel.FAILED.toString());
        query.setParameter( "name", ActionName.update_Checking);

        return query.getResultList();
    }

    public List<UpdateResults> getUpdateResultsWithConflictBetweenSequenceAndIdentityXRefs() {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u join u.listOfActions as a where a.statusLabel = :status and a.name = :name" );
        query.setParameter( "status", StatusLabel.TO_BE_REVIEWED.toString());
        query.setParameter( "name", ActionName.update_Checking);

        return query.getResultList();
    }

    public List<UpdateResults> getUpdateResultsWithConflictBetweenSwissprotSequenceAndFeatureRanges() {
        final Query query = getEntityManager().createQuery( "select u from UpdateResults as u join u.listOfActions as a where a.statusLabel = :status and a.name = :name" );
        query.setParameter( "status", StatusLabel.TO_BE_REVIEWED.toString());
        query.setParameter( "name", ActionName.feature_range_checking);

        return query.getResultList();
    }
}
